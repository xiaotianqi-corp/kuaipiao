package org.xiaotianqi.kuaipiao.api.routing.v1.benchmark.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.OrganizationSearchByCodeRoute
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.organizationSearchByCodeRoute() {
    val organizationDao by inject<OrganizationDao>()

    get<OrganizationSearchByCodeRoute> { route ->
        val org = organizationDao.getByCode(route.code)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Organization not found for code: ${route.code}")
        call.respond(HttpStatusCode.OK, org)
    }
}
