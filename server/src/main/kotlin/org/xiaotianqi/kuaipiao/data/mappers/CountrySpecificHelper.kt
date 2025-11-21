package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.trade.QuotaLimits
import org.xiaotianqi.kuaipiao.domain.trade.TariffRestrictions
import org.xiaotianqi.kuaipiao.enums.RiskLevel

object CountrySpecificHelper {

    fun extractRestrictions(countrySpecific: Map<String, String>): TariffRestrictions? {
        val restrictionText = countrySpecific["restriction"] ?: return null
        val lower = restrictionText.lowercase()

        val isRestricted = listOf("restricted", "ban", "forbidden", "prohibited", "limit")
            .any { it in lower }

        val requiresLicense = listOf("license", "permit", "authorization")
            .any { it in lower }

        val embargoCountries = countrySpecific["embargoCountries"]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        return TariffRestrictions(
            isRestricted = isRestricted,
            requiresLicense = requiresLicense,
            embargoCountries = embargoCountries,
            specialRequirements = extractSpecialRequirements(lower),
            quotaLimits = extractQuotaLimits(countrySpecific)
        )
    }

    private fun extractSpecialRequirements(lowerText: String): List<String> {
        val special = mutableListOf<String>()
        if ("inspection" in lowerText) special += "Special inspection required"
        if ("certificate" in lowerText) special += "Certificate required"
        if ("origin" in lowerText) special += "Proof of origin required"
        if ("sanitary" in lowerText) special += "Sanitary authorization required"
        return special
    }

    private fun extractQuotaLimits(countrySpecific: Map<String, String>): QuotaLimits? {
        val quotaType = countrySpecific["quotaType"] ?: return null
        val limit = countrySpecific["quotaLimit"]?.toDoubleOrNull() ?: return null
        val unit = countrySpecific["quotaUnit"] ?: "units"
        val period = countrySpecific["quotaPeriod"] ?: "annual"
        val used = countrySpecific["quotaUsed"]?.toDoubleOrNull() ?: 0.0
        val remaining = (limit - used).coerceAtLeast(0.0)

        return QuotaLimits(
            quotaType = quotaType,
            limit = limit,
            unit = unit,
            period = period,
            used = used,
            remaining = remaining
        )
    }

    fun extractRequiredDocuments(countrySpecific: Map<String, String>): List<String> {
        return countrySpecific["requiredDocuments"]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    fun extractTaxRate(countrySpecific: Map<String, String>): Double? {
        return countrySpecific["taxRate"]?.toDoubleOrNull()
    }

    fun inferRiskLevel(confidence: Double): RiskLevel {
        return when {
            confidence >= 0.85 -> RiskLevel.LOW
            confidence >= 0.60 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    }
}
