package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable


@Serializable
data class SendEmailDTO(
    val from: String? = null,
    val to: List<String>,
    val subject: String,
    val html: String? = null,
    val text: String? = null,
    val cc: List<String>? = null,
    val bcc: List<String>? = null,
    val replyTo: List<String>? = null,
    val tags: List<Map<String, String>>? = null,
)

@Serializable
data class SendBatchEmailDTO(
    val emails: List<SendEmailDTO>,
)

@Serializable
data class CreateTemplateDTO(
    val name: String,
    val html: String,
    val alias: String? = null,
    val from: String? = null,
    val subject: String? = null,
    val replyTo: List<String>? = null,
    val text: String? = null,
)

@Serializable
data class CreateAudienceDTO(
    val name: String,
)

@Serializable
data class CreateContactDTO(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
)

@Serializable
data class CreateWebhookDTO(
    val endpoint: String,
    val events: List<String>,
)