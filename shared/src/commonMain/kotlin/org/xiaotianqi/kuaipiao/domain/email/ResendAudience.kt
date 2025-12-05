package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable

@Serializable
data class ResendCreateAudienceRequest(
    val name: String,
)

@Serializable
data class ResendAudienceResponse(
    val id: String,
    val `object`: String,
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
    val `object`: String,
    val data: List<ResendAudienceListItem>,
)

