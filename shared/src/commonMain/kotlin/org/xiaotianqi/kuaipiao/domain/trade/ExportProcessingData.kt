package org.xiaotianqi.kuaipiao.domain.trade

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceCheck
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.AlertType
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@Serializable
@ExperimentalTime
@ExperimentalUuidApi
data class ExportDocumentResult(
    val documentData: InvoiceProcessingResult,
    val tariffClassifications: List<TariffClassification>,
    val complianceCheck: ComplianceCheck,
    val requiredDocuments: List<String>,
    val alerts: List<ExportAlert>,
    val rawText: String,
    val riskLevel: RiskLevel,
    val processingTime: Long = 0L,
    val aiProvider: String = "Unknown",
    val timestamp: Instant = Clock.System.now()
)

@Serializable
data class ExportAlert(
    val type: AlertType,
    val severity: SeverityStatus,
    val message: String,
    val actionRequired: Boolean,
    val suggestedAction: String? = null
)

data class ExportRules(
    val originCountry: String,
    val destinationCountry: String,
    val isTradeAllowed: Boolean,
    val requiredDocuments: List<String>,
    val restrictions: List<String>,
    val taxTreaties: List<String>
)