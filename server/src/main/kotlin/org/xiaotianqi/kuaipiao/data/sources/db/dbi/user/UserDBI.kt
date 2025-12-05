package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface UserDBI : DBI {
    suspend fun create(data: UserCreateData): UserEntity
    suspend fun createAndReturnEntity(data: UserCreateData): UserEntity
    suspend fun get(id: String): UserEntity?
    suspend fun getAll(page: Int = 0, limit: Int = 50): List<UserEntity>
    suspend fun updateStatus(id: DtId<UserData>, isActive: Boolean): Int
    suspend fun getByEmail(email: String): UserData?
    suspend fun verifyEmail(id: DtId<UserData>): Int
    suspend fun resetPassword(id: DtId<UserData>, newPasswordHashed: String, verifyEmail: Boolean): Int
    suspend fun delete(id: DtId<UserData>): Int
}