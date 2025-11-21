package org.xiaotianqi.kuaipiao.domain.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductInfo(
    val name: String,
    val description: String? = null,
    val category: String? = null
)

@Serializable
data class ProductDescription(
    val name: String,
    val description: String,
    val sku: String? = null,
    val brand: String? = null,
    val attributes: Map<String, String> = emptyMap(),
    val additionalInfo: Map<String, String> = emptyMap()
)