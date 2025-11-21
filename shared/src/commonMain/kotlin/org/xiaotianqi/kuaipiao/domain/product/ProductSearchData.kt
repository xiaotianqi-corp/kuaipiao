package org.xiaotianqi.kuaipiao.domain.product

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class ProductSearchResult(
    val productName: String,
    val category: String,
    val taxCategory: String,
    val descriptions: List<String>,
    val sources: List<String>,
    val confidence: Double,
    val lastUpdated: Instant
) {
    companion object {
        fun empty(): ProductSearchResult {
            return ProductSearchResult(
                productName = "",
                category = "UNKNOWN",
                taxCategory = "UNKNOWN",
                descriptions = emptyList(),
                sources = emptyList(),
                confidence = 0.0,
                lastUpdated = Clock.System.now()
            )
        }
    }
}