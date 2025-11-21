package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.get
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminUserRoute
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminUserRoutes() {
    val userDao by inject<UserDao>()

    authenticate("admin-realm") {

        get<AdminUserRoute> {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50

            val users = userDao.getAll(page, limit)

            call.respond(HttpStatusCode.OK, mapOf(
                "users" to users.map { user ->
                    UserResponse(
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
                    )
                },
                "page" to page,
                "limit" to limit
            ))
        }
    }
}