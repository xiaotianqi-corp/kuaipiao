package org.xiaotianqi.kuaipiao.domain.financial

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FinancialExtractionResult(
    val success: Boolean,
    val extractedData: Map<String, JsonElement>,
    val confidence: Double,
    val missingFields: List<String> = emptyList()
)