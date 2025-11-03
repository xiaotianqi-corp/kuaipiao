package org.xiaotianqi.kuaipiao.api.routing.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.auth.LoginRoute
import org.xiaotianqi.kuaipiao.core.exceptions.AuthenticationException
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.mappers.toResponse
import org.xiaotianqi.kuaipiao.domain.auth.LoginCredentials
import java.util.UUID

fun Route.loginRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()

    post<LoginRoute> {
        val loginData = call.receive<LoginCredentials>()
        val user = userDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (!passwordEncoder.matches(loginData.password, user.passwordHash)) {
            throw AuthenticationException()
        }

        if (!user.emailVerified) {
            return@post call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val userSessionId = userSessionDao.create(
            userId = DtId(UUID.fromString(user.id)),
            device = call.request.userAgent(),
            ip = call.request.origin.remoteAddress
        )

        call.sessions.set(userSessionId)
        call.respond(user.toResponse())
    }
}
