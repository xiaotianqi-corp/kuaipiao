package org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.OrganizationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationsTable
import org.xiaotianqi.kuaipiao.data.mappers.fromCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.UUID
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class OrganizationDBIImpl : OrganizationDBI {

    override suspend fun create(data: OrganizationCreateData): OrganizationEntity =
        dbQuery {
            OrganizationEntity.new(UUID.fromString(data.id)) {
                fromCreateData(data)
            }
        }

    override suspend fun get(id: DtId<OrganizationData>): OrganizationEntity? =
        dbQuery {
            OrganizationEntity.findById(id.id)
        }

    override suspend fun getByCode(code: String): OrganizationEntity? =
        dbQuery {
            OrganizationEntity
                .find { OrganizationsTable.code eq code }
                .limit(1)
                .firstOrNull()
        }

    override suspend fun updateStatus(id: DtId<OrganizationData>, status: EntityStatus) {
        dbQuery {
            OrganizationsTable.update({ OrganizationsTable.id eq id.toEntityId(OrganizationsTable) }) {
                it[OrganizationsTable.status] = status
            }
        }
    }

    override suspend fun delete(id: DtId<OrganizationData>) {
        dbQuery {
            OrganizationsTable.deleteWhere { OrganizationsTable.id eq id.toEntityId(OrganizationsTable) }
        }
    }
}
