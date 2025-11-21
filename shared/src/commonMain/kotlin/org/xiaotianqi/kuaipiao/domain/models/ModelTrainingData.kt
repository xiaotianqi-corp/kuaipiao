package org.xiaotianqi.kuaipiao.domain.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.xiaotianqi.kuaipiao.domain.metrics.PerformanceMetrics
import org.xiaotianqi.kuaipiao.enums.ModelType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import kotlin.time.ExperimentalTime

@Serializable
data class ModelTrainingRequest(
    val modelType: ModelType,
    val trainingData: List<Map<String, JsonElement>>,
    val targetVariable: String,
    val features: List<String>,
    val validationSplit: Double = 0.2,
    val hyperparameters: Map<String, JsonElement> = emptyMap()
)

@Serializable
@ExperimentalTime
data class ModelTrainingResponse(
    val trainingId: String,
    val modelType: ModelType,
    val status: OperationStatus,
    val performance: PerformanceMetrics? = null,
    val trainingTime: Long,
    val modelVersion: String,
    val featureImportance: Map<String, Double>
)

@Serializable
data class ModelPerformance(
    val accuracy: Double,
    val precision: Double,
    val recall: Double,
    val f1Score: Double,
    val confusionMatrix: Map<String, Map<String, Int>>,
    val trainingTime: Long,
    val inferenceTime: Long,
    val modelSize: Long
)
