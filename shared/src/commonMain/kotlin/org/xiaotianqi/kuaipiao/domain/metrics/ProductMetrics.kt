package org.xiaotianqi.kuaipiao.domain.metrics

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class PerformanceMetrics(
    val mae: Double,
    val mse: Double,
    val rmse: Double,
    val r2: Double,
    val mape: Double,
    val lastUpdated: Instant = Clock.System.now()
)


@Serializable
data class ProductGrowth(
    val productName: String,
    val growth: Double,
    val currentSales: Double,
    val predictedSales: Double,
    val confidence: Double
)

@Serializable
data class InventoryAlert(
    val product: String,
    val risk: RiskLevel,
    val message: String,
    val suggestedAction: String? = null,
    val estimatedImpact: Double? = null
)

