package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthSessionData(
    val id: String,
    val userId: String,
    val iat: Long,
    val deviceName: String?,
    val ip: String
)