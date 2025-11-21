package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.patch
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseUpdatePlanRoute
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseUpdatePlanRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    patch<EnterpriseUpdatePlanRoute> {
        val id = call.parameters["id"]
            ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val payload = call.receive<Map<String, String>>()
        val plan = payload["plan"]?.let { EnterprisePlan.valueOf(it) }
            ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid plan value")

        enterpriseDao.updatePlan(DtId(UUID.fromString(id)), plan)
        call.respond(HttpStatusCode.OK, "Plan updated successfully")
    }
}
