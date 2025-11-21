package org.xiaotianqi.kuaipiao.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class SuperAdminCreateRequest(
    val username: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)