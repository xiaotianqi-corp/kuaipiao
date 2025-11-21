package org.xiaotianqi.kuaipiao.data.daos.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.UserRoleDBI
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class UserRoleDao(
    private val dbi: UserRoleDBI
) {
    suspend fun assignRoleToUser(userId: DtId<UserData>, roleId: DtId<RoleData>) =
        dbi.assignRoleToUser(userId, roleId)

    suspend fun getRolesByUser(userId: DtId<UserData>): List<DtId<RoleData>> =
        dbi.getRolesByUser(userId)
}
