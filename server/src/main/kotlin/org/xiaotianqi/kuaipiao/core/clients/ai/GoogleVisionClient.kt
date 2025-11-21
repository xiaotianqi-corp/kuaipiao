package org.xiaotianqi.kuaipiao.core.clients.ai

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json
import org.xiaotianqi.kuaipiao.config.ai.GoogleVisionConfig
import org.xiaotianqi.kuaipiao.core.ai.prompts.InvoicePromptBuilder
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.heuristics.applyAllHeuristics
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliation
import org.xiaotianqi.kuaipiao.domain.ai.*
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DocumentData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.organization.CompanyHistory
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.enums.FileType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import java.io.ByteArrayInputStream
import java.util.Base64
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}
private val json = Json { ignoreUnknownKeys = true }

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class GoogleVisionClient(
    private val httpClient: HttpClient,
    private val config: GoogleVisionConfig,
    private val debugLogs: Boolean = false
) {
    private var cachedAccessToken: String? = null
    private var tokenExpiryTime: Long = 0L
    private val credentials: GoogleCredentials? by lazy {
        initializeCredentials()
    }

    suspend fun processInvoiceDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): InvoiceProcessingResult {
        val startTime = System.currentTimeMillis()

        val extractedText = when (fileType) {
            FileType.PDF -> extractTextFromPdf(fileBytes)
            FileType.IMAGE -> extractTextFromImage(fileBytes)
            else -> throw AiException("Unsupported file type: $fileType")
        }

        if (debugLogs) logger.debug { "Extracted text from Vision API: $extractedText" }

        val prompt = InvoicePromptBuilder.build(country)
        val fullPrompt = """
            $prompt
            
            Text extracted from the document:
            $extractedText
            
            IMPORTANT: Respond ONLY with a valid JSON object, without markdown or additional text.
        """.trimIndent()

        val response = executeStructuredAnalysis(fullPrompt)
        val processingTime = System.currentTimeMillis() - startTime

        return parseInvoiceResponse(response, country, processingTime)
    }

    suspend fun classifyTariffCode(
        productDescription: String,
        countryOrigin: String,
        countryDestination: String
    ): TariffClassification {
        val prompt = """
            Classify the tariff heading for:
            Product: $productDescription
            Origin: $countryOrigin
            Destination: $countryDestination
            
            Respond ONLY with a valid JSON object with this structure:
            {
                "tariffCode": "6-10 digit HS code",
                "description": "detailed description",
                "confidence": number between 0 and 1,
                "requiredDocuments": ["document1", "document2"]
            }
        """.trimIndent()

        val response = executeStructuredAnalysis(prompt)
        return parseTariffResponse(response)
    }

    suspend fun analyzeComplianceRisk(
        transactionData: TransactionData,
        companyHistory: CompanyHistory
    ): ComplianceRiskAnalysis {
        val prompt = """
            Analyze the compliance risk for this transaction:
            
            Transaction details: ${serializeToJson(transactionData)}
            Company history: ${companyHistory.summary}
            
            Respond ONLY with a valid JSON object with this structure:
            { 
                "riskScore": number between 0 and 100, 
                "riskLevel": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL", 
                "auditProbability": number between 0 and 1, 
                "recommendations": ["recommendation1", "recommendation2"], 
                "flaggedIssues": ["issue1", "issue2"], 
                "complianceChecks": { 
                "amlCompliant": boolean, 
                "sanctionsCheck": boolean, 
                "taxCompliant": boolean 
            } 
        }
        """.trimIndent()

        val response = executeStructuredAnalysis(prompt)
        return parseComplianceResponse(response)
    }

    suspend fun reconcileAccounting(
        documentData: DocumentData,
        historicalPatterns: List<AccountingPattern>
    ): AccountingReconciliation {
        val patternsJson = historicalPatterns.map {
            mapOf(
                "accountCode" to it.accountCode,
                "description" to it.description,
                "frequency" to it.frequency
            )
        }

        val prompt = """
            Perform the accounting reconciliation for this document:

            Document data: ${serializeToJson(documentData)}
            Historical patterns (${historicalPatterns.size} patterns): ${serializeToJson(patternsJson)}
            
            Respond ONLY with a valid JSON object with this structure:
            {
                "suggestedAccounts": [
                {
                    "accountCode": "accounting code",
                    "accountName": "account name",
                    "debit": number or null,
                    "credit": number or null,
                    "confidence": number between 0 and 1
                }
                "taxImplications": {
                    "taxableAmount": number,
                    "taxRate": number,
                    "taxType": "VAT" | "Income Tax" | etc.,
                    "deductible": boolean
                },
                "matchConfidence": number between 0 and 1,
                "warnings": ["warning1", "warning2"]
            }
        """.trimIndent()

        val response = executeStructuredAnalysis(prompt)
        return parseAccountingResponse(response)
    }

    suspend fun executeGenericOperation(operation: String): String {
        return executeStructuredAnalysis(operation)
    }

    suspend fun extractTextFromImage(imageBytes: ByteArray): String {
        val request = VisionRequest(
            requests = listOf(
                ImageRequest(
                    image = ImageContent(content = Base64.getEncoder().encodeToString(imageBytes)),
                    features = listOf(Feature(type = "TEXT_DETECTION", maxResults = 50))
                )
            )
        )

        return try {
            val response: VisionResponse = httpClient.post(
                "${config.baseUrl}/v1/images:annotate"
            ) {
                parameter("key", config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            response.responses.firstOrNull()?.fullTextAnnotation?.text
                ?: throw AiException("No text detected in image")
        } catch (e: Exception) {
            logger.error(e) { "Error extracting text from image" }
            throw AiException("Failed to extract text from image: ${e.message}")
        }
    }

    suspend fun extractTextFromPdf(pdfBytes: ByteArray): String {
        val request = VisionRequest(
            requests = listOf(
                ImageRequest(
                    image = ImageContent(content = Base64.getEncoder().encodeToString(pdfBytes)),
                    features = listOf(
                        Feature(type = "DOCUMENT_TEXT_DETECTION", maxResults = 50)
                    )
                )
            )
        )

        return try {
            val response: VisionResponse = httpClient.post(
                "${config.baseUrl}/v1/images:annotate"
            ) {
                parameter("key", config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            response.responses.firstOrNull()?.fullTextAnnotation?.text
                ?: throw AiException("No text detected in PDF")
        } catch (e: Exception) {
            logger.error(e) { "Error extracting text from PDF" }
            throw AiException("Failed to extract text from PDF: ${e.message}")
        }
    }

    suspend fun analyzeDocumentStructure(imageBytes: ByteArray): DocumentStructureAnalysis {
        val request = VisionRequest(
            requests = listOf(
                ImageRequest(
                    image = ImageContent(content = Base64.getEncoder().encodeToString(imageBytes)),
                    features = listOf(
                        Feature(type = "DOCUMENT_TEXT_DETECTION"),
                        Feature(type = "LABEL_DETECTION", maxResults = 10),
                        Feature(type = "LOGO_DETECTION", maxResults = 5)
                    )
                )
            )
        )

        return try {
            val response: VisionResponse = httpClient.post(
                "${config.baseUrl}/v1/images:annotate"
            ) {
                parameter("key", config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            val firstResponse = response.responses.firstOrNull()
                ?: throw AiException("Empty Vision API response")

            DocumentStructureAnalysis(
                fullText = firstResponse.fullTextAnnotation?.text ?: "",
                labels = extractLabels(firstResponse),
                logos = extractLogos(firstResponse),
                pages = firstResponse.fullTextAnnotation?.pages ?: emptyList()
            )
        } catch (e: Exception) {
            logger.error(e) { "Error analyzing document structure" }
            throw AiException("Failed to analyze document structure: ${e.message}")
        }
    }

    private fun extractLabels(response: VisionAnnotateImageResponse): List<String> {
        return try {
            response.labelAnnotations?.map { annotation -> annotation.description } ?: emptyList()
        } catch (e: Exception) {
            logger.warn(e) { "Failed to extract labels" }
            emptyList()
        }
    }

    private fun extractLogos(response: VisionAnnotateImageResponse): List<String> {
        return try {
            response.logoAnnotations?.map { annotation -> annotation.description } ?: emptyList()
        } catch (e: Exception) {
            logger.warn(e) { "Failed to extract logos" }
            emptyList()
        }
    }

    private suspend fun executeStructuredAnalysis(prompt: String): String {
        return if (config.useGemini) {
            executeGeminiRequest(prompt)
        } else {
            throw AiException("Structured analysis requires Gemini API. Set useGemini=true in config")
        }
    }

    private suspend fun executeGeminiRequest(prompt: String): String {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.1,
                topK = 40,
                topP = 0.95,
                maxOutputTokens = 8192
            ),
            safetySettings = listOf(
                SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
            )
        )

        return try {
            val response: GeminiResponse = httpClient.post(
                "${config.geminiBaseUrl}/v1beta/models/${config.geminiModel}:generateContent"
            ) {
                parameter("key", config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw AiException("Empty Gemini response")

            cleanJsonResponse(text)
        } catch (e: Exception) {
            logger.error(e) { "Error executing Gemini request" }
            throw AiException("Failed to execute Gemini request: ${e.message}")
        }
    }

    private fun initializeCredentials(): GoogleCredentials? {
        return try {
            if (config.serviceAccountJson.isNotEmpty()) {
                val stream = ByteArrayInputStream(config.serviceAccountJson.toByteArray())
                ServiceAccountCredentials.fromStream(stream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            } else {
                null
            }
        } catch (e: Exception) {
            logger.warn(e) { "Failed to initialize Google credentials, falling back to API key" }
            null
        }
    }

    private suspend fun getAccessToken(): String {
        credentials?.let { creds ->
            val now = System.currentTimeMillis()
            if (cachedAccessToken == null || now >= tokenExpiryTime) {
                creds.refreshIfExpired()
                cachedAccessToken = creds.accessToken.tokenValue
                tokenExpiryTime = now + 3000000
            }
            return cachedAccessToken!!
        }

        return config.apiKey
    }

    private fun cleanJsonResponse(response: String): String {
        var cleaned = response.trim()

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json").removeSuffix("```").trim()
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```").removeSuffix("```").trim()
        }

        return cleaned
    }

    private fun serializeToJson(obj: Any): String {
        return try {
            json.encodeToString(kotlinx.serialization.serializer(), obj)
        } catch (e: Exception) {
            obj.toString()
        }
    }

    private fun parseInvoiceResponse(
        response: String,
        country: String,
        processingTime: Long
    ): InvoiceProcessingResult {
        try {
            val cleanedResponse = cleanJsonResponse(response)
            if (debugLogs) logger.debug { "Vision/Gemini cleaned response: $cleanedResponse" }

            val invoice: InvoiceData = json.decodeFromString(InvoiceData.serializer(), cleanedResponse)

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
                processingTime = processingTime,
                aiProvider = "Google Vision + Gemini",
                status = status
            )
        } catch (e: Exception) {
            logger.error(e) { "Error parsing invoice response: $response" }
            throw AiException("Error parsing invoice response: ${e.message}")
        }
    }

    private fun parseTariffResponse(response: String): TariffClassification {
        return try {
            val cleaned = cleanJsonResponse(response)
            json.decodeFromString<TariffClassification>(cleaned)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing tariff response: $response" }
            throw AiException("Error parsing tariff classification: ${e.message}")
        }
    }

    private fun parseComplianceResponse(response: String): ComplianceRiskAnalysis {
        return try {
            val cleaned = cleanJsonResponse(response)
            json.decodeFromString<ComplianceRiskAnalysis>(cleaned)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing compliance response: $response" }
            throw AiException("Error parsing compliance analysis: ${e.message}")
        }
    }

    private fun parseAccountingResponse(response: String): AccountingReconciliation {
        return try {
            val cleaned = cleanJsonResponse(response)
            json.decodeFromString<AccountingReconciliation>(cleaned)
        } catch (e: Exception) {
            logger.error(e) { "Error parsing accounting response: $response" }
            throw AiException("Error parsing accounting reconciliation: ${e.message}")
        }
    }
}

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class GoogleVisionClientAdapter(
    private val client: GoogleVisionClient
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
class GoogleVisionProductClassifierAdapter(
    private val client: GoogleVisionProductClassifier
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {
        return client.classify(input)
    }
}

