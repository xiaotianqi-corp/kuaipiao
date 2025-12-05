package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResendEmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String? = null,
    val text: String? = null,
    val cc: List<String>? = null,
    val bcc: List<String>? = null,
    val reply_to: List<String>? = null,
    val template: ResendEmailTemplate? = null,
    val headers: Map<String, String>? = null,
    val scheduled_at: String? = null,
    val attachments: List<ResendAttachmentRequest>? = null,
    val tags: List<ResendTag>? = null,
) {
    init {
        require(html != null || text != null || template != null) {
            "At least one of html, text, or template must be provided"
        }
    }
}

@Serializable
data class ResendEmailTemplate(
    val id: String,
    val variables: Map<String, JsonElement>? = null,
)

@Serializable
data class ResendAttachmentRequest(
    val content: String? = null,
    val filename: String? = null,
    val path: String? = null,
    val content_type: String? = null,
)

@Serializable
data class ResendTag(
    val name: String,
    val value: String,
)

@Serializable
data class ResendEmailResponse(
    val id: String,
)

@Serializable
data class ResendBatchEmailResponse(
    val data: List<ResendEmailResponse>,
)

@Serializable
data class ResendEmailDetail(
    val `object`: String,
    val id: String,
    val to: List<String>,
    val from: String,
    val created_at: String,
    val subject: String,
    val html: String? = null,
    val text: String? = null,
    val bcc: List<String>? = null,
    val cc: List<String>? = null,
    val reply_to: List<String>? = null,
    val last_event: String,
)