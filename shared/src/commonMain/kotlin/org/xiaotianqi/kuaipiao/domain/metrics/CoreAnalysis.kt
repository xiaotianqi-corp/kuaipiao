package org.xiaotianqi.kuaipiao.domain.metrics

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.TrendDirection

@Serializable
data class KeyFactor(
    val factor: String,
    val impact: Double,
    val explanation: String,
    val evidence: List<String> = emptyList()
)

@Serializable
data class TrendAnalysis(
    val direction: TrendDirection,
    val strength: Double,
    val duration: String,
    val confidence: Double
)