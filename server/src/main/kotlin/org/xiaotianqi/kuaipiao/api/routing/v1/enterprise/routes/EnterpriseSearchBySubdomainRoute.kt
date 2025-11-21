package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseSearchBySubdomainRoute
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseSearchBySubdomainRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    get<EnterpriseSearchBySubdomainRoute> {
        val subdomain = call.parameters["subdomain"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing subdomain")

        val result = enterpriseDao.getBySubdomain(subdomain)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Enterprise not found")

        call.respond(HttpStatusCode.OK, result)
    }
}
