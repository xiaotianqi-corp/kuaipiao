package org.xiaotianqi.kuaipiao.domain.reconciliation

import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
@ExperimentalTime
data class BatchReconciliationResult(
    val successful: List<AccountingReconciliationResult>,
    val failed: List<ReconciliationError>,
    val summary: ReconciliationSummary
)

@Serializable
data class ReconciliationError(
    val documentIndex: Int,
    val documentType: String,
    val error: String
)

@Serializable
data class ReconciliationSummary(
    val totalDocuments: Int,
    val reconciled: Int,
    val failed: Int,
    val automationRate: Double
)

