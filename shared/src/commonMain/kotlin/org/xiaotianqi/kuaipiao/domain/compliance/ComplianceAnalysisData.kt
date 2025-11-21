package org.xiaotianqi.kuaipiao.domain.compliance

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.risk.HighRiskTransaction
import org.xiaotianqi.kuaipiao.domain.risk.RiskPattern
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class ComplianceRiskAnalysis(
    val companyId: String,
    val period: DateRange,
    val riskScore: Double,
    val riskPatterns: List<RiskPattern>,
    val highRiskTransactions: List<HighRiskTransaction>,
    val auditProbability: Double,
    val recommendations: List<ComplianceRecommendation>,
    val nextReviewDate: Instant,
    val analysisDate: Instant = Clock.System.now()
)

@Serializable
data class ComplianceRecommendation(
    val code: String,
    val description: String,
    val priority: SeverityStatus,
    val estimatedEffort: String,
    val suggestedDeadline: String? = null
)

@Serializable
data class ComplianceCheck(
    val isCompliant: Boolean,
    val riskScore: Double,
    val issues: List<String>?,
    val missingDocuments: List<String>,
    val validationErrors: List<ValidationError>,
    val warnings: List<ComplianceWarning>,
    val suggestedActions: List<String>
)

@Serializable
data class ComplianceWarning(
    val code: String,
    val message: String,
    val severity: SeverityStatus,
    val suggestedAction: String? = null
)

@Serializable
data class ComplianceRisk(
    val type: String,
    val severity: List<SeverityStatus> = listOf(SeverityStatus.HIGH),
    val description: String,
    val mitigation: String
)

@Serializable
@ExperimentalTime
data class ComplianceAnalysisRequest(
    val transactions: List<TransactionData>,
    val country: String,
    val timeWindow: TimeWindow
)

@Serializable
data class TimeWindow(
    val startDate: String,
    val endDate: String
)
