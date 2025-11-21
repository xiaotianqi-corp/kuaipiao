package org.xiaotianqi.kuaipiao.domain.rbac

import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
    val id: String,
    val name: String,
    val description: String?,
    val permissionIds: List<String> = emptyList()
)

@Serializable
data class RoleCreateData(
    val id: String,
    val name: String,
    val description: String?,
    val permissionIds: List<String> = emptyList()
)

@Serializable
data class RoleResponse(
    val id: String,
    val name: String,
    val description: String?,
    val permissionIds: List<String> = emptyList()
)
