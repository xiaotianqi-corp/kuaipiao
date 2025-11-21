package org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface EnterpriseDBI : DBI {
    suspend fun create(enterpriseData: EnterpriseCreateData): EnterpriseEntity
    suspend fun get(id: DtId<EnterpriseData>): EnterpriseEntity?
    suspend fun getBySubdomain(subdomain: String): EnterpriseEntity?
    suspend fun updateStatus(id: DtId<EnterpriseData>, status: EntityStatus)
    suspend fun updatePlan(id: DtId<EnterpriseData>, plan: EnterprisePlan)
    suspend fun delete(id: DtId<EnterpriseData>)
}
