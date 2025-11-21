package org.xiaotianqi.kuaipiao.domain.product

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.classification.AlternativeClassification

@Serializable
data class ProductClassificationData(
    val productName: String,
    val tariffCode: String,
    val suggestedCategory: String,
    val accountingAccount: String,
    val taxCategory: String,
    val productCode: String,
    val confidence: Double,
    val alternatives: List<AlternativeClassification> = emptyList(),
    val validationMessage: String? = null,
    val countrySpecific: Map<String, String> = emptyMap()
)

@Serializable
data class ProductBatchClassificationResult(
    val successful: List<ProductClassificationData>,
    val failed: List<ProductClassificationError>,
    val summary: ProductClassificationSummary
)

@Serializable
data class ProductClassificationError(
    val productIndex: Int,
    val productName: String,
    val error: String
)

@Serializable
data class ProductClassificationSummary(
    val totalProducts: Int,
    val classified: Int,
    val failed: Int,
    val categories: Map<String, Int>,
    val averageConfidence: Double,
    val categoryDistribution: Map<String, Int>,
    val processingTime: Long
)