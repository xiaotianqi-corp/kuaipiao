package org.xiaotianqi.kuaipiao.data.sources.external

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.config.ai.OpenAIConfig
import org.xiaotianqi.kuaipiao.core.exceptions.AiException
import org.xiaotianqi.kuaipiao.core.exceptions.AiRateLimitException
import org.xiaotianqi.kuaipiao.domain.ai.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.ai.Message
import org.xiaotianqi.kuaipiao.domain.ai.OpenAiRequest
import org.xiaotianqi.kuaipiao.domain.ai.OpenAiResponse
import org.xiaotianqi.kuaipiao.domain.ai.SentimentAnalysis
import org.xiaotianqi.kuaipiao.domain.ai.UsageStats
import org.xiaotianqi.kuaipiao.domain.embedding.EmbeddingRequest
import org.xiaotianqi.kuaipiao.domain.embedding.EmbeddingResponse

private val logger = KotlinLogging.logger {}

@OptIn(InternalAPI::class)
class OpenAIDataSource(
    private val httpClient: HttpClient,
    private val config: OpenAIConfig
) {

    suspend fun createChatCompletion(
        messages: List<Message>,
        temperature: Double = 0.1,
        maxTokens: Int = 1000
    ): OpenAiResponse {
        return try {
            httpClient.post("${config.baseUrl}/chat/completions") {
                header("Authorization", "Bearer ${config.apiKey}")
                contentType(ContentType.Application.Json)
                body = OpenAiRequest(
                    model = config.model,
                    messages = messages,
                    temperature = temperature,
                    maxTokens = maxTokens
                )
            }.body()
        } catch (e: Exception) {
            handleOpenAIError(e)
        }
    }

    suspend fun extractStructuredData(
        text: String,
        schema: String,
        instructions: String = ""
    ): String {
        val prompt = """
            $instructions
            
            Extract structured data from the following text according to the provided schema:
            
            SCHEME:
            $schema
            
            TEXT:
            $text
            
            Respond ONLY with valid JSON according to the schema, without additional explanations.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return response.choices.first().message.content
    }

    suspend fun classifyText(
        text: String,
        categories: List<String>,
        context: String = ""
    ): ClassificationResult {
        val prompt = """
            $context
            
            Classify the following text into one of these categories: ${categories.joinToString()}
            
            TEXT:
            $text
            
            Respond ONLY with the name of the most appropriate category.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        val category = response.choices.first().message.content

        return ClassificationResult(
            category = category,
            confidence = 0.8,
            alternatives = categories.filter { it != category }
        )
    }

    suspend fun analyzeSentiment(
        text: String,
        context: String = ""
    ): SentimentAnalysis {
        val prompt = """
            $context
            
            Analyze the sentiment of the following text and provide:
                1. Overall sentiment (POSITIVE, NEGATIVE, NEUTRAL)
                2. Confidence score (0-1)
                3. Key aspects mentioned
                4. Emotions detected
            
            TEXT:
            $text
            
            Respond in JSON format.
        """.trimIndent()

        val response = createChatCompletion(
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.1
        )

        return parseSentimentResponse(response.choices.first().message.content)
    }

    suspend fun generateEmbeddings(
        text: String,
        model: String = "text-embedding-ada-002"
    ): List<Double> {
        return try {
            val response: EmbeddingResponse = httpClient.post("${config.baseUrl}/embeddings") {
                header("Authorization", "Bearer ${config.apiKey}")
                contentType(ContentType.Application.Json)
                body = EmbeddingRequest(
                    model = model,
                    input = text
                )
            }.body()

            response.data.first().embedding
        } catch (e: Exception) {
            handleOpenAIError(e)
        }
    }

    suspend fun getUsageStats(): UsageStats {
        return try {
            UsageStats(
                totalTokens = 0,
                promptTokens = 0,
                completionTokens = 0,
                totalCost = 0.0
            )
        } catch (e: Exception) {
            logger.warn(e) { "Error getting OpenAI usage stats" }
            UsageStats()
        }
    }

    private fun handleOpenAIError(e: Exception): Nothing {
        logger.error(e) { "OpenAI API error" }

        when {
            e.message?.contains("rate limit") == true -> {
                throw AiRateLimitException("OpenAI", 60)
            }
            e.message?.contains("insufficient_quota") == true -> {
                throw AiException("OpenAI quota exceeded", "OPENAI_QUOTA_EXCEEDED")
            }
            e.message?.contains("invalid_api_key") == true -> {
                throw AiException("Invalid OpenAI API key", "OPENAI_INVALID_API_KEY")
            }
            else -> {
                throw AiException("OpenAI API error: ${e.message}", "OPENAI_API_ERROR")
            }
        }
    }

    private fun parseSentimentResponse(response: String): SentimentAnalysis {
        return SentimentAnalysis(
            sentiment = "NEUTRAL",
            confidence = 0.5,
            aspects = emptyList(),
            emotions = emptyList()
        )
    }
}