package org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise.impl

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise.EnterpriseDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterprisesTable
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseData
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.time.Instant
import java.util.UUID
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class EnterpriseDBIImpl : EnterpriseDBI {

    override suspend fun create(enterpriseData: EnterpriseCreateData): EnterpriseEntity = dbQuery {
        EnterpriseEntity.new(UUID.randomUUID()) {
            subdomain = enterpriseData.subdomain
            domain = enterpriseData.domain
            plan = enterpriseData.plan
            status = EntityStatus.ACTIVE
            settings = Json.encodeToString(enterpriseData.settings)
            metadata = Json.encodeToString(enterpriseData.metadata)
            createdAt = Instant.now()
            updatedAt = null
            expiresAt = null
        }
    }

    override suspend fun get(id: DtId<EnterpriseData>): EnterpriseEntity? = dbQuery {
        EnterpriseEntity.findById(id.id)
    }

    override suspend fun getBySubdomain(subdomain: String): EnterpriseEntity? = dbQuery {
        EnterpriseEntity.find { EnterprisesTable.subdomain eq subdomain }
            .limit(1)
            .firstOrNull()
    }

    override suspend fun updateStatus(id: DtId<EnterpriseData>, status: EntityStatus) {
        dbQuery {
            EnterprisesTable.update({ EnterprisesTable.id eq id.toEntityId(EnterprisesTable) }) {
                it[EnterprisesTable.status] = status
                it[EnterprisesTable.updatedAt] = Instant.now()
            }
        }
    }

    override suspend fun updatePlan(id: DtId<EnterpriseData>, plan: EnterprisePlan) {
        dbQuery {
            EnterprisesTable.update({ EnterprisesTable.id eq id.toEntityId(EnterprisesTable) }) {
                it[EnterprisesTable.plan] = plan
                it[EnterprisesTable.updatedAt] = Instant.now()
            }
        }
    }

    override suspend fun delete(id: DtId<EnterpriseData>) {
        dbQuery {
            EnterprisesTable.deleteWhere { EnterprisesTable.id eq id.toEntityId(EnterprisesTable) }
        }
    }
}
