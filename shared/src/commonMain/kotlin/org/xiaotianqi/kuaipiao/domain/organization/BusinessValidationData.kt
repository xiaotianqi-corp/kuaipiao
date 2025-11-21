package org.xiaotianqi.kuaipiao.domain.organization

import kotlinx.serialization.Serializable

@Serializable
data class BusinessDataValidationRequest(
    val companyName: String,
    val taxId: String,
    val country: String,
    val dataType: String
)

@Serializable
data class BusinessDataValidationResult(
    val isValid: Boolean,
    val confidence: Double,
    val sources: List<String>,
    val warnings: List<String>
)