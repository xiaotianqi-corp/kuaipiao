package org.xiaotianqi.kuaipiao.domain.organization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.xiaotianqi.kuaipiao.domain.address.AddressData
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.enums.AuditStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Serializable
@ExperimentalTime
data class CompanyInfo(
    val name: String,
    val taxId: String,
    val address: AddressData,
    val contact: ContactInfo,
    val createdAt: Instant,
    val updatedAt: Instant?
)

@Serializable
data class ContactInfo(
    val phone: String,
    val email: String,
    val contactPerson: String
)

@Serializable
@ExperimentalTime
data class CompanyHistory(
    val companyId: String,
    val fiscalYears: List<FiscalYearInfo> = emptyList(),
    val previousComplianceIssues: String? = null,
    val establishedDate: Instant? = null,
    val summary: String? = null
) {
    companion object {
        fun fromTransactions(
            companyId: String,
            transactions: List<TransactionData>,
            period: DateRange
        ): CompanyHistory {
            val totalRevenue = transactions.sumOf { it.amount }
            val summary = buildString {
                append("Analysis of ${transactions.size} transactions ")
                append("from ${period.start} to ${period.end}.")
                append("Total amount: $totalRevenue")
            }

            return CompanyHistory(
                companyId = companyId,
                fiscalYears = emptyList(),
                previousComplianceIssues = null,
                establishedDate = null,
                summary = summary
            )
        }
    }
}

@Serializable
@ExperimentalTime
data class FiscalYearInfo(
    val year: Int,
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netIncome: Double,
    val auditStatus: AuditStatus
)

@Serializable
data class CountryRules(
    val currency: String,
    val taxRates: Map<String, JsonElement>,
    val requiredDocuments: List<String>
)