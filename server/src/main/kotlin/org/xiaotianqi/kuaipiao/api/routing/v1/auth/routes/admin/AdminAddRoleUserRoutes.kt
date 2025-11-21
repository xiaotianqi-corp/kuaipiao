package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminAddRoleUserRoute
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.rbac.RoleDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.UserRoleDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminAddRoleUserRoutes() {
    val userDao by inject<UserDao>()
    val roleDao by inject<RoleDao>()
    val userRoleDao by inject<UserRoleDao>()

    authenticate("admin-realm") {
        post<AdminAddRoleUserRoute> {
            val targetUserId = call.parameters["id"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user ID")

            val request = call.receive<Map<String, String>>()
            val roleId = request["roleId"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing roleId")

            val user = userDao.get(targetUserId)
                ?: return@post call.respond(HttpStatusCode.NotFound, "User not found")

            val role = roleDao.get(DtId(UUID.fromString(roleId)))
                ?: return@post call.respond(HttpStatusCode.NotFound, "Role not found")

            userRoleDao.assignRoleToUser(
                userId = DtId(UUID.fromString(targetUserId)),
                roleId = DtId(UUID.fromString(roleId))
            )

            call.respond(HttpStatusCode.OK, mapOf(
                "message" to "Role assigned successfully",
                "userId" to targetUserId,
                "roleId" to roleId
            ))
        }
    }
}