package org.xiaotianqi.kuaipiao.domain.classification

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData
import org.xiaotianqi.kuaipiao.enums.AiProvider
import org.xiaotianqi.kuaipiao.enums.ClassifierType
import org.xiaotianqi.kuaipiao.enums.ConfidentialityLevel
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import org.xiaotianqi.kuaipiao.enums.UrgencyLevel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class ClassificationResult(
    val id: String,
    val input: ClassificationInput,
    val predictions: List<PredictionData>,
    val topPrediction: PredictionData,
    val confidence: Double,
    val modelVersion: String,
    val processingTime: Long,
    val metadata: ClassificationMetadata,
    val createdAt: Instant = Clock.System.now()
)

@Serializable
data class ClassificationInput(
    val text: String,
    val context: Map<String, String> = emptyMap(),
    val categories: List<String>? = null,
    val language: String = "es"
)

@Serializable
data class ClassificationMetadata(
    val classifierType: ClassifierType,
    val aiProvider: String,
    val modelName: String,
    val featureCount: Int? = null,
    val trainingDataSize: Int? = null,
    val threshold: Double = 0.5,
    val maxPredictions: Int = 5
)

@Serializable
@ExperimentalTime
data class ClassificationItem(
    val input: ClassificationInput,
    val result: ClassificationResult,
    val status: OperationStatus
)

@Serializable
data class ClassificationSummary(
    val totalItems: Int,
    val processed: Int,
    val failed: Int,
    val averageConfidence: Double,
    val categoryDistribution: Map<String, Int>,
    val processingTime: Long
)

@Serializable
data class ClassificationTrainingData(
    val features: List<Double>,
    val label: String,
    val weight: Double = 1.0,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ClassificationError(
    val productName: String? = null,
    val reason: String,
    val details: String? = null,
    val provider: AiProvider,
    val operation: String = "classification",
    val index: Int,
    val input: String,
    val error: String
)

@Serializable
data class ExpenseClassification(
    val description: String,
    val expenseType: String,
    val category: String,
    val deductible: Boolean,
    val taxDeductionRate: Double? = null,
    val requiredDocumentation: List<String> = emptyList(),
    val confidence: Double,
    val legalReferences: List<String> = emptyList()
)

@Serializable
data class DocumentClassification(
    val documentType: String,
    val subType: String? = null,
    val urgency: UrgencyLevel,
    val retentionPeriod: Int? = null,
    val confidentiality: ConfidentialityLevel,
    val requiredActions: List<String> = emptyList(),
    val confidence: Double
)

@Serializable
data class AlternativeClassification(
    val tariffCode: String,
    val description: String,
    val confidence: Double,
    val reason: String
)