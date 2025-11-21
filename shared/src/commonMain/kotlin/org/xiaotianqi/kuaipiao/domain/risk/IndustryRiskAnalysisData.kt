package org.xiaotianqi.kuaipiao.domain.risk

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.RiskComparison

@Serializable
data class IndustryRiskAnalysis(
    val companyId: String,
    val industry: String,
    val companySize: String,
    val companyRiskScore: Double,
    val industryAverageRisk: Double,
    val riskComparison: RiskComparison,
    val recommendations: List<String>
)