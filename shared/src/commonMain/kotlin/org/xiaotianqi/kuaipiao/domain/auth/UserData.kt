package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String,
    val email: String,
    val passwordHash: String,
    val emailVerified: Boolean,
    val creationTimestamp: Long
)

@Serializable
data class UserCreateData(
    val id: String,
    val email: String,
    val passwordHash: String,
    val emailVerified: Boolean,
    val creationTimestamp: Long
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val creationTimestamp: Long
)