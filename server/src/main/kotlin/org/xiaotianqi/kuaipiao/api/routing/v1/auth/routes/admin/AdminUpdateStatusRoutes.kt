package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminUpdateStatusRoute
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminUpdateStatusRoutes() {
    val userDao by inject<UserDao>()

    authenticate("admin-realm") {
        put<AdminUpdateStatusRoute> {
            val targetUserId = call.parameters["id"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing user ID")

            val request = call.receive<Map<String, Boolean>>()
            val isActive = request["isActive"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing isActive field")

            userDao.updateStatus(DtId(UUID.fromString(targetUserId)), isActive)

            call.respond(HttpStatusCode.OK, mapOf(
                "message" to "User status updated",
                "userId" to targetUserId,
                "isActive" to isActive
            ))
        }
    }
}