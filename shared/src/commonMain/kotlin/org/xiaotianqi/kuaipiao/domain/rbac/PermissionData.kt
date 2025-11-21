package org.xiaotianqi.kuaipiao.domain.rbac

import kotlinx.serialization.Serializable

@Serializable
data class PermissionData(
    val id: String,
    val code: String,
    val name: String,
    val description: String?
)

@Serializable
data class PermissionCreateData(
    val id: String,
    val code: String,
    val name: String,
    val description: String?
)

@Serializable
data class PermissionResponse(
    val id: String,
    val code: String,
    val name: String,
    val description: String?
)
