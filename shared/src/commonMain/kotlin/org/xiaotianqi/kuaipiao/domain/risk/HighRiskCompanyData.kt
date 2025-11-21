package org.xiaotianqi.kuaipiao.domain.risk

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class HighRiskCompany(
    val companyId: String,
    val riskScore: Double,
    val auditProbability: Double,
    val highRiskPatterns: Int,
    val lastAnalysis: Instant,
    val recommendedActions: List<String>
)

@Serializable
@ExperimentalTime
data class HighRiskTransaction(
    val transactionId: String,
    val date: Instant,
    val amount: Double,
    val description: String,
    val riskFactors: List<RiskFactor>,
    val suggestedAction: String
)
