package org.xiaotianqi.kuaipiao.domain.benchmark

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.RiskPatternType

@Serializable
data class IndustryBenchmarks(
    val industry: String,
    val avgTransactionSize: Double,
    val typicalTransactionCount: Int,
    val commonRiskPatterns: List<RiskPatternType>,
    val taxComplianceRate: Double = 0.95,
    val auditProbability: Double = 0.15
)