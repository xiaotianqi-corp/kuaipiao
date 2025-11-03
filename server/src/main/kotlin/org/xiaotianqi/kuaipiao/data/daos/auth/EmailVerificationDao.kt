package org.xiaotianqi.kuaipiao.data.daos.auth

import org.xiaotianqi.kuaipiao.domain.email.EmailVerificationData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.EmailVerificationDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class EmailVerificationDao(
    private val emailVerificationDBI: EmailVerificationDBI
) {
    suspend fun create(emailVerificationData: EmailVerificationData) =
        emailVerificationDBI.create(emailVerificationData)

    suspend fun get(token: String): EmailVerificationData? {
        return emailVerificationDBI.get(token)
    }

    suspend fun deleteAllOfUser(id: String) =
        emailVerificationDBI.deleteAll(id)

    suspend fun isUserRateLimited(id: String): Boolean {
        return emailVerificationDBI.count(id) >= 5
    }
}