package org.xiaotianqi.kuaipiao.domain.product

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class TariffSearchResult(
    val productDescription: String,
    val suggestions: List<TariffSuggestion>,
    val sources: List<String>,
    val lastUpdated: Instant
) {
    companion object {
        fun empty(): TariffSearchResult {
            return TariffSearchResult(
                productDescription = "",
                suggestions = emptyList(),
                sources = emptyList(),
                lastUpdated = Clock.System.now()
            )
        }
    }
}

@Serializable
data class TariffSuggestion(
    val country: String,
    val code: String,
    val description: String,
    val confidence: Double
)
