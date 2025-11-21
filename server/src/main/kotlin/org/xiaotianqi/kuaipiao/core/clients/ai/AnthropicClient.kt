package org.xiaotianqi.kuaipiao.core.clients.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.InternalAPI
import org.xiaotianqi.kuaipiao.config.ai.AnthropicConfig
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.domain.ai.*
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.organization.CompanyHistory
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@OptIn(InternalAPI::class)
class AnthropicClient(
    private val httpClient: HttpClient,
    private val config: AnthropicConfig
) {

    suspend fun extractDocument(
        prompt: String,
        fileBytes: ByteArray,
        fileType: FileType
    ): DocumentExtractionResult {

        val base64 = fileBytes.encodeBase64()
        val finalPrompt = """
        $prompt

        Documento en Base64:
        $base64
    """.trimIndent()

        val response = executeAnthropicRequest(finalPrompt)
        val content = response.content.first().text

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.IDENTIFICATION,
            success = true,
            confidence = 0.83,
            extractedData = emptyMap(),
            rawText = content,
            processingTimeMs = 950
        )
    }

    suspend fun analyzeComplianceRisk(
        transactionData: TransactionData,
        companyHistory: CompanyHistory
    ): ComplianceRiskAnalysis {
        val prompt = """
            Analiza riesgo de compliance para esta transacción.
            Responde en formato JSON estructurado.
            Datos de la transacción: $transactionData
            Historial de la compañía: $companyHistory
        """.trimIndent()

        val response = executeAnthropicRequest(prompt)

        return parseAnthropicResponse(response)
    }

    suspend fun executeGenericOperation(operation: String): String {
        val response = executeAnthropicRequest(operation)
        return response.content.first().text
    }

    private suspend fun executeAnthropicRequest(promptContent: String): AnthropicResponse {
        return httpClient.post(
            "https://api.anthropic.com/v1/messages"
        ) {
            header("x-api-key", config.apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            body = AnthropicRequest(
                model = config.model,
                messages = listOf(AnthropicMessage(role = "user", content = promptContent)),
                maxTokens = 1000,
                temperature = 1.0,
                system = null
            )
        }.body()
    }

    private fun parseAnthropicResponse(response: AnthropicResponse): ComplianceRiskAnalysis {
        return ComplianceRiskAnalysis(
            companyId = "",
            period = DateRange(
                start = Clock.System.now(),
                end = Clock.System.now(),
            ),
            riskScore = 0.5,
            riskPatterns = emptyList(),
            highRiskTransactions = emptyList(),
            auditProbability = 0.3,
            recommendations = emptyList(),
            nextReviewDate = Clock.System.now()
        )
    }
}



@ExperimentalTime
class AnthropicProductClassifierAdapter(
    private val client: AnthropicProductClassifier
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {
        return client.classify(input)
    }
}
