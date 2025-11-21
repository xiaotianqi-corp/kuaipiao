package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.OrganizationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.toData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.ExperimentalTime

@Single
@ExperimentalTime
class OrganizationUseCase(
    private val dao: OrganizationDBI
) {

    suspend fun create(data: OrganizationCreateData): OrganizationData {
        val entity = dao.create(data)
        return entity.toData()
    }

    suspend fun get(id: String): OrganizationData? {
        val entity = dao.getByCode(id)
        return entity?.toData()
    }

    suspend fun updateStatus(id: String, status: EntityStatus) {
        dao.updateStatus(DtId(id), status)
    }

    fun isRecentlyCreated(org: OrganizationData): Boolean {
        val createdMillis = org.createdAt.toEpochMilliseconds()
        val diff = DatetimeUtils.currentMillis() - createdMillis
        return diff < 7L * 24 * 60 * 60 * 1000
    }
}
