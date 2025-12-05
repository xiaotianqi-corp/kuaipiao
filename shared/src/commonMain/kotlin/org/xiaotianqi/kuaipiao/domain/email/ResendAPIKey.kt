package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable

@Serializable
data class ResendCreateApiKeyRequest(
    val name: String,
    val permission: String = "full_access",
    val domain_id: String? = null,
)

@Serializable
data class ResendApiKeyResponse(
    val id: String,
    val token: String,
)

