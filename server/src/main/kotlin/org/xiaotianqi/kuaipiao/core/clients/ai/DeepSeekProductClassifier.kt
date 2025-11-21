package org.xiaotianqi.kuaipiao.core.clients.ai

import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.xiaotianqi.kuaipiao.config.ai.DeepSeekConfig
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.classification.*
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData
import org.xiaotianqi.kuaipiao.enums.ClassifierType
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DeepSeekProductClassifier(
    private val httpClient: HttpClient,
    private val config: DeepSeekConfig
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {

        val response = httpClient.post(config.baseUrl) {
            headers { append(HttpHeaders.Authorization, "Bearer ${config.apiKey}") }
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "model" to config.model,
                    "input" to input.text,
                    "context" to input.context,
                    "categories" to input.categories
                )
            )
        }

        val body: Map<String, Any?> = response.body()

        val prediction = PredictionData(
            label = body["label"]?.toString(),
            confidence = (body["confidence"] as? Number)?.toDouble() ?: 0.0,
            tariffCode = body["tariff_code"]?.toString(),
            accountingAccount = body["account_code"]?.toString(),
            taxCategory = body["tax_category"]?.toString(),
            productCode = body["product_code"]?.toString()
        )

        return ClassificationResult(
            id = "deepseek-${System.currentTimeMillis()}",
            input = input,
            predictions = listOf(prediction),
            topPrediction = prediction,
            confidence = prediction.confidence,
            modelVersion = config.model,
            processingTime = 0,
            metadata = ClassificationMetadata(
                classifierType = ClassifierType.CUSTOM,
                aiProvider = "deepseek",
                modelName = config.model
            )
        )
    }
}
