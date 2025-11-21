package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.fromCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.PermissionDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionsTable
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionCreateData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData
import java.util.UUID

@Single(createdAtStart = true)
class PermissionDBIImpl : PermissionDBI {

    override suspend fun create(data: PermissionCreateData): PermissionEntity =
        dbQuery {
            PermissionEntity.new(UUID.fromString(data.id)) {
                fromCreateData(data)
            }
        }

    override suspend fun get(id: DtId<PermissionData>): PermissionEntity? =
        dbQuery {
            PermissionEntity.findById(id.id)
        }

    override suspend fun getByIds(ids: List<DtId<PermissionData>>): List<PermissionEntity> =
        dbQuery {
            if (ids.isEmpty()) return@dbQuery emptyList()

            val uuidList = ids.map { it.id }
            PermissionEntity.find { PermissionsTable.id inList uuidList }.toList()
        }

    override suspend fun delete(id: DtId<PermissionData>) {
        dbQuery {
            PermissionsTable.deleteWhere { PermissionsTable.id eq id.toEntityId(PermissionsTable) }
        }
    }
}
