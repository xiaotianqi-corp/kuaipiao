package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RoleEntity
import org.xiaotianqi.kuaipiao.domain.rbac.RoleCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData

@Single(createdAtStart = true)

interface RoleDBI : DBI {
    suspend fun create(data: RoleCreateData): RoleEntity
    suspend fun get(id: DtId<RoleData>): RoleEntity?
    suspend fun getByIds(ids: List<DtId<RoleData>>): List<RoleEntity>
    suspend fun getByName(name: String): RoleEntity?
    suspend fun delete(id: DtId<RoleData>)
}

