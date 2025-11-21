package org.xiaotianqi.kuaipiao.domain.tax

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRisk
import org.xiaotianqi.kuaipiao.enums.TaxType

@Serializable
data class TaxComplianceAnalysis(
    val isCompliant: Boolean,
    val applicableTaxes: List<TaxDetail>,
    val requiredDocuments: List<String>,
    val risks: List<ComplianceRisk>,
    val confidence: Double
)

@Serializable
data class TaxDetail(
    val type: String,
    val rate: Double,
    val taxableBase: Double? = null,
    val amount: Double? = null,
    val description: String? = null,
    val isExempt: Boolean = false,
    val isRetention: Boolean = false
)

@Serializable
data class TaxCalculationData(
    val subtotal: String,
    val taxRate: String,
    val taxAmount: String,
    val total: String,
    val breakdown: List<TaxBreakdownItem> = emptyList()
)
@Serializable
data class TaxBreakdownItem(
    val name: String,
    val rate: String,
    val amount: String,
    val type: TaxType
)