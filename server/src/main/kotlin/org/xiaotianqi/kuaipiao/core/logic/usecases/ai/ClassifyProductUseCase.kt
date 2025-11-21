package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import org.xiaotianqi.kuaipiao.core.clients.AiClientManager
import org.xiaotianqi.kuaipiao.core.logic.ai.CountryRuleEngine
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.classification.AlternativeClassification
import org.xiaotianqi.kuaipiao.domain.product.ProductBatchClassificationResult
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationError
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationSummary
import org.xiaotianqi.kuaipiao.domain.product.ProductInfo
import org.xiaotianqi.kuaipiao.domain.trade.TradeRules
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
class ClassifyProductUseCase(
    private val aiClientManager: AiClientManager,
    private val countryRuleEngine: CountryRuleEngine,
    private val aiDBI: AiDBI
) {

    suspend operator fun invoke(
        productName: String,
        description: String?,
        category: String?,
        userId: String,
        companyId: String,
        country: String = "EC"
    ): Result<ProductClassificationData> {

        logger.info { "Clasificando producto: $productName" }

        return try {
            // Validar entrada
            if (productName.isBlank()) {
                return Result.failure(IllegalArgumentException("Nombre del producto no puede estar vacío"))
            }

            // Obtener reglas del país
            val countryRules = countryRuleEngine.getTradeRules(country, country)

            // Clasificar producto
            val classification = classifyProductInternal(
                productName = productName,
                description = description,
                category = category,
                country = country
            )

            val validatedClassification = applyCountryRules(
                classification = classification,
                countryRules = countryRules,
                country = country
            )

            aiDBI.saveProductClassification(
                userId = userId,
                companyId = companyId,
                classification = validatedClassification
            )

            logger.info {
                "Classified product: $productName -> ${validatedClassification.suggestedCategory} " +
                        "(trust: ${validatedClassification.confidence})"
            }

            Result.success(validatedClassification)

        } catch (e: Exception) {
            logger.error(e) { "Error classifying product: $productName" }
            Result.failure(e)
        }
    }

    suspend fun classifyProductBatch(
        products: List<ProductInfo>,
        userId: String,
        companyId: String,
        country: String
    ): ProductBatchClassificationResult {

        val classifications = mutableListOf<ProductClassificationData>()
        val errors = mutableListOf<ProductClassificationError>()
        val analysisStartTime: Long = Clock.System.now().toEpochMilliseconds()
        val endTime = Clock.System.now().toEpochMilliseconds()
        val processingTime = endTime - analysisStartTime

        val averageConfidence = if (classifications.isNotEmpty())
            classifications.map { it.confidence }.average()
        else 0.0

        val categoryDistribution = classifications
            .groupBy { it.suggestedCategory }
            .mapValues { it.value.size }

        products.forEachIndexed { index, product ->
            try {
                val classification = invoke(
                    productName = product.name,
                    description = product.description,
                    category = product.category,
                    userId = userId,
                    companyId = companyId,
                    country = country
                ).getOrThrow()

                classifications.add(classification)
            } catch (e: Exception) {
                errors.add(ProductClassificationError(
                    productIndex = index,
                    productName = product.name,
                    error = e.message ?: "Unknown error"
                ))
            }
        }

        return ProductBatchClassificationResult(
            successful = classifications,
            failed = errors,
            summary = ProductClassificationSummary(
                totalProducts = products.size,
                classified = classifications.size,
                failed = errors.size,
                categories = categoryDistribution,
                averageConfidence = averageConfidence,
                categoryDistribution = categoryDistribution,
                processingTime = processingTime
            )
        )
    }

    private suspend fun classifyProductInternal(
        productName: String,
        description: String?,
        category: String?,
        country: String
    ): ProductClassificationData {

        val prompt = """            
            Classifies the product for an ERP billing system in `$country`.
            Product: `$productName`
            Description: `${description?: "Not Available"}`
            Suggested Category: `${category?: "Not Specified"}`
            Provides:
                1. Primary Category
                2. Accounting Account Code
                3. Tax Category
                4. Standard Product Code
            Responds in JSON:
            {
                "suggestedCategory": "string",
                "accountingAccount": "string",
                "taxCategory": "string",
                "productCode": "string",
                "confidence": number,
                "alternatives": ["string"]
            }
        """.trimIndent()

        val result = aiClientManager.executeWithFallback(
            operation = "product_classification_$country",
            primary = {
                ProductClassificationData(
                    productName = productName,
                    tariffCode = "000000",
                    suggestedCategory = "Electronics",
                    accountingAccount = "4010101",
                    taxCategory = "IVA",
                    productCode = "ELEC-001",
                    confidence = 0.85,
                    alternatives = listOf(
                        AlternativeClassification(
                            tariffCode = "854239",
                            description = "Technology",
                            confidence = 0.70,
                            reason = "Alternative classification"
                        ),
                        AlternativeClassification(
                            tariffCode = "847149",
                            description = "Equipments",
                            confidence = 0.65,
                            reason = "Alternative classification"
                        )
                    ),
                    validationMessage = "Manually generated classification",
                    countrySpecific = mapOf(
                        "origin" to "EC",
                        "destination" to "US"
                    )
                )

            }
        )

        return result
    }

    private fun applyCountryRules(
        classification: ProductClassificationData,
        countryRules: TradeRules,
        country: String
    ): ProductClassificationData {

        var updatedClassification = classification

        when (country) {
            "EC" -> {
                if (classification.taxCategory == "IVA" && classification.suggestedCategory in listOf("Books", "Medicines")) {
                    updatedClassification = classification.copy(
                        taxCategory = "IVA_0"
                    )
                }
            }
            "US" -> {
                if (classification.suggestedCategory == "Food") {
                    updatedClassification = classification.copy(
                        taxCategory = "FOOD"
                    )
                }
            }
        }

        if (classification.suggestedCategory in countryRules.prohibitedProducts) {
            updatedClassification = classification.copy(
                confidence = 0.0,
                validationMessage = "Potentially prohibited product in $country"
            )
        }

        return updatedClassification
    }
}