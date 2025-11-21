package org.xiaotianqi.kuaipiao.domain.risk

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.RiskPatternType
import org.xiaotianqi.kuaipiao.enums.RiskType
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class RiskPrediction(
    val companyId: String,
    val riskType: RiskType,
    val riskScore: Double,
    val factors: List<RiskFactor>,
    val mitigationStrategies: List<String>,
    val monitoringRecommendations: List<String>,
    val predictionHorizon: String,
    val confidence: Double
)

@Serializable
data class RiskFactor(
    val factor: String,
    val impact: Double,
    val probability: Double,
    val timeFrame: String
)

@Serializable
@ExperimentalTime
data class RiskPattern(
    val patternType: RiskPatternType,
    val description: String,
    val severity: SeverityStatus,
    val occurrences: Int,
    val totalAmount: Double,
    val firstOccurrence: Instant,
    val lastOccurrence: Instant
)