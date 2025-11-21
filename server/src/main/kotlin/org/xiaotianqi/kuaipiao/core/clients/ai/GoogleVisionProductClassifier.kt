package org.xiaotianqi.kuaipiao.core.clients.ai

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.xiaotianqi.kuaipiao.config.ai.GoogleVisionConfig
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.classification.*
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData
import org.xiaotianqi.kuaipiao.enums.ClassifierType
import kotlin.time.ExperimentalTime

@ExperimentalTime
class GoogleVisionProductClassifier(
    private val httpClient: HttpClient,
    private val config: GoogleVisionConfig
) : ProductClassificationService {

    override suspend fun classify(input: ClassificationInput): ClassificationResult {

        val response = httpClient.post(config.baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "document" to mapOf("content" to input.text),
                    "features" to listOf(mapOf("type" to "TEXT_DETECTION")),
                    "key" to config.apiKey
                )
            )
        }

        val raw: Map<String, Any?> = response.body()

        val prediction = PredictionData(
            label = "google-vision-text",
            confidence = 0.50,
            tariffCode = null,
            accountingAccount = null,
            taxCategory = null,
            productCode = null,
        )

        return ClassificationResult(
            id = "googlevision-${System.currentTimeMillis()}",
            input = input,
            predictions = listOf(prediction),
            topPrediction = prediction,
            confidence = prediction.confidence,
            modelVersion = "vision-api",
            processingTime = 0,
            metadata = ClassificationMetadata(
                classifierType = ClassifierType.OCR,
                aiProvider = "google-vision",
                modelName = "vision-api"
            )
        )
    }
}

