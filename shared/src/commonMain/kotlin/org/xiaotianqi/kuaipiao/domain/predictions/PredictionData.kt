package org.xiaotianqi.kuaipiao.domain.predictions

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.metrics.KeyFactor
import org.xiaotianqi.kuaipiao.domain.metrics.PerformanceMetrics
import org.xiaotianqi.kuaipiao.domain.metrics.TrendAnalysis
import org.xiaotianqi.kuaipiao.domain.sales.SalesPrediction
import org.xiaotianqi.kuaipiao.enums.ModelType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class PredictionData(
    val label: String? = null,
    val category: String? = null,
    val confidence: Double,
    val explanation: String? = null,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val tariffCode: String? = null,
    val accountingAccount: String? = null,
    val taxCategory: String? = null,
    val productCode: String? = null
)

@Serializable
@ExperimentalTime
data class MultiplePeriodPrediction(
    val companyId: String,
    val predictions: List<SalesPrediction>,
    val consensus: PredictionConsensus
)

@Serializable
data class PredictionConsensus(
    val averageGrowth: Double,
    val confidence: Double,
    val agreementLevel: String
)

@Serializable
@ExperimentalTime
data class PredictionResult(
    val predictionId: String,
    val modelType: ModelType,
    val inputFeatures: Map<String, JsonElement>,
    val predictions: List<PredictionValue>,
    val confidence: Double,
    val explanation: PredictionExplanation,
    val metadata: PredictionMetadata,
    val createdAt: Instant = Clock.System.now()
)

@Serializable
data class PredictionValue(
    val target: String,
    val value: Double,
    val confidence: Double,
    val lowerBound: Double? = null,
    val upperBound: Double? = null,
    val unit: String? = null
)

@Serializable
data class PredictionExplanation(
    val keyFactors: List<KeyFactor>,
    val featureImportance: Map<String, Double>,
    val trendAnalysis: TrendAnalysis,
    val limitations: List<String> = emptyList()
)

@Serializable
@ExperimentalTime
data class PredictionMetadata(
    val modelVersion: String,
    val trainingDataRange: DateRange,
    val predictionHorizon: String,
    val algorithm: String,
    val hyperparameters: Map<String, JsonElement>,
    val performanceMetrics: PerformanceMetrics
)

@Serializable
data class PredictionRequest(
    val modelType: ModelType,
    val inputData: Map<String, JsonElement>,
    val horizon: String = "30d",
    val confidenceThreshold: Double = 0.7,
    val includeExplanation: Boolean = true,
    val includeUncertainty: Boolean = true
)

@Serializable
@ExperimentalTime
data class PredictionResponse(
    val success: Boolean,
    val prediction: PredictionResult? = null,
    val error: String? = null,
    val warnings: List<String> = emptyList(),
    val processingTime: Long
)