package org.xiaotianqi.kuaipiao.domain.accounting

import kotlinx.serialization.Serializable

@Serializable
data class JournalLine(
    val accountCode: String,
    val accountName: String?,
    val description: String?,
    val debit: MoneyData? = null,
    val credit: MoneyData? = null,
    val dimensions: Map<String, String> = emptyMap()
)
