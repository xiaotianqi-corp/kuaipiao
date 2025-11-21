package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface PasswordResetDBI : DBI {
    suspend fun count(id: String): Long

    suspend fun create(passwordResetData: PasswordResetData)

    suspend fun get(token: String): PasswordResetData?

    suspend fun deleteAll(id: DtId<UserData>)

    suspend fun deleteExpired()
}
