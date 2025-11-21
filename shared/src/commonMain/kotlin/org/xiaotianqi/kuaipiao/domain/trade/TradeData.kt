package org.xiaotianqi.kuaipiao.domain.trade

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.tax.TaxDetail

@Serializable
data class CalculationBreakdown(
    val itemNumber: Int,
    val productDescription: String,
    val duties: List<DutyCalculation>,
    val taxes: List<TaxDetail>,
    val total: Double
)

@Serializable
data class TradeAgreement(
    val name: String,
    val countries: List<String>,
    val effectiveDate: LocalDateTime,
    val tariffReductions: Map<String, Double>,
    val rulesOfOrigin: List<String>,
    val certificateRequirements: List<String>
)

@Serializable
data class TradeRules(
    val isTradeAllowed: Boolean,
    val requiresLicense: Boolean,
    val prohibitedProducts: List<String>
)
