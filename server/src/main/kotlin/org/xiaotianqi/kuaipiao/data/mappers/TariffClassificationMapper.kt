package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.trade.TariffRestrictions
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData
import org.xiaotianqi.kuaipiao.domain.trade.QuotaLimits
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalTime
@ExperimentalUuidApi
object TariffClassificationMapper {

    fun mapToTariffClassification(
        product: ProductClassificationData,
        countryOrigin: String,
        countryDestination: String
    ): TariffClassification {

        val restrictions = CountrySpecificHelper.extractRestrictions(product.countrySpecific)

        return TariffClassification(
            productDescription = product.productName,
            tariffCode = product.tariffCode,
            suggestedCategory = product.suggestedCategory,
            accountingAccount = product.accountingAccount,
            taxCategory = product.taxCategory,
            productCode = product.productCode,
            tariffDescription = product.validationMessage
                ?: "Automated tariff classification for ${product.productName}",
            confidence = product.confidence,
            countryOrigin = countryOrigin,
            countryDestination = countryDestination,
            restrictions = restrictions,
            requiredDocuments = CountrySpecificHelper.extractRequiredDocuments(product.countrySpecific),
            taxRate = CountrySpecificHelper.extractTaxRate(product.countrySpecific),
            riskLevel = CountrySpecificHelper.inferRiskLevel(product.confidence),
            alternatives = product.alternatives,
            legalReferences = emptyList()
        )
    }

    private fun inferRiskLevel(confidence: Double): RiskLevel {
        return when {
            confidence >= 0.85 -> RiskLevel.LOW
            confidence >= 0.60 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    }

    private fun extractRequiredDocuments(product: ProductClassificationData): List<String> {
        return product.countrySpecific["requiredDocuments"]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    private fun extractTaxRate(product: ProductClassificationData): Double? {
        return product.countrySpecific["taxRate"]?.toDoubleOrNull()
    }
    
    private fun generateRestrictions(product: ProductClassificationData): TariffRestrictions? {
        val text = product.countrySpecific["restriction"] ?: return null
        val lower = text.lowercase()

        val isRestricted = listOf("restricted", "ban", "forbidden", "prohibited", "limit")
            .any { it in lower }

        val requiresLicense = listOf("license", "permit", "authorization")
            .any { it in lower }

        val embargoCountries = product.countrySpecific["embargoCountries"]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val quota = product.countrySpecific["quota"]?.toIntOrNull()

        return TariffRestrictions(
            isRestricted = isRestricted,
            requiresLicense = requiresLicense,
            quotaLimits = generateQuotaLimits(product),
            embargoCountries = embargoCountries,
            specialRequirements = extractSpecialRequirements(text)
        )
    }

    private fun generateQuotaLimits(product: ProductClassificationData): QuotaLimits? {
        val quotaType = product.countrySpecific["quotaType"] ?: return null
        val limit = product.countrySpecific["quotaLimit"]?.toDoubleOrNull() ?: return null
        val unit = product.countrySpecific["quotaUnit"] ?: "units"
        val period = product.countrySpecific["quotaPeriod"] ?: "annual"
        val used = product.countrySpecific["quotaUsed"]?.toDoubleOrNull() ?: 0.0

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


    private fun extractSpecialRequirements(text: String): List<String> {
        val matches = mutableListOf<String>()
        val lower = text.lowercase()

        if ("inspection" in lower) matches += "Special inspection required"
        if ("certificate" in lower) matches += "Certificate required"
        if ("origin" in lower) matches += "Proof of origin required"
        if ("sanitary" in lower) matches += "Sanitary authorization required"

        return matches
    }
}
