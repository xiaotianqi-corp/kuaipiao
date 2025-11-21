package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseSearchByIdRoute
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseSearchByIdRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    get<EnterpriseSearchByIdRoute> {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val result = enterpriseDao.get(DtId(UUID.fromString(id)))
            ?: return@get call.respond(HttpStatusCode.NotFound, "Enterprise not found")

        call.respond(HttpStatusCode.OK, result)
    }
}
