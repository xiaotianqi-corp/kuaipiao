package org.xiaotianqi.kuaipiao.core.clients.ai

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.xiaotianqi.kuaipiao.config.ai.OpenAIConfig
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.classification.*
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData
import org.xiaotianqi.kuaipiao.enums.ClassifierType
import kotlin.time.ExperimentalTime

@ExperimentalTime
class OpenAIProductClassifier(
    private val httpClient: HttpClient,
    private val config: OpenAIConfig
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {

        val response = httpClient.post(config.baseUrl) {
            headers { append(HttpHeaders.Authorization, "Bearer ${config.apiKey}") }
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "model" to config.model,
                    "input" to input.text
                )
            )
        }

        val result: Map<String, Any?> = response.body()

        val prediction = PredictionData(
            label = result["label"]?.toString(),
            confidence = (result["confidence"] as? Number)?.toDouble() ?: 0.0,
            tariffCode = result["tariff_code"]?.toString(),
            accountingAccount = result["account_code"]?.toString(),
            taxCategory = result["tax_category"]?.toString(),
            productCode = result["product_code"]?.toString()
        )

        return ClassificationResult(
            id = "openai-${System.currentTimeMillis()}",
            input = input,
            predictions = listOf(prediction),
            topPrediction = prediction,
            confidence = prediction.confidence,
            modelVersion = config.model,
            processingTime = 0,
            metadata = ClassificationMetadata(
                classifierType = ClassifierType.CUSTOM,
                aiProvider = "openai",
                modelName = config.model
            )
        )
    }
}

