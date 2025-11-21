package org.xiaotianqi.kuaipiao.domain.sales

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.metrics.InventoryAlert
import org.xiaotianqi.kuaipiao.domain.metrics.ProductGrowth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class SalesData(
    val date: String,
    val amount: Double,
    val product: String,
    val quantity: Int,
    val category: String
)

@Serializable
@ExperimentalTime
data class SalesPrediction(
    val companyId: String,
    val periodDays: Int,
    val predictedGrowth: Double,
    val topProducts: List<ProductGrowth>,
    val inventoryAlerts: List<InventoryAlert>,
    val riskFactors: List<String>,
    val confidence: Double,
    val period: String,
    val generatedAt: Instant = Clock.System.now(),
    val assumptions: List<String> = emptyList()
)
