package org.xiaotianqi.kuaipiao.data.daos.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.RoleDBI
import org.xiaotianqi.kuaipiao.domain.rbac.RoleCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData

@Single(createdAtStart = true)
class RoleDao(
    private val roleDBI: RoleDBI,
) {

    suspend fun create(data: RoleCreateData): RoleData {
        val entity = roleDBI.create(data)
        return entity.toDomain()
    }

    suspend fun get(id: DtId<RoleData>): RoleData? {
        return roleDBI.get(id)?.toDomain()
    }

    suspend fun getByName(name: String): RoleData? {
        return roleDBI.getByName(name)?.toDomain()
    }

    suspend fun getByIds(ids: List<DtId<RoleData>>): List<RoleData> {
        if (ids.isEmpty()) return emptyList()
        val entities = roleDBI.getByIds(ids)
        return entities.map { it.toDomain() }
    }

    suspend fun delete(id: DtId<RoleData>) {
        roleDBI.delete(id)
    }
}
