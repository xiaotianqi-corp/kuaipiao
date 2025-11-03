package org.xiaotianqi.kuaipiao.data.sources.cache.cm.users

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import org.xiaotianqi.kuaipiao.domain.auth.UserData

interface UserSessionCM {
    suspend fun get(userId: String, sessionId: String): UserAuthSessionData?
    suspend fun cache(userAuthSessionData: UserAuthSessionData)
    suspend fun delete(userId: DtId<UserData>, sessionId: DtId<UserAuthSessionData>)
    suspend fun deleteAllOfUser(userId: DtId<UserData>)
}