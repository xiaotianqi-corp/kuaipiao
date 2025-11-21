package org.xiaotianqi.kuaipiao.core.clients.ai

import ai.koog.rag.base.files.FileMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.encodeBase64
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json
import org.xiaotianqi.kuaipiao.config.ai.OpenAIConfig
import org.xiaotianqi.kuaipiao.core.ai.prompts.InvoicePromptBuilder
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.heuristics.applyAllHeuristics
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliation
import org.xiaotianqi.kuaipiao.domain.ai.Message
import org.xiaotianqi.kuaipiao.domain.ai.OpenAiRequest
import org.xiaotianqi.kuaipiao.domain.ai.OpenAiResponse
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DocumentData
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.organization.CompanyHistory
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}
private val json = Json { ignoreUnknownKeys = true }

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class OpenAIClient(
    private val httpClient: HttpClient,
    private val config: OpenAIConfig,
    private val debugLogs: Boolean = false
) {

    suspend fun extractDocument(
        prompt: String,
        fileBytes: ByteArray,
        fileType: FileType
    ): DocumentExtractionResult {

        val base64 = fileBytes.encodeBase64()

        val fullPrompt = """
        $prompt

        El archivo viene en Base64:
        $base64
    """.trimIndent()

        val response = executeOpenAiRequest(fullPrompt)

        val content = response.choices.first().message.content

        val elapsed = 800L

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.IDENTIFICATION,
            success = true,
            confidence = 0.85,
            extractedData = emptyMap(),
            rawText = content,
            processingTimeMs = elapsed
        )
    }

    suspend fun processInvoiceDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        val prompt = InvoicePromptBuilder.build(country)
        val response = executeOpenAiRequest(prompt)
        return parseInvoiceResponse(response, country)
    }

    suspend fun classifyTariffCode(
        productDescription: String,
        countryOrigin: String,
        countryDestination: String
    ): TariffClassification {
        val prompt = """
            Clasifica la partida arancelaria para:
            Producto: $productDescription
            Origen: $countryOrigin
            Destino: $countryDestination
            Responde en JSON: { "tariffCode": "string", "description": "string", "confidence": number, "requiredDocuments": ["string"] }
        """.trimIndent()
        val response = executeOpenAiRequest(prompt)
        return parseTariffResponse(response)
    }

    suspend fun analyzeComplianceRisk(
        transactionData: TransactionData,
        companyHistory: CompanyHistory
    ): ComplianceRiskAnalysis {
        val prompt = """
            Analiza riesgo de compliance para transacción: ${transactionData.toJson()}
            Historial: ${companyHistory.summary}
            Responde en JSON con riskScore, auditProbability, recommendations.
        """.trimIndent()
        val response = executeOpenAiRequest(prompt)
        return parseComplianceResponse(response)
    }

    suspend fun reconcileAccounting(
        documentData: DocumentData,
        historicalPatterns: List<AccountingPattern>
    ): AccountingReconciliation {
        val prompt = """
            Reconciliación contable para: ${documentData.toJson()}
            Patrones históricos: ${historicalPatterns.size} patrones
            Responde en JSON con suggestedAccounts, taxImplications.
        """.trimIndent()
        val response = executeOpenAiRequest(prompt)
        return parseAccountingResponse(response)
    }

    suspend fun executeGenericOperation(operation: String): String {
        val response = executeOpenAiRequest(operation)
        return response.choices.first().message.content
    }

    private suspend fun executeOpenAiRequest(prompt: String): OpenAiResponse {
        return httpClient.post("${config.baseUrl}/chat/completions") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAiRequest(
                    model = config.model,
                    messages = listOf(Message(role = "user", content = prompt)),
                    temperature = 0.1
                )
            )
        }.body()
    }

    private fun parseInvoiceResponse(response: OpenAiResponse, country: String): InvoiceProcessingResult {
        try {
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw AiException("Empty OpenAI response.")

            if (debugLogs) logger.debug { "OpenAI raw response: $content" }

            val invoice: InvoiceData = json.decodeFromString(InvoiceData.serializer(), content)

            val (confidence, errors, suggestions) = applyAllHeuristics(invoice, country)
            val status = if (errors.isEmpty()) OperationStatus.SUCCESS else OperationStatus.WARNING

            return InvoiceProcessingResult(
                documentId = invoice.id,
                invoiceId = invoice.number,
                supplierName = invoice.providerId.name,
                totalAmount = invoice.total.toDoubleOrNull() ?: 0.0,
                currency = invoice.currency,
                issueDate = invoice.date,
                taxAmount = invoice.tax.toDoubleOrNull() ?: 0.0,
                extractedData = invoice,
                confidence = confidence,
                validationErrors = errors,
                suggestions = suggestions,
                processingTime = 0L,
                aiProvider = "OpenAI",
                status = status
            )
        } catch (e: Exception) {
            logger.error(e) { "Error parsing OpenAI invoice response" }
            throw AiException("Error parsing OpenAI invoice response: ${e.message}")
        }
    }

    private fun parseTariffResponse(response: OpenAiResponse): TariffClassification {
        return try {
            json.decodeFromString<TariffClassification>(response.choices.first().message.content)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing OpenAI tariff response" }
            throw AiException("Error parsing tariff classification: ${e.message}")
        }
    }

    private fun parseComplianceResponse(response: OpenAiResponse): ComplianceRiskAnalysis {
        return try {
            json.decodeFromString<ComplianceRiskAnalysis>(response.choices.first().message.content)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing OpenAI compliance response" }
            throw AiException("Error parsing compliance analysis: ${e.message}")
        }
    }

    private fun parseAccountingResponse(response: OpenAiResponse): AccountingReconciliation {
        return try {
            json.decodeFromString<AccountingReconciliation>(response.choices.first().message.content)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing OpenAI accounting response" }
            throw AiException("Error parsing accounting reconciliation: ${e.message}")
        }
    }
}

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class OpenAIClientAdapter(
    private val client: OpenAIClient
) : InvoiceExtractionService {

    override suspend fun processInvoice(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        return client.processInvoiceDocument(fileBytes, fileType, country)
    }
}

@ExperimentalTime
class OpenAIProductClassifierAdapter(
    private val client: OpenAIProductClassifier
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {
        return client.classify(input)
    }
}