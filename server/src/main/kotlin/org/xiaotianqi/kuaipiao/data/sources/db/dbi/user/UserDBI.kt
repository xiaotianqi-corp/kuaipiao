package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI

interface UserDBI : DBI {
    suspend fun create(userData: UserCreateData)
    suspend fun get(id: DtId<UserData>): UserEntity?
    suspend fun get(email: String): UserEntity?
    suspend fun verifyEmail(id: DtId<UserData>)
    suspend fun changePassword(id: DtId<UserData>, newPasswordHashed: String)
    suspend fun resetPassword(id: DtId<UserData>, newPasswordHashed: String, verifyEmail: Boolean)
    suspend fun delete(id: DtId<UserData>)
}