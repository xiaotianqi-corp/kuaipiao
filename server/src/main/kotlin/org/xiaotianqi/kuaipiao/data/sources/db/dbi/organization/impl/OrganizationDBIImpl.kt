package org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.impl

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.OrganizationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationsTable
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@Single(createdAtStart = true)
@ExperimentalTime
class OrganizationDBIImpl : OrganizationDBI {

    override suspend fun create(data: OrganizationCreateData, userEntities: List<UserEntity>): OrganizationEntity = dbQuery {
        OrganizationEntity.new(UUID.fromString(data.id)) {
            if (userEntities.isNotEmpty()) {
                users = SizedCollection(userEntities)
            } else {
                users = SizedCollection(emptyList())
            }

            name = data.name
            code = data.code
            address = data.address
            phone = data.phone
            email = data.email
            country = data.country
            city = data.city
            status = data.status
            metadata = data.metadata
            createdAt = data.createdAt.toJavaInstant()
            updatedAt = data.updatedAt?.toJavaInstant()
        }
    }

    override suspend fun get(id: DtId<OrganizationData>): OrganizationEntity? = dbQuery {
        OrganizationEntity.findById(id.id)
    }

    override suspend fun getByCode(code: String): OrganizationEntity? = dbQuery {
        OrganizationEntity.find { OrganizationsTable.code eq code }.limit(1).firstOrNull()
    }

    override suspend fun updateStatus(id: DtId<OrganizationData>, status: EntityStatus) {
        dbQuery {
            OrganizationsTable.update({ OrganizationsTable.id eq id.id }) {
                it[OrganizationsTable.status] = status
            }
        }
    }

    override suspend fun delete(id: DtId<OrganizationData>) {
        dbQuery {
            OrganizationsTable.deleteWhere { OrganizationsTable.id eq id.id }
        }
    }
}