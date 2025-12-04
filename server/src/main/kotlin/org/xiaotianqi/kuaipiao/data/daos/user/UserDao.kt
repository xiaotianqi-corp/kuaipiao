package org.xiaotianqi.kuaipiao.data.daos.user

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.UserDBI
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@Serializable
@ExperimentalTime
class UserDao(
    private val userDBI: UserDBI,
) {
    suspend fun create(userData: UserCreateData) {
        userDBI.create(userData)
    }

    suspend fun createAndReturnEntity(userData: UserCreateData): UserEntity {
        return userDBI.createAndReturnEntity(userData)
    }

    suspend fun get(id: String): UserData? {
        return userDBI.get(id)?.toDomain()
    }

    suspend fun getAll(page: Int = 0, limit: Int = 50): List<UserData> {
        return userDBI.getAll(page, limit).map { it.toDomain() }
    }

    suspend fun updateStatus(id: DtId<UserData>, isActive: Boolean) {
        userDBI.updateStatus(id, isActive)
    }

    suspend fun getFromEmail(email: String): UserData? {
        return userDBI.get(email)?.toDomain()
    }

    suspend fun verifyEmail(id:  DtId<UserData>) {
        userDBI.verifyEmail(id)
    }

    suspend fun resetPassword(
        id: DtId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    ) {
        userDBI.resetPassword(id, newPasswordHashed, verifyEmail)
    }

    suspend fun delete(id: DtId<UserData>) {
        userDBI.delete(id)
    }
}