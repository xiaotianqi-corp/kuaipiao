package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.fromCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.RoleDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RoleEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.domain.rbac.RoleCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import java.util.UUID

@Single(createdAtStart = true)
class RoleDBIImpl : RoleDBI {

    override suspend fun create(data: RoleCreateData): RoleEntity =
        dbQuery {
            RoleEntity.new(UUID.fromString(data.id)) {
                fromCreateData(data)
            }
        }

    override suspend fun get(id: DtId<RoleData>): RoleEntity? =
        dbQuery {
            RoleEntity.findById(id.id)
        }

    override suspend fun getByName(name: String): RoleEntity? =
        dbQuery {
            RoleEntity.find { RolesTable.name eq name }
                .limit(1)
                .firstOrNull()
        }

    override suspend fun getByIds(ids: List<DtId<RoleData>>): List<RoleEntity> =
        dbQuery {
            if (ids.isEmpty()) return@dbQuery emptyList()

            val uuidList = ids.map { it.id }
            RoleEntity.find { RolesTable.id inList uuidList }.toList()
        }

    override suspend fun delete(id: DtId<RoleData>) {
        dbQuery {
            RolesTable.deleteWhere { RolesTable.id eq id.toEntityId(RolesTable) }
        }
    }
}
