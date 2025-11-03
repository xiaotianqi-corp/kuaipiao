package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSessionCookie(
    val sessionId: String,
    val userId: String
)