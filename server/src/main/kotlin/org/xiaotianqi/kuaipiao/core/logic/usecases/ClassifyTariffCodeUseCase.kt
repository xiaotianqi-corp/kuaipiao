package org.xiaotianqi.kuaipiao.core.logic.usecases

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.config.ai.OpenAIConfig
import org.xiaotianqi.kuaipiao.core.clients.AiClientManager
import org.xiaotianqi.kuaipiao.core.exceptions.AiValidationException
import org.xiaotianqi.kuaipiao.core.logic.ai.CountryRuleEngine
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai.AiCacheSource
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.core.clients.ai.AiClientFactory
import org.xiaotianqi.kuaipiao.core.logic.usecases.validators.validateClassification
import org.xiaotianqi.kuaipiao.data.mappers.TariffClassificationMapper
import org.xiaotianqi.kuaipiao.domain.classification.AlternativeClassification
import org.xiaotianqi.kuaipiao.domain.product.ProductBatchClassificationResult
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationError
import org.xiaotianqi.kuaipiao.domain.product.ProductDescription
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationSummary
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import kotlin.Int
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.models.ModelResult
import org.xiaotianqi.kuaipiao.domain.trade.TariffRestrictions

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class ClassifyTariffCodeUseCase(
    private val aiClientManager: AiClientManager,
    private val countryRuleEngine: CountryRuleEngine,
    private val aiDBI: AiDBI,
    private val aiCacheSource: AiCacheSource,
    private val httpClient: HttpClient,
    private val openAIConfig: OpenAIConfig
) {

    suspend operator fun invoke(
        productDescription: String,
        countryOrigin: String,
        countryDestination: String,
        userId: String,
        companyId: String
    ): Result<TariffClassification> {

        logger.info { "Classifying tariff heading for: $productDescription" }

        return try {
            if (productDescription.isBlank()) {
                return Result.failure(IllegalArgumentException("Product description cannot be empty"))
            }

            val countryRules = countryRuleEngine.getTradeRules(countryOrigin, countryDestination)
            if (!countryRules.isTradeAllowed) {
                return Result.failure(IllegalStateException("Trade not permitted between $countryOrigin y $countryDestination"))
            }

            val classification = aiClientManager.classifyTariffCode(
                productDescription,
                countryOrigin,
                countryDestination
            )

            if (classification.confidence < 0.6) {
                logger.warn { "Low confidence in tariff classification: ${classification.confidence}" }
            }

            val restrictions = countryRuleEngine.validateTariffRestrictions(
                classification.tariffCode,
                countryOrigin,
                countryDestination
            )

            if (restrictions.isRestricted) {
                classification.restrictions = restrictions
                classification.riskLevel = RiskLevel.HIGH
            }

            aiDBI.saveModelResult(
                ModelResult(
                    modelType = "TARIFF_CLASSIFICATION",
                    aiProvider = "openai",
                    operation = "classify_tariff",
                    inputHash = "$productDescription$countryOrigin$countryDestination".hashCode().toString(),
                    inputData = productDescription,
                    outputData = classification.tariffCode,
                    confidence = classification.confidence,
                    processingTime = 0
                )
            )

            logger.info {
                "Classification completed: ${classification.tariffCode}" +
                        "(confidence: ${classification.confidence})"
            }

            Result.success(classification)

        } catch (e: Exception) {
            logger.error(e) { "Error in tariff classification" }
            Result.failure(
                AiValidationException("Error classifying tariff heading: ${e.message}")
            )
        }
    }

    suspend fun classifySingleProduct(
        product: ProductDescription,
        countryOrigin: String,
        countryDestination: String
    ): ProductClassificationData {

        val start = System.currentTimeMillis()
        val productHash = product.name.hashCode().toString() + product.description.hashCode().toString()

        val cached = aiCacheSource.getTariffClassification(
            productHash = productHash,
            origin = countryOrigin,
            destination = countryDestination
        )

        if (cached != null) {
            return ProductClassificationData(
                productName = product.name,
                tariffCode = cached.tariffCode,
                suggestedCategory = cached.suggestedCategory,
                accountingAccount = cached.accountingAccount,
                taxCategory = cached.taxCategory,
                productCode = cached.productCode,
                confidence = cached.confidence,
                alternatives = cached.alternatives,
                validationMessage = "Classification obtained from cache",
                countrySpecific = mapOf(
                    "origin" to countryOrigin,
                    "destination" to countryDestination
                )
            )
        }

        val classificationInput = ClassificationInput(
            text = buildString {
                appendLine(product.name)
                appendLine(product.description)
                product.attributes.forEach { (k, v) -> appendLine("$k: $v") }
            },
            context = mapOf(
                "country_origin" to countryOrigin,
                "country_destination" to countryDestination
            ),
            categories = listOf("customs", "hs_code", "accounting", "tax")
        )

        val aiClassificationClient = AiClientFactory.createClassifier(
            provider = AiClientFactory.Provider.OPENAI,
            httpClient = httpClient,
            configs = mapOf(AiClientFactory.Provider.OPENAI to openAIConfig)
        )

        val result: ClassificationResult = aiClassificationClient.classify(input = classificationInput)
        val top = result.topPrediction

        val countrySpecific = mutableMapOf<String, String>(
            "origin" to countryOrigin,
            "destination" to countryDestination,
            "restriction" to top.metadata["restriction"].orEmpty(),
            "quotaType" to top.metadata["quotaType"].orEmpty(),
            "quotaLimit" to top.metadata["quotaLimit"].orEmpty(),
            "quotaUsed" to top.metadata["quotaUsed"].orEmpty(),
            "quotaUnit" to top.metadata["quotaUnit"].orEmpty(),
            "quotaPeriod" to top.metadata["quotaPeriod"].orEmpty(),
            "requiredDocuments" to top.metadata["requiredDocuments"].orEmpty(),
            "taxRate" to top.metadata["taxRate"].orEmpty(),
            "embargoCountries" to top.metadata["embargoCountries"].orEmpty()
        )

        val productClassification = ProductClassificationData(
            productName = product.name,
            tariffCode = top.tariffCode ?: "000000",
            suggestedCategory = top.category ?: "unknown",
            accountingAccount = top.accountingAccount ?: "0000",
            taxCategory = top.taxCategory ?: "general",
            productCode = top.productCode ?: Uuid.random().toString(),
            confidence = top.confidence,
            alternatives = result.predictions.drop(1).map {
                AlternativeClassification(
                    tariffCode = it.tariffCode ?: "000000",
                    description = it.label ?: "",
                    confidence = it.confidence,
                    reason = "Alternative prediction of the model"
                )
            },
            validationMessage = validateClassification(top),
            countrySpecific = countrySpecific
        )

        val tariffClassification = TariffClassificationMapper.mapToTariffClassification(
            product = productClassification,
            countryOrigin = countryOrigin,
            countryDestination = countryDestination
        ).apply {
            if (tariffCode.isEmpty()) tariffCode = "000000"
            if (suggestedCategory.isEmpty()) suggestedCategory = "unknown"
            if (accountingAccount.isEmpty()) accountingAccount = "0000"
            if (taxCategory.isEmpty()) taxCategory = "general"
            if (productCode.isEmpty()) productCode = Uuid.random().toString()
            if (requiredDocuments.isEmpty()) requiredDocuments = emptyList()
            if (restrictions == null) restrictions = TariffRestrictions(
                isRestricted = false,
                requiresLicense = false,
                quotaLimits = null,
                embargoCountries = emptyList(),
                specialRequirements = emptyList()
            )
        }

        aiCacheSource.setTariffClassification(
            productHash = productHash,
            origin = countryOrigin,
            destination = countryDestination,
            classification = tariffClassification,
            aiProvider = result.metadata.aiProvider,
            processingTime = System.currentTimeMillis() - start
        )

        return productClassification
    }

    sealed class ProductClassificationResult {
        data class Success(val data: ProductClassificationData) : ProductClassificationResult()
        data class Failure(val error: ProductClassificationError) : ProductClassificationResult()
    }

    suspend fun classifyBatch(
        products: List<ProductDescription>,
        countryOrigin: String,
        countryDestination: String,
        maxConcurrency: Int = 5
    ): ProductBatchClassificationResult = coroutineScope {

        val start = System.currentTimeMillis()
        val semaphore = Semaphore(maxConcurrency)

        val deferredResults = products.mapIndexed { index, product ->
            async {
                semaphore.withPermit {
                    try {
                        val classification = classifySingleProduct(
                            product,
                            countryOrigin,
                            countryDestination
                        )
                        ProductClassificationResult.Success(classification)
                    } catch (e: Exception) {
                        ProductClassificationResult.Failure(
                            ProductClassificationError(
                                productIndex = index,
                                productName = product.name,
                                error = e.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }
        }

        val results = deferredResults.awaitAll()

        val successful = results
            .filterIsInstance<ProductClassificationResult.Success>()
            .map { it.data }

        val failed = results
            .filterIsInstance<ProductClassificationResult.Failure>()
            .map { it.error }

        val avgConfidence = if (successful.isNotEmpty())
            successful.map { it.confidence }.average()
        else 0.0

        val categories = successful.groupingBy { it.taxCategory }.eachCount()

        ProductBatchClassificationResult(
            successful = successful,
            failed = failed,
            summary = ProductClassificationSummary(
                totalProducts = products.size,
                classified = successful.size,
                failed = failed.size,
                categories = categories,
                averageConfidence = avgConfidence,
                categoryDistribution = categories,
                processingTime = System.currentTimeMillis() - start
            )
        )
    }
    private fun inferRiskLevel(confidence: Double) = when {
        confidence >= 0.85 -> RiskLevel.LOW
        confidence >= 0.60 -> RiskLevel.MEDIUM
        else -> RiskLevel.HIGH
    }
}