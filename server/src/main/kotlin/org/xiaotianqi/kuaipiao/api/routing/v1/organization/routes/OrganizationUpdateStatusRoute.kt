package org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.OrganizationUpdateStatusRoute
import org.xiaotianqi.kuaipiao.domain.organization.UpdateStatusRequest
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.organizationUpdateStatusRoute() {
    val organizationDao by inject<OrganizationDao>()

    put<OrganizationUpdateStatusRoute> { route ->
        val id = route.id

        val request = try {
            call.receive<UpdateStatusRequest>()
        } catch (e: Exception) {
            return@put call.respond(HttpStatusCode.BadRequest, "Invalid body")
        }

        val orgId = runCatching { UUID.fromString(id) }.getOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid UUID")

        organizationDao.updateStatus(DtId(orgId), request.status)

        call.respond(
            HttpStatusCode.OK,
            mapOf("message" to "Organization status updated successfully")
        )
    }
}
