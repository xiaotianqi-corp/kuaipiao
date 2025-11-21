package org.xiaotianqi.kuaipiao.data.mappers


import org.jetbrains.exposed.sql.SizedCollection
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RoleEntity
import org.xiaotianqi.kuaipiao.domain.rbac.RoleCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleResponse

fun RoleEntity.toDomain() = RoleData(
    id = id.value.toString(),
    name = name,
    description = description,
    permissionIds = permissions.map { it.id.value.toString() }
)

fun RoleEntity.toResponse() = RoleResponse(
    id = id.value.toString(),
    name = name,
    description = description,
    permissionIds = permissions.map { it.id.value.toString() }
)

fun RoleData.toResponse() = RoleResponse(
    id = id,
    name = name,
    description = description,
    permissionIds = permissionIds
)

fun RoleEntity.fromCreateData(
    data: RoleCreateData,
    permissionEntities: List<PermissionEntity> = emptyList()
) {
    permissions = SizedCollection(permissionEntities)
    name = data.name
    description = data.description
}
