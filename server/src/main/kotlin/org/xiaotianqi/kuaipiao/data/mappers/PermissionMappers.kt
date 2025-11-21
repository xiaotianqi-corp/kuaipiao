package org.xiaotianqi.kuaipiao.data.mappers


import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionEntity
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionResponse

fun PermissionEntity.toDomain() = PermissionData(
    id = id.value.toString(),
    code = code,
    name = name,
    description = description
)

fun PermissionEntity.toResponse() = PermissionResponse(
    id = id.value.toString(),
    code = code,
    name = name,
    description = description
)

fun PermissionData.toResponse() = PermissionResponse(
    id = id,
    code = code,
    name = name,
    description = description
)

fun PermissionEntity.fromCreateData(
    data: PermissionCreateData,
) {
    code = data.code
    name = data.name
    description = data.description
}
