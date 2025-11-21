package org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
interface UserRoleDBI : DBI {
    suspend fun assignRoleToUser(userId: DtId<UserData>, roleId: DtId<RoleData>)
    suspend fun getRolesByUser(userId: DtId<UserData>): List<DtId<RoleData>>
}
