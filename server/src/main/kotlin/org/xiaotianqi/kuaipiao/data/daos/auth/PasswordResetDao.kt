package org.xiaotianqi.kuaipiao.data.daos.auth

import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.PasswordResetDBI
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class PasswordResetDao(
    private val passwordResetDBI: PasswordResetDBI
) {
    suspend fun create(passwordResetData: PasswordResetData) =
        passwordResetDBI.create(passwordResetData)

    suspend fun get(token: String): PasswordResetData? =
        passwordResetDBI.get(token)

    suspend fun isUserRateLimited(id: String): Boolean {
        return passwordResetDBI.count(id) >= 7
    }
}