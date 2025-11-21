package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.v1.auth.LoginRoute
import org.xiaotianqi.kuaipiao.core.exceptions.AuthenticationException
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.RoleDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.data.mappers.toResponse
import org.xiaotianqi.kuaipiao.domain.auth.LoginCredentials
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionData
import io.ktor.http.*
import io.ktor.server.plugins.origin
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.data.daos.rbac.PermissionDao
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import java.util.UUID
import kotlin.getValue
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.loginRoute() {
    val userDao by inject<UserDao>()
    val roleDao by inject<RoleDao>()
    val permissionDao by inject<PermissionDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val jwtService by inject<JwtService>()

    /**
     * User login endpoint
     *
     * Authenticates a user with email and password credentials.
     *
     * @request [LoginCredentials] User email and password
     * @response 200 [UserResponse] Login successful, returns user data
     * @response 401 Invalid credentials
     * @response 405 Email not verified
     */

    post<LoginRoute> {
        val loginData = call.receive<LoginCredentials>()
        val user = userDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (!passwordEncoder.matches(loginData.password, user.passwordHash)) {
            throw AuthenticationException()
        }

        if (!user.emailVerified) {
            return@post call.respond(HttpStatusCode.MethodNotAllowed, "Email not verified")
        }
        val token = jwtService.generateToken(userId = user.id)

        val userSessionId = userSessionDao.create(
            userId = DtId(UUID.fromString(user.id)),
            device = call.request.userAgent(),
            ip = call.request.origin.remoteAddress,
            token = token,
        )

        val roleIds = user.roleIds.map { DtId<RoleData>(UUID.fromString(it)) }
        val roles = roleDao.getByIds(roleIds)
        val permissionIds = roles.flatMap { it.permissionIds }.distinct()
        val permissions = permissionDao.getByIds(
            permissionIds.map { DtId<PermissionData>(UUID.fromString(it)) }
        )

        val session = UserSessionData(
            sessionId = userSessionId.toString(),
            userId = user.id,
            token = token,
            roles = roles.map { it.name },
            permissions = permissions.map { it.code }
        )

        call.sessions.set(session)
        call.respond(HttpStatusCode.OK, user.toResponse())
    }
}
