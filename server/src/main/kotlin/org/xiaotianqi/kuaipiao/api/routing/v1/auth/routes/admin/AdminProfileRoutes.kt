package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.resources.get
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminProfileRoute
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminProfileRoutes() {
    val userDao by inject<UserDao>()

    authenticate("admin-realm") {
        get<AdminProfileRoute> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString()
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            val user = userDao.get(userId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.respond(HttpStatusCode.OK, UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                enterpriseId = user.enterpriseId,
                organizationIds = user.organizationIds,
                roleIds = user.roleIds,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            ))
        }
    }
}