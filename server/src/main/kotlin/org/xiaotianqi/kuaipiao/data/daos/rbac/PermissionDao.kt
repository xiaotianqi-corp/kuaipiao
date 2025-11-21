package org.xiaotianqi.kuaipiao.data.daos.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.PermissionDBI
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData

@Single(createdAtStart = true)
class PermissionDao(
    private val permissionDBI: PermissionDBI,
) {

    suspend fun create(data: PermissionCreateData): PermissionData {
        val entity = permissionDBI.create(data)
        return entity.toDomain()
    }

    suspend fun get(id: DtId<PermissionData>): PermissionData? {
        return permissionDBI.get(id)?.toDomain()
    }

    suspend fun getByIds(ids: List<DtId<PermissionData>>): List<PermissionData> {
        if (ids.isEmpty()) return emptyList()
        val entities = permissionDBI.getByIds(ids)
        return entities.map { it.toDomain() }
    }

    suspend fun delete(id: DtId<PermissionData>) {
        permissionDBI.delete(id)
    }
}
