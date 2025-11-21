package org.xiaotianqi.kuaipiao.domain.accounting

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.time.ExperimentalTime
import org.xiaotianqi.kuaipiao.enums.LedgerBook

@Serializable
@ExperimentalTime
data class JournalEntry(
    val id: String,
    val ledger: LedgerBook,
    val date: Instant,
    val reference: String?,
    val description: String?,
    val lines: List<JournalLine>,
    val sourceDocumentType: String,
    val sourceDocumentId: String,
    val createdBy: String,
    val createdAt: Instant,
    val provenance: Map<String, String> = emptyMap()
) {
    fun balanceCheck(): Boolean {
        val totalDebit = lines.sumOf { it.debit?.baseAmount ?: 0.0 }
        val totalCredit = lines.sumOf { it.credit?.baseAmount ?: 0.0 }
        return kotlin.math.abs(totalDebit - totalCredit) < 0.000001
    }
}
