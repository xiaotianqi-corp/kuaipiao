package org.xiaotianqi.kuaipiao.core.clients.ai

import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.xiaotianqi.kuaipiao.config.ai.AnthropicConfig
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.classification.*
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData
import org.xiaotianqi.kuaipiao.enums.ClassifierType
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AnthropicProductClassifier(
    private val httpClient: HttpClient,
    private val config: AnthropicConfig
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {

        val response = httpClient.post(config.baseUrl) {
            headers { append(HttpHeaders.Authorization, "Bearer ${config.apiKey}") }
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "model" to config.model,
                    "messages" to listOf(
                        mapOf("role" to "user", "content" to input.text)
                    )
                )
            )
        }

        val body: Map<String, Any?> = response.body()
        val data = body["classification"] as? Map<*, *> ?: emptyMap<String, Any?>()

        val prediction = PredictionData(
            label = data["label"]?.toString(),
            confidence = (data["confidence"] as? Number)?.toDouble() ?: 0.0,
            tariffCode = data["tariff_code"]?.toString(),
            accountingAccount = data["account_code"]?.toString(),
            taxCategory = data["tax_category"]?.toString(),
            productCode = data["product_code"]?.toString(),
        )

        return ClassificationResult(
            id = "anthropic-${System.currentTimeMillis()}",
            input = input,
            predictions = listOf(prediction),
            topPrediction = prediction,
            confidence = prediction.confidence,
            modelVersion = config.model,
            processingTime = 0,
            metadata = ClassificationMetadata(
                classifierType = ClassifierType.CUSTOM,
                aiProvider = "anthropic",
                modelName = config.model
            )
        )
    }
}

