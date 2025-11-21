package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseRemoveRoute
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseRemoveRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    delete<EnterpriseRemoveRoute> {
        val id = call.parameters["id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing ID")

        enterpriseDao.delete(DtId(UUID.fromString(id)))
        call.respond(HttpStatusCode.NoContent, "Enterprise deleted successfully")
    }
}
