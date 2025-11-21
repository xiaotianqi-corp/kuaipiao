package org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.OrganizationSearchByIdRoute
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.organizationSearchByIdRoute() {
    val organizationDao by inject<OrganizationDao>()

    get<OrganizationSearchByIdRoute> { route ->
        val org = organizationDao.get(DtId(route.id))
            ?: return@get call.respond(HttpStatusCode.NotFound, "Organization not found")
        call.respond(HttpStatusCode.OK, org)
    }
}
