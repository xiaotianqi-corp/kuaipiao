// File: Converters.kt (actualizado)
package org.xiaotianqi.kuaipiao.domain.accounting

import kotlin.time.ExperimentalTime
import org.xiaotianqi.kuaipiao.enums.AccountingType
import kotlin.time.Clock

@ExperimentalTime
fun flattenJournalToAccountingEntries(journal: JournalEntry): List<AccountingEntry> {
    return journal.lines.mapIndexed { idx, line ->
        val isDebit = line.debit != null
        val money = line.debit ?: line.credit!!
        val baseDebit = line.debit?.baseAmount ?: 0.0
        val baseCredit = line.credit?.baseAmount ?: 0.0
        val signedBaseAmount = baseDebit - baseCredit
        val signedForeign = (line.debit?.amount ?: 0.0) - (line.credit?.amount ?: 0.0)

        AccountingEntry(
            entryId = "${journal.id}-$idx",
            journalId = journal.id,
            ledgerBook = journal.ledger,
            type = if (signedBaseAmount >= 0) listOf(AccountingType.DEBIT) else listOf(AccountingType.CREDIT),
            accountCode = line.accountCode,
            accountName = line.accountName ?: line.description,
            debit = if (signedBaseAmount >= 0) signedBaseAmount else 0.0,
            credit = if (signedBaseAmount < 0) -signedBaseAmount else 0.0,
            currency = line.debit?.currency ?: line.credit?.currency ?: "",
            amount = kotlin.math.abs(signedForeign),
            foreignAmount = if ((line.debit?.exchangeRateToBase ?: line.credit?.exchangeRateToBase ?: 1.0) != 1.0) signedForeign else null,
            exchangeRate = line.debit?.exchangeRateToBase ?: line.credit?.exchangeRateToBase ?: 1.0,
            postingDate = journal.date.toString(),
            transactionDate = journal.date.toString(),
            reference = journal.reference,
            description = line.description ?: journal.description,
            dimensions = AccountingDimension.fromMap(line.dimensions),
            provenance = journal.provenance["source"],
            confidence = journal.provenance["confidence"]?.toDoubleOrNull() ?: 1.0,
            createdAt = Clock.System.now(),
            createdBy = journal.createdBy
        )
    }
}
