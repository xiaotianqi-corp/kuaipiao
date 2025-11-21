package org.xiaotianqi.kuaipiao.domain.accounting

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.validation.CrossValidationResult
import org.xiaotianqi.kuaipiao.enums.AutomationLevel
import kotlin.time.ExperimentalTime

@Serializable
@ExperimentalTime
data class AccountingReconciliationResult(
    val extractedData: InvoiceProcessingResult,
    val reconciliation: AccountingReconciliation,
    val crossValidation: CrossValidationResult,
    val suggestedEntries: List<AccountingEntry>,
    val confidenceScore: Double,
    val automationLevel: AutomationLevel,
    val manualReviewRequired: Boolean = false
)

@Serializable
data class AccountingReconciliation(
    val documentType: String,
    val documentDate: String,
    val currency: String,
    val baseCurrency: String,
    val suggestedAccounts: List<AccountSuggestion>,
    val taxImplications: List<String> = emptyList(),
    val costCenter: String? = null,
    val projectCode: String? = null,
    val validationRules: List<String> = emptyList()
)

@Serializable
data class AccountingPattern(
    val vendorName: String,
    val accountCode: String,
    val description: String,
    val typicalAccounts: List<String>,
    val frequency: Int
)
