package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.rbac.RoleDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.UserRoleDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.*
import org.xiaotianqi.kuaipiao.domain.rbac.RoleCreateData
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminRegisterRoute

import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminRegisterRoutes() {
    val userDao by inject<UserDao>()
    val roleDao by inject<RoleDao>()
    val userRoleDao by inject<UserRoleDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val jwtService by inject<JwtService>()

    post<AdminRegisterRoute> {
        val request = call.receive<SuperAdminCreateRequest>()

        val allowedDomain = "xiaotianqi.com"
        val emailDomain = request.email.substringAfter("@").lowercase()
        val blockedDomains = listOf("gmail.com", "icloud.com", "outlook.com", "hotmail.com", "yahoo.com")

        if (blockedDomains.contains(emailDomain) || emailDomain != allowedDomain) {
            return@post call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Invalid email domain"))
        }

        val existing = userDao.getFromEmail(request.email)
        if (existing != null) {
            return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
        }

        val hashedPassword = passwordEncoder.encode(request.password)
        val userId = UUID.randomUUID().toString()

        val user = UserCreateData(
            id = userId,
            username = request.username ?: request.email.substringBefore("@"),
            firstName = request.firstName,
            lastName = request.lastName,
            enterpriseId = null,
            email = request.email,
            passwordHash = hashedPassword,
            emailVerified = true,
            createdAt = Clock.System.now(),
            organizationIds = emptyList(),
            roleIds = emptyList()
        )

        userDao.create(user)

        val superAdminRole = roleDao.getByName("SUPER_ADMIN") ?: run {
            val roleId = UUID.randomUUID().toString()
            roleDao.create(
                RoleCreateData(
                    id = roleId,
                    name = "SUPER_ADMIN",
                    description = "Global administrative role with all permissions",
                    permissionIds = emptyList()
                )
            )
        }

        userRoleDao.assignRoleToUser(
            userId = DtId(UUID.fromString(userId)),
            roleId = DtId(UUID.fromString(superAdminRole.id))
        )

        val token = jwtService.generateToken(
            userId = userId,
            roles = listOf("SUPER_ADMIN")
        )

        val sessionId = UUID.randomUUID().toString()
        call.sessions.set(
            UserSessionData(
                sessionId = sessionId,
                userId = userId,
                token = token,
                roles = listOf("SUPER_ADMIN"),
                permissions = listOf("*")
            )
        )

        call.respond(
            HttpStatusCode.Created,
            mapOf(
                "message" to "Super admin registered successfully",
                "token" to token,
                "user" to mapOf(
                    "id" to userId,
                    "email" to request.email,
                    "firstName" to request.firstName,
                    "lastName" to request.lastName,
                    "roles" to listOf("SUPER_ADMIN")
                )
            )
        )
    }
}