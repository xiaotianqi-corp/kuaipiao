package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user

import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.domain.email.EmailVerificationData

interface EmailVerificationDBI : DBI {
    suspend fun count(id: String): Long

    suspend fun create(emailVerificationData: EmailVerificationData)

    suspend fun get(token: String): EmailVerificationData?

    suspend fun deleteAll(id: String)

    suspend fun deleteExpired()
}
