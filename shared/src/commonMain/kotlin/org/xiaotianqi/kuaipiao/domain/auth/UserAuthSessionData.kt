package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthSessionData(
    val id: String,
    val userId: String,
    val iat: Long,
    val deviceName: String?,
    val ip: String,
    val token: String,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList()
)

@Serializable
data class UserSessionData(
    val sessionId: String,
    val userId: String,
    val token: String,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList()
)