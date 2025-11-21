package org.xiaotianqi.kuaipiao.data.sources.cache.cm.users.impl

import org.xiaotianqi.kuaipiao.config.ApiConfig
import org.xiaotianqi.kuaipiao.core.clients.RedisClient
import org.xiaotianqi.kuaipiao.core.logic.ObjectMapper
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.users.UserSessionCM
import org.xiaotianqi.kuaipiao.data.sources.cache.core.ExpiringCM
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true, binds = [UserSessionCM::class])
@ExperimentalTime
class UserSessionCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : UserSessionCM,
    ExpiringCM(
        keyBase = "sessions",
        expirationInSeconds = (ApiConfig.sessionMaxAgeInSeconds + 10),
        redisClient,
        objectMapper,
    ) {
    private fun keyValue(
        userId: DtId<UserData>,
        sessionId: DtId<UserAuthSessionData>,
    ) = "$userId:$sessionId"

    override suspend fun get(
        userId: String,
        sessionId: String,
    ): UserAuthSessionData? =
        get(
            keyValue(
                userId = DtId(userId),
                sessionId = DtId(sessionId),
            ),
        )

    override suspend fun cache(userAuthSessionData: UserAuthSessionData) =
        cache(
            keyValue(
                userId = DtId(userAuthSessionData.userId),
                sessionId = DtId(userAuthSessionData.id)
            ),
            userAuthSessionData
        )

    override suspend fun delete(
        userId: DtId<UserData>,
        sessionId: DtId<UserAuthSessionData>
    ) = delete(keyValue(userId, sessionId))

    override suspend fun deleteAllOfUser(userId: DtId<UserData>) = delete("$userId:*")

}
