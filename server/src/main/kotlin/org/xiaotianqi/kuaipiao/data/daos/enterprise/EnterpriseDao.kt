package org.xiaotianqi.kuaipiao.data.daos.enterprise

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise.EnterpriseDBI
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseData
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class EnterpriseDao(
    private val enterpriseDBI: EnterpriseDBI,
) {

    suspend fun create(enterpriseData: EnterpriseCreateData): EnterpriseData {
        val entity = enterpriseDBI.create(enterpriseData)
        return entity.toDomain()
    }

    suspend fun get(id: DtId<EnterpriseData>): EnterpriseData? {
        return enterpriseDBI.get(id)?.toDomain()
    }

    suspend fun getBySubdomain(subdomain: String): EnterpriseData? {
        return enterpriseDBI.getBySubdomain(subdomain)?.toDomain()
    }

    suspend fun updateStatus(id: DtId<EnterpriseData>, status: EntityStatus) {
        enterpriseDBI.updateStatus(id, status)
    }

    suspend fun updatePlan(id: DtId<EnterpriseData>, plan: EnterprisePlan) {
        enterpriseDBI.updatePlan(id, plan)
    }

    suspend fun delete(id: DtId<EnterpriseData>) {
        enterpriseDBI.delete(id)
    }
}
