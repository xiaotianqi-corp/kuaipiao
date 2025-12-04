package org.xiaotianqi.kuaipiao.data.daos.organization

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.OrganizationDBI
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class OrganizationDao(
    private val organizationDBI: OrganizationDBI,
) {

    suspend fun create(data: OrganizationCreateData, userEntities: List<UserEntity> = emptyList()): OrganizationData {
        val entity = organizationDBI.create(data, userEntities)
        return entity.toDomain()
    }

    suspend fun get(id: DtId<OrganizationData>): OrganizationData? {
        return organizationDBI.get(id)?.toDomain()
    }

    suspend fun getByCode(code: String): OrganizationData? {
        return organizationDBI.getByCode(code)?.toDomain()
    }

    suspend fun updateStatus(id: DtId<OrganizationData>, status: EntityStatus) {
        organizationDBI.updateStatus(id, status)
    }

    suspend fun delete(id: DtId<OrganizationData>) {
        organizationDBI.delete(id)
    }
}