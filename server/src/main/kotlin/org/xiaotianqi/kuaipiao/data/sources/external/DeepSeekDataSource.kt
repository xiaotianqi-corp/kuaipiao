package org.xiaotianqi.kuaipiao.data.sources.external

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.InternalAPI
import org.xiaotianqi.kuaipiao.config.ai.DeepSeekConfig
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.exceptions.AiRateLimitException
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingEntriesResult
import org.xiaotianqi.kuaipiao.domain.ai.DeepSeekRequest
import org.xiaotianqi.kuaipiao.domain.ai.DeepSeekResponse
import org.xiaotianqi.kuaipiao.domain.ai.Message
import org.xiaotianqi.kuaipiao.domain.business.BusinessRulesValidation
import org.xiaotianqi.kuaipiao.domain.financial.FinancialExtractionResult
import org.xiaotianqi.kuaipiao.domain.tax.TaxComplianceAnalysis
import org.xiaotianqi.kuaipiao.domain.validation.ValidationResult
import org.xiaotianqi.kuaipiao.enums.EntryPurpose
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalStdlibApi
@OptIn(InternalAPI::class)
class DeepSeekDataSource(
    private val httpClient: HttpClient,
    private val config: DeepSeekConfig
) {

    suspend fun createChatCompletion(
        messages: List<Message>,
        temperature: Double = 0.1,
        maxTokens: Int = 1000,
        stream: Boolean = false
    ): DeepSeekResponse {
        return try {
            httpClient.post("${config.baseUrl}/chat/completions") {
                header("Authorization", "Bearer ${config.apiKey}")
                contentType(ContentType.Application.Json)
                body = DeepSeekRequest(
                    model = config.model,
                    messages = messages,
                    temperature = temperature,
                    maxTokens = maxTokens,
                    stream = stream
                )
            }.body()
        } catch (e: Exception) {
            handleDeepSeekError(e)
        }
    }

    suspend fun extractFinancialData(
        text: String,
        documentType: String
    ): FinancialExtractionResult {
        val prompt = """
            You are a specialist in extracting financial and accounting data.
            
            Extract structured information from the following document type $documentType:
            
            TEXT:
            $text
            
            Provide the data in JSON format with the fields relevant to the document type.
            Include only the fields you can confidently extract.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return parseFinancialResponse(response.choices.first().message.content)
    }

    suspend fun analyzeTaxCompliance(
        transactionData: String,
        country: String
    ): TaxComplianceAnalysis {
        val prompt = """
            You are a tax compliance specialist of $country.
            
            Analyze the following transaction and determine:
                1. Compliance with tax regulations
                2. Applicable taxes
                3. Documentation requirements
                4. Potential risks
            
            TRANSACTION DATA:
            $transactionData
            
            Respond in structured JSON format.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return parseTaxComplianceResponse(response.choices.first().message.content)
    }

    suspend fun generateAccountingEntries(
        transactionData: String,
        chartOfAccounts: String
    ): AccountingEntriesResult {
        val prompt = """
            Generate journal entries for the following transaction:

            TRANSACTION:
            $transactionData
            
            CHART OF ACCOUNTS:
            $chartOfAccounts
            
            Provide:

                - Debit and credit account numbers
                - Amounts
                - Descriptions
                - References

            Respond in JSON format.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return parseAccountingResponse(response.choices.first().message.content)
    }

    suspend fun validateBusinessRules(
        data: String,
        rules: String
    ): BusinessRulesValidation {
        val prompt = """
            Validate the following data according to the business rules:
            
            DATA:
            $data
            
            BUSINESS RULES:
            $rules
            
            Provides:

                - Validation result (APPROVED/REJECTED)
                - Errors found
                - Warnings
                - Suggestions
            
            Responds in JSON format.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return parseValidationResponse(response.choices.first().message.content)
    }

    private fun handleDeepSeekError(e: Exception): Nothing {
        logger.error(e) { "DeepSeek API error" }

        when {
            e.message?.contains("rate limit") == true -> {
                throw AiRateLimitException("DeepSeek", 60)
            }
            e.message?.contains("quota") == true -> {
                throw AiException("DeepSeek quota exceeded", "DEEPSEEK_QUOTA_EXCEEDED")
            }
            e.message?.contains("invalid_api_key") == true -> {
                throw AiException("Invalid DeepSeek API key", "DEEPSEEK_INVALID_API_KEY")
            }
            else -> {
                throw AiException("DeepSeek API error: ${e.message}", "DEEPSEEK_API_ERROR")
            }
        }
    }

    private fun parseFinancialResponse(response: String): FinancialExtractionResult {
        return FinancialExtractionResult(
            success = true,
            extractedData = emptyMap(),
            confidence = 0.8
        )
    }

    private fun parseTaxComplianceResponse(response: String): TaxComplianceAnalysis {
        return TaxComplianceAnalysis(
            isCompliant = true,
            applicableTaxes = emptyList(),
            requiredDocuments = emptyList(),
            risks = emptyList(),
            confidence = 0.8
        )
    }

    private fun parseAccountingResponse(response: String): AccountingEntriesResult {
        return AccountingEntriesResult(
            entries = emptyList(),
            purpose = EntryPurpose.UNKNOWN,
            validation = ValidationResult(true, emptyList()),
            confidence = 0.8
        )
    }

    private fun parseValidationResponse(response: String): BusinessRulesValidation {
        return BusinessRulesValidation(
            isValid = true,
            errors = emptyList(),
            warnings = emptyList(),
            suggestions = emptyList()
        )
    }
}


