package org.xiaotianqi.kuaipiao.domain.accounting

import kotlinx.serialization.Serializable


@Serializable
data class MoneyData(
    val amount: Double,
    val currency: String,
    val exchangeRateToBase: Double = 1.0
) {
    val baseAmount: Double get() = amount * exchangeRateToBase
}