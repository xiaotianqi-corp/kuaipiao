package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.v1.auth.LogoutRoute
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionCookie
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.logoutRoutes() {
    val userSessionDao by inject<UserSessionDao>()

    get<LogoutRoute> {
        val session = call.sessions.get<UserSessionCookie>()!!
        val userIdDtId = DtId<UserData>(session.userId)
        val sessionIdDtId = DtId<UserAuthSessionData>(session.sessionId)

        userSessionDao.delete(userIdDtId, sessionIdDtId)

        call.sessions.clear<UserSessionCookie>()
        call.respond(HttpStatusCode.OK)
    }
}
