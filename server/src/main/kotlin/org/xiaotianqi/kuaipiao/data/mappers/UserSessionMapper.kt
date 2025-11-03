package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionCookie

fun UserAuthSessionData.toDomain() = UserAuthSessionData(
    id = this.id.toString(),
    userId = this.userId.toString(),
    iat = this.iat,
    deviceName = this.deviceName,
    ip = this.ip
)

fun UserSessionCookie.toDomain() = UserSessionCookie(
    sessionId = this.sessionId.toString(),
    userId = this.userId.toString()
)