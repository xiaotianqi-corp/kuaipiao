package org.xiaotianqi.kuaipiao.domain.trade

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.classification.AlternativeClassification
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@ExperimentalTime
data class TariffCode(
    val code: String,
    val description: String,
    val parentCode: String? = null,
    val level: Int,
    val category: String,
    val unit: String? = null,
    val generalRate: Double,
    val preferentialRates: Map<String, Double> = emptyMap(),
    val restrictions: List<String> = emptyList(),
    val requiredDocuments: List<String> = emptyList(),
    val notes: String? = null,
    val lastUpdated: Instant,
    val validFrom: Instant,
    val validTo: Instant? = null
)

@Serializable
data class TariffRestrictions(
    val isRestricted: Boolean,
    val requiresLicense: Boolean,
    val quotaLimits: QuotaLimits? = null,
    val embargoCountries: List<String> = emptyList(),
    val specialRequirements: List<String> = emptyList()
)

@Serializable
data class TariffSearchRequest(
    val productDescription: String,
    val countryOrigin: String,
    val countryDestination: String,
    val includeRestrictions: Boolean = true,
    val includeRates: Boolean = true,
    val language: String = "es"
)

@Serializable
@ExperimentalTime
@ExperimentalUuidApi
data class TariffSearchResponse(
    val success: Boolean,
    val classifications: List<TariffClassification>,
    val tradeAgreements: List<TradeAgreement>,
    val warnings: List<String>,
    val processingTime: Long
)

@Serializable
data class QuotaLimits(
    val quotaType: String,
    val limit: Double,
    val unit: String,
    val period: String,
    val used: Double = 0.0,
    val remaining: Double = 0.0
)

@Serializable
data class TariffClassificationRequest(
    val productDescription: String,
    val destinationCountry: String,
    val additionalContext: Map<String, String> = emptyMap()
)

@Serializable
data class TariffClassificationResponse(
    val hsCode: String,
    val description: String,
    val confidence: Double,
    val alternativeCodes: List<AlternativeCode> = emptyList(),
    val requiredDocuments: List<String>,
    val restrictions: List<String> = emptyList()
)

@Serializable
data class AlternativeCode(
    val hsCode: String,
    val description: String,
    val confidence: Double
)