package org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
interface OrganizationDBI : DBI {
    suspend fun create(data: OrganizationCreateData): OrganizationEntity
    suspend fun get(id: DtId<OrganizationData>): OrganizationEntity?
    suspend fun getByCode(code: String): OrganizationEntity?
    suspend fun updateStatus(id: DtId<OrganizationData>, status: EntityStatus)
    suspend fun delete(id: DtId<OrganizationData>)
}