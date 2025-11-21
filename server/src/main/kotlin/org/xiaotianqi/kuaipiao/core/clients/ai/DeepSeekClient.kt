package org.xiaotianqi.kuaipiao.core.clients.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json
import org.xiaotianqi.kuaipiao.config.ai.DeepSeekConfig
import org.xiaotianqi.kuaipiao.core.ai.prompts.InvoicePromptBuilder
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.core.heuristics.applyAllHeuristics
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.ai.DeepSeekRequest
import org.xiaotianqi.kuaipiao.domain.ai.DeepSeekResponse
import org.xiaotianqi.kuaipiao.domain.ai.Message
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}
private val json = Json { ignoreUnknownKeys = true }

@ExperimentalTime
@OptIn(InternalAPI::class)
class DeepSeekClient(
    private val httpClient: HttpClient,
    private val config: DeepSeekConfig,
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
        
        Archivo en Base64:
        $base64
    """.trimIndent()

        val response = callDeepSeek(fullPrompt)
        val content = response.choices.first().message.content

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.IDENTIFICATION,
            success = true,
            confidence = 0.83,
            extractedData = emptyMap(),
            rawText = content,
            processingTimeMs = 900
        )
    }


    suspend fun processInvoiceDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        val base64 = fileBytes.encodeBase64()
        val prompt = InvoicePromptBuilder.build(country)

        val response = callDeepSeek(prompt)

        return parseDeepSeekResponse(response, country)
    }

    suspend fun executeGenericOperation(operation: String): String {
        val response = callDeepSeek(operation)
        return response.choices.first().message.content
    }

    private suspend fun callDeepSeek(prompt: String): DeepSeekResponse {
        return httpClient.post("${config.baseUrl}/chat/completions") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(
                DeepSeekRequest(
                    model = config.model,
                    messages = listOf(Message(role = "user", content = prompt))
                )
            )
        }.body()
    }

    private fun parseDeepSeekResponse(response: DeepSeekResponse, country: String): InvoiceProcessingResult {
        try {
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw AiException("Empty DeepSeek response.")

            if (debugLogs) logger.debug { "DeepSeek raw response: $content" }

            val invoice: InvoiceData = Json.decodeFromString(content)

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
                aiProvider = "DeepSeek",
                status = status
            )

        } catch (e: Exception) {
            logger.error(e) { "Error parsing DeepSeek response" }
            throw AiException("Error parsing DeepSeek response: ${e.message}")
        }
    }
}

@ExperimentalTime
class DeepSeekClientAdapter(
    private val client: DeepSeekClient
) : InvoiceExtractionService {

    override suspend fun processInvoice(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ) = client.processInvoiceDocument(fileBytes, fileType, country)
}

@ExperimentalTime
class DeepSeekProductClassifierAdapter(
    private val client: DeepSeekProductClassifier
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {
        return client.classify(input)
    }
}
