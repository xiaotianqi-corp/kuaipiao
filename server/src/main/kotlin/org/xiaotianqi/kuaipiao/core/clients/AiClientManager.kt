package org.xiaotianqi.kuaipiao.core.clients

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonElement
import org.xiaotianqi.kuaipiao.core.clients.ai.AnthropicClient
import org.xiaotianqi.kuaipiao.core.clients.ai.DeepSeekClient
import org.xiaotianqi.kuaipiao.core.clients.ai.GoogleVisionClient
import org.xiaotianqi.kuaipiao.core.clients.ai.OpenAIClient
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliation
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DocumentData
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.invoice.toDocumentData
import org.xiaotianqi.kuaipiao.domain.organization.CompanyHistory
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.enums.AiProvider
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.compareTo
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class AiClientManager(
    private val openAIClient: OpenAIClient,
    private val deepSeekClient: DeepSeekClient,
    private val googleVisionClient: GoogleVisionClient,
    private val anthropicClient: AnthropicClient,
    private val timeout: Duration,
    private var currentProvider: String = "OpenAI"

) : InvoiceExtractionService {

    override suspend fun processInvoice(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        return processInvoiceWithOCR(fileBytes, fileType, country)
    }
    suspend fun extractDocument(
        prompt: String,
        fileBytes: ByteArray,
        fileType: FileType,
        operation: String
    ): DocumentExtractionResult {
        val startMs = Clock.System.now().toEpochMilliseconds()

        return try {
            val result = executeWithFallback(
                operation = operation,
                primary = {
                    openAIClient.extractDocument(prompt, fileBytes, fileType)
                },
                fallbacks = listOf(
                    { deepSeekClient.extractDocument(prompt, fileBytes, fileType) },
                    { anthropicClient.extractDocument(prompt, fileBytes, fileType) }
                )
            )

            val processingTime = if (result.processingTimeMs > 0L) result.processingTimeMs
            else Clock.System.now().toEpochMilliseconds() - startMs

            result.copy(processingTimeMs = processingTime)
        } catch (e: Exception) {
            logger.error(e) { "extractDocument failed for operation=$operation" }

            DocumentExtractionResult(
                documentIndex = 0,
                documentType = DocumentType.IDENTIFICATION,
                success = false,
                confidence = 0.0,
                rawText = "",
                extractedData = emptyMap<String, JsonElement>(),
                processingTimeMs = Clock.System.now().toEpochMilliseconds() - startMs
            )
        }
    }

    suspend fun <T> executeWithFallback(
        operation: String,
        primary: suspend () -> T,
        fallbacks: List<suspend () -> T> = emptyList(),
        timeout: Duration = this.timeout
    ): T {
        var lastException: Exception? = null

        suspend fun executeWithTimeout(provider: String, block: suspend () -> T): T? {
            return try {
                withTimeout(timeout.inWholeMilliseconds) {
                    block()
                }
            } catch (e: TimeoutCancellationException) {
                logger.warn { "Timeout at provider $provider for operation $operation" }
                throw AiException("Timeout at provider $provider after ${timeout.inWholeSeconds}s")
            } catch (e: Exception) {
                logger.warn { "Provider error $provider: ${e.message}" }
                lastException = e
                null
            }
        }

        logger.debug { "Running operation $operation with primary provider" }
        executeWithTimeout("Primary", primary)?.let { return it }

        fallbacks.forEachIndexed { index, fallback ->
            val providerName = "Fallback-${index + 1}"
            logger.debug { "Attempting $providerName for $operation" }
            executeWithTimeout(providerName, fallback)?.let { return it }
        }

        throw lastException ?: AiException("All AI providers failed for operation: $operation")
    }


    suspend fun processInvoiceWithOCR(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        return executeWithFallback(
            operation = "invoice_ocr_${country}",
            primary = {
                openAIClient.processInvoiceDocument(fileBytes, fileType, country)
            },
            fallbacks = listOf(
                { deepSeekClient.processInvoiceDocument(fileBytes, fileType, country) },
                { googleVisionClient.processInvoiceDocument(fileBytes, fileType, country) }

            )
        )
    }

    suspend fun classifyTariffCode(
        productDescription: String,
        countryOrigin: String,
        countryDestination: String
    ): TariffClassification {
        return executeWithFallback(
            operation = "tariff_classification_${countryOrigin}_${countryDestination}",
            primary = {
                openAIClient.classifyTariffCode(productDescription, countryOrigin, countryDestination)
            }
        )
    }

    suspend fun analyzeComplianceRisk(
        transactionData: TransactionData,
        companyHistory: CompanyHistory
    ): ComplianceRiskAnalysis {
        return executeWithFallback(
            operation = "compliance_risk_analysis",
            primary = {
                anthropicClient.analyzeComplianceRisk(transactionData, companyHistory)
            },
            fallbacks = listOf(
                { openAIClient.analyzeComplianceRisk(transactionData, companyHistory) }
            )
        )
    }

    suspend fun reconcileAccounting(
        invoiceData: InvoiceData,
        historicalPatterns: List<AccountingPattern>
    ): AccountingReconciliation {
        val documentData = invoiceData.toDocumentData()
        return executeWithFallback(
            operation = "accounting_reconciliation",
            primary = {
                openAIClient.reconcileAccounting(documentData, historicalPatterns)
            }
        )
    }

    fun getCurrentProvider(): AiProvider {
        return when (currentProvider) {
            "OpenAI" -> AiProvider.OPENAI
            "DeepSeek" -> AiProvider.DEEPSEEK
            "GoogleVision" -> AiProvider.GOOGLE_VISION
            "Anthropic" -> AiProvider.ANTHROPIC
            else -> AiProvider.OPENAI
        }
    }
}
