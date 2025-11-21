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
@ExperimentalUuidApi
data class TariffClassification(
    val id: String = Uuid.random().toString(),
    val productDescription: String,
    var tariffCode: String,
    var suggestedCategory: String,
    var accountingAccount: String,
    var taxCategory: String,
    var productCode: String,
    val tariffDescription: String,
    val confidence: Double,
    val countryOrigin: String,
    val countryDestination: String,
    var restrictions: TariffRestrictions? = null,
    var requiredDocuments: List<String> = emptyList(),
    val taxRate: Double? = null,
    var riskLevel: RiskLevel = RiskLevel.LOW,
    val alternatives: List<AlternativeClassification> = emptyList(),
    val legalReferences: List<String> = emptyList(),
    val lastUpdated: Instant = Clock.System.now()
)