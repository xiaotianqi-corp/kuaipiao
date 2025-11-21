package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class UserData(
    val id: String,
    val username: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean,
    val passwordHash: String,
    val enterpriseId: String? = null,
    val organizationIds: List<String> = emptyList(),
    val roleIds: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
    val lastLoginAt: Instant? = null,
)

@Serializable
@ExperimentalTime
data class UserCreateData(
    val id: String,
    val username: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean,
    val passwordHash: String,
    val enterpriseId: String? = null,
    val organizationIds: List<String> = emptyList(),
    val roleIds: List<String> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
)


@Serializable
@ExperimentalTime
data class UserResponse(
    val id: String,
    val username: String? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val enterpriseId: String? = null,
    val organizationIds: List<String> = emptyList(),
    val roleIds: List<String> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
)