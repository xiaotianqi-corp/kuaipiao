package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.UserRoleDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.UserRolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class UserRoleDBIImpl : UserRoleDBI {
    override suspend fun assignRoleToUser(userId: DtId<UserData>, roleId: DtId<RoleData>) {
        dbQuery {
            UserRolesTable.insertIgnore {
                it[UserRolesTable.user] = EntityID(userId.id, UsersTable)
                it[UserRolesTable.role] = EntityID(roleId.id, RolesTable)
            }
        }
    }

    override suspend fun getRolesByUser(userId: DtId<UserData>): List<DtId<RoleData>> {
        return dbQuery {
            UserRolesTable
                .selectAll()
                .where { UserRolesTable.user eq EntityID(userId.id, UsersTable) }
                .map { row ->
                    DtId<RoleData>(row[UserRolesTable.role].value)
                }
        }
    }
}