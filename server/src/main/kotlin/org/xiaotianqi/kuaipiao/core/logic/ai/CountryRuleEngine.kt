package org.xiaotianqi.kuaipiao.core.logic.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.organization.CountryRules
import org.xiaotianqi.kuaipiao.domain.tax.RegulatoryChange
import org.xiaotianqi.kuaipiao.domain.trade.ExportRules
import org.xiaotianqi.kuaipiao.domain.trade.QuotaLimits
import org.xiaotianqi.kuaipiao.domain.trade.TariffRestrictions
import org.xiaotianqi.kuaipiao.domain.trade.TradeRules
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

@ExperimentalTime
class CountryRuleEngine {

    private val countryRules = mapOf(
        "EC" to CountryRules(
            currency = "USD",
            taxRates = Json.encodeToJsonElement(
                mapOf(
                    "IVA" to 0.12,
                    "ICE" to mapOf(
                        "alcohol" to 0.30,
                        "cigarrillos" to 0.50
                    )
                )
            ).jsonObject,
            requiredDocuments = listOf("RUC", "Invoice", "Withholding Certificate")
        ),
        "US" to CountryRules(
            currency = "USD",
            taxRates = Json.encodeToJsonElement(
                mapOf(
                    "sales_tax" to mapOf(
                        "CA" to 0.0725,
                        "NY" to 0.04
                    )
                )
            ).jsonObject,
            requiredDocuments = listOf("EIN", "Invoice", "W-9")
        )
    )

    fun getExportRules(origin: String, destination: String): ExportRules {
        return ExportRules(
            originCountry = origin,
            destinationCountry = destination,
            isTradeAllowed = isTradeAllowed(origin, destination),
            requiredDocuments = getRequiredDocuments(origin, destination),
            restrictions = getTradeRestrictions(origin, destination),
            taxTreaties = getTaxTreaties(origin, destination)
        )
    }

    fun getTradeRules(origin: String, destination: String): TradeRules {
        return TradeRules(
            isTradeAllowed = isTradeAllowed(origin, destination),
            requiresLicense = requiresExportLicense(origin, destination),
            prohibitedProducts = getProhibitedProducts(origin, destination)
        )
    }

    fun validateTariffRestrictions(
        tariffCode: String,
        origin: String,
        destination: String
    ): TariffRestrictions {
        return TariffRestrictions(
            isRestricted = isProductRestricted(tariffCode, origin, destination),
            requiresLicense = requiresProductLicense(tariffCode, origin, destination),
            quotaLimits = getQuotaLimits(tariffCode, origin, destination)
        )
    }

    fun getRecentRegulatoryChanges(period: DateRange): List<RegulatoryChange> {
        val effectiveInstant = Instant.now().minusSeconds(30 * 24 * 3600L)
        return listOf(
            RegulatoryChange(
                country = "EC",
                description = "Nuevo formato de facturación electrónica",
                effectiveDate = effectiveInstant.toKotlinInstant(),
                impactLevel = "HIGH"
            )
        )
    }

    private fun isTradeAllowed(origin: String, destination: String): Boolean {
        val restrictedPairs = setOf(
            "US-CU", "CU-US", "US-IR", "IR-US"
        )
        return "$origin-$destination" !in restrictedPairs
    }

    private fun getRequiredDocuments(origin: String, destination: String): List<String> {
        val baseDocs = listOf("Commercial Invoice", "Packing List")

        return when {
            origin == "EC" && destination == "US" -> baseDocs + "Certificate of Origin"
            origin == "US" && destination == "EC" -> baseDocs + "Export Declaration"
            else -> baseDocs
        }
    }

    private fun isProductRestricted(tariffCode: String, origin: String, destination: String): Boolean {
        val restrictedProducts = mapOf(
            "EC-US" to setOf("0301", "0302"),
            "US-EC" to setOf("8703")
        )

        return restrictedProducts["$origin-$destination"]?.any {
            tariffCode.startsWith(it)
        } ?: false
    }

    private fun requiresExportLicense(origin: String, destination: String): Boolean {
        return when {
            origin == "US" && destination in setOf("CN", "RU") -> true
            else -> false
        }
    }

    private fun requiresProductLicense(tariffCode: String, origin: String, destination: String): Boolean {
        val licensedProducts = mapOf(
            "EC-US" to setOf("999999"),
            "US-EC" to setOf("999999")
        )

        return licensedProducts["$origin-$destination"]?.contains(tariffCode) ?: false
    }

    private fun getQuotaLimits(tariffCode: String, origin: String, destination: String): QuotaLimits? {
        return null
    }

    private fun getTradeRestrictions(origin: String, destination: String): List<String> {
        return emptyList()
    }

    private fun getTaxTreaties(origin: String, destination: String): List<String> {
        return when {
            setOf(origin, destination) == setOf("EC", "US") -> listOf("Treaty for Avoidance of Double Taxation")
            else -> emptyList()
        }
    }

    private fun getProhibitedProducts(origin: String, destination: String): List<String> {
        return emptyList()
    }
}