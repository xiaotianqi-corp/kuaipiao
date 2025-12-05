package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
    val variables: Map<String, JsonElement>? = null,
)

@Serializable
data class ResendTemplateVariable(
    val id: String,
    val key: String,
    val type: String,
    val variables: Map<String, JsonElement>? = null,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class ResendTemplateResponse(
    val id: String,
    val `object`: String,
)

@Serializable
data class ResendTemplateDetail(
    val `object`: String,
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
    val `object`: String,
    val data: List<ResendTemplateListItem>,
    val has_more: Boolean,
)
