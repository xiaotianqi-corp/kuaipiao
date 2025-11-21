package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.AdminRemoveUserRoute
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.adminRemoveUserRoutes() {
    val userDao by inject<UserDao>()

    authenticate("admin-realm") {
        delete<AdminRemoveUserRoute> {
            val principal = call.principal<JWTPrincipal>()
            val adminId = principal?.payload?.getClaim("userId")?.asString()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            val targetUserId = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user ID")

            if (adminId == targetUserId) {
                return@delete call.respond(HttpStatusCode.Forbidden, "Cannot delete your own account")
            }

            val user = userDao.get(targetUserId)
                ?: return@delete call.respond(HttpStatusCode.NotFound, "User not found")

            userDao.delete(DtId(UUID.fromString(targetUserId)))

            call.respond(HttpStatusCode.NoContent)
        }
    }
}