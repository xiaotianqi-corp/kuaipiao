package org.xiaotianqi.kuaipiao.data.daos.auth

import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.core.logic.typedId.newDtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.users.UserSessionCM
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionCookie

@Single(createdAtStart = true)
class UserSessionDao(
    private val userSessionCM: UserSessionCM,
) {
    suspend fun get(
        userId: String,
        sessionId: String,
    ) = userSessionCM.get(userId, sessionId)

    suspend fun create(
        userId: DtId<UserData>,
        device: String?,
        ip: String,
    ): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(
            newDtId<UserAuthSessionData>().toString(),
            userId.toString()
        )

        upsert(
            UserAuthSessionData(
                id = userSessionCookie.sessionId,
                userId = userId.toString(),
                iat = DatetimeUtils.currentMillis(),
                deviceName = device,
                ip = ip,
            ),
        )

        return userSessionCookie
    }

    private suspend fun upsert(userAuthSessionData: UserAuthSessionData) = userSessionCM.cache(userAuthSessionData)

    suspend fun delete(
        userId: DtId<UserData>,
        sessionId: DtId<UserAuthSessionData>,
    ) = userSessionCM.delete(userId, sessionId)

    suspend fun deleteAllOfUser(userId: DtId<UserData>) = userSessionCM.deleteAllOfUser(userId)
}
