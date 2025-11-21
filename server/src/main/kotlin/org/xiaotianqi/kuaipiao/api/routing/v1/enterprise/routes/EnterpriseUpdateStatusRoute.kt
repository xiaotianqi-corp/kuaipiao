package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.patch
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseUpdateStatusRoute
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseUpdateStatusRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    patch<EnterpriseUpdateStatusRoute> {
        val id = call.parameters["id"]
            ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val payload = call.receive<Map<String, String>>()
        val status = payload["status"]?.let { EntityStatus.valueOf(it) }
            ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid status value")

        enterpriseDao.updateStatus(DtId(UUID.fromString(id)), status)
        call.respond(HttpStatusCode.OK, "Status updated successfully")
    }
}
