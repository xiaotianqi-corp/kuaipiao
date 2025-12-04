package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val variables: Map<String, Any>? = null,
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
    val object: String,
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

// ========== Domain Models ==========

@Serializable
data class ResendCreateDomainRequest(
    val name: String,
    val region: String = "us-east-1",
)

@Serializable
data class ResendDomainRecord(
    val record: String,
    val name: String,
    val type: String,
    val ttl: String? = null,
    val status: String,
    val value: String,
    val priority: Int? = null,
)

@Serializable
data class ResendDomainResponse(
    val id: String,
    val name: String,
    val created_at: String,
    val status: String,
    val records: List<ResendDomainRecord>? = null,
    val region: String,
)

@Serializable
data class ResendDomainItem(
    val id: String,
    val name: String,
    val status: String,
    val created_at: String,
    val region: String,
)

@Serializable
data class ResendDomainsListResponse(
    val data: List<ResendDomainItem>,
)

// ========== API Key Models ==========

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

// ========== Template Models ==========

@Serializable
data class ResendCreateTemplateRequest(
    val name: String,
    val html: String,
    val alias: String? = null,
    val from: String? = null,
    val subject: String? = null,
    val reply_to: List<String>? = null,
    val text: String? = null,
    val variables: List<ResendTemplateVariableInput>? = null,
)

@Serializable
data class ResendTemplateVariableInput(
    val key: String,
    val type: String,
    val fallback_value: Any? = null,
)

@Serializable
data class ResendTemplateVariable(
    val id: String,
    val key: String,
    val type: String,
    val fallback_value: Any? = null,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class ResendTemplateResponse(
    val id: String,
    val object: String,
)

@Serializable
data class ResendTemplateDetail(
    val object: String,
    val id: String,
    val current_version_id: String,
    val name: String,
    val alias: String? = null,
    val from: String? = null,
    val subject: String? = null,
    val reply_to: List<String>? = null,
    val html: String,
    val text: String? = null,
    val variables: List<ResendTemplateVariable>? = null,
    val created_at: String,
    val updated_at: String,
    val status: String,
    val published_at: String? = null,
    val has_unpublished_versions: Boolean,
)

@Serializable
data class ResendTemplateListItem(
    val id: String,
    val name: String,
    val status: String,
    val published_at: String? = null,
    val created_at: String,
    val updated_at: String,
    val alias: String? = null,
)

@Serializable
data class ResendTemplatesListResponse(
    val object: String,
    val data: List<ResendTemplateListItem>,
    val has_more: Boolean,
)

// ========== Audience Models ==========

@Serializable
data class ResendCreateAudienceRequest(
    val name: String,
)

@Serializable
data class ResendAudienceResponse(
    val id: String,
    val object: String,
    val name: String,
)

@Serializable
data class ResendAudienceListItem(
    val id: String,
    val name: String,
    val created_at: String,
)

@Serializable
data class ResendAudiencesListResponse(
    val object: String,
    val data: List<ResendAudienceListItem>,
)

// ========== Contact Models ==========

@Serializable
data class ResendCreateContactRequest(
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val unsubscribed: Boolean = false,
)

@Serializable
data class ResendContactResponse(
    val object: String,
    val id: String,
)

@Serializable
data class ResendContactDetail(
    val object: String,
    val id: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val created_at: String,
    val unsubscribed: Boolean,
)

@Serializable
data class ResendContactsListResponse(
    val object: String,
    val data: List<ResendContactDetail>,
)

// ========== Webhook Models ==========

@Serializable
data class ResendCreateWebhookRequest(
    val endpoint: String,
    val events: List<String>,
)

@Serializable
data class ResendWebhookResponse(
    val object: String,
    val id: String,
    val signing_secret: String,
)

@Serializable
data class ResendWebhookDetail(
    val object: String,
    val id: String,
    val endpoint: String,
    val events: List<String>? = null,
    val status: String,
    val created_at: String,
    val signing_secret: String,
)

@Serializable
data class ResendWebhooksListResponse(
    val object: String,
    val has_more: Boolean,
    val data: List<ResendWebhookDetail>,
)