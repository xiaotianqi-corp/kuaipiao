package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable

@Serializable
data class ResendCreateContactRequest(
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val unsubscribed: Boolean = false,
)

@Serializable
data class ResendContactResponse(
    val `object`: String,
    val id: String,
)

@Serializable
data class ResendContactDetail(
    val `object`: String,
    val id: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val created_at: String,
    val unsubscribed: Boolean,
)

@Serializable
data class ResendContactsListResponse(
    val `object`: String,
    val data: List<ResendContactDetail>,
)