package org.xiaotianqi.kuaipiao.domain.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class ModelResult(
    val modelType: String,
    val aiProvider: String,
    val operation: String,
    val inputHash: String,
    val inputData: String?,
    val outputData: String,
    val confidence: Double,
    val processingTime: Long,
    val tokensUsed: Int? = null,
    val cost: Double? = null,
    val success: Boolean = true,
    val errorMessage: String? = null,
    val createdAt: Instant = Clock.System.now()
)

@Serializable
data class ProviderStats(
    val provider: String,
    val totalRequests: Int,
    val averageConfidence: Double,
    val averageProcessingTime: Long,
    val totalTokens: Int,
    val totalCost: Double
)

@Serializable
data class OperationStats(
    val operation: String,
    val totalRequests: Int,
    val averageConfidence: Double,
    val averageProcessingTime: Long,
    val successRate: Double
)

@Serializable
@ExperimentalTime
data class CostAnalysis(
    val totalCost: Double,
    val costByProvider: Map<String, Double>,
    val costByOperation: Map<String, Double>,
    val period: Pair<Instant, Instant>
)
