package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResendCreateWebhookRequest(
    val endpoint: String,
    val events: List<String>,
)

@Serializable
data class ResendWebhookResponse(
    val `object`: String,
    val id: String,
    val signing_secret: String,
)

@Serializable
data class ResendWebhookDetail(
    val `object`: String,
    val id: String,
    val endpoint: String,
    val events: List<String>? = null,
    val status: String,
    val created_at: String,
    val signing_secret: String,
)

@Serializable
data class ResendWebhooksListResponse(
    val `object`: String,
    val has_more: Boolean,
    val data: List<ResendWebhookDetail>,
)

@Serializable
data class CreateWebhookDTO(
    val type: String,
    val data: Map<String, JsonElement>? = null,
)
