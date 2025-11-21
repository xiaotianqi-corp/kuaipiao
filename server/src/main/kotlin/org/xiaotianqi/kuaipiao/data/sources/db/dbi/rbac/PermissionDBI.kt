package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionEntity
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData

@Single(createdAtStart = true)

interface PermissionDBI : DBI {
    suspend fun create(data: PermissionCreateData): PermissionEntity
    suspend fun get(id: DtId<PermissionData>): PermissionEntity?
    suspend fun getByIds(ids: List<DtId<PermissionData>>): List<PermissionEntity>
    suspend fun delete(id: DtId<PermissionData>)
}

