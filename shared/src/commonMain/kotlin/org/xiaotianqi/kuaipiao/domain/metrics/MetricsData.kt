package org.xiaotianqi.kuaipiao.domain.metrics

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AutomationMetrics(
    val companyId: String,
    val periodDays: Duration,
    val totalReconciliations: Int,
    val fullAutomation: Int,
    val partialAutomation: Int,
    val manualProcessing: Int,
    val averageConfidence: Double,
    val automationRate: Double,
    val timeSaved: Duration
)
