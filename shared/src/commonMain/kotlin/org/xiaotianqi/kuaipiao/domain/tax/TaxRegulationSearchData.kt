package org.xiaotianqi.kuaipiao.domain.tax

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class TaxRegulationSearchResult(
    val country: String,
    val taxType: String,
    val regulations: List<TaxRegulation>,
    val lastUpdated: Instant
) {
    companion object {
        fun empty(): TaxRegulationSearchResult {
            return TaxRegulationSearchResult(
                country = "",
                taxType = "",
                regulations = emptyList(),
                lastUpdated = Clock.System.now()
            )
        }
    }
}

@Serializable
data class TaxRegulation(
    val name: String,
    val description: String,
    val effectiveDate: String,
    val authority: String
)

@Serializable
@ExperimentalTime
data class RegulatoryChange(
    val country: String,
    val description: String,
    val effectiveDate: Instant,
    val impactLevel: String
)