package org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes

import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.OrganizationRemoveRoute
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.organizationRemoveRoute() {
    val organizationDao by inject<OrganizationDao>()

    delete<OrganizationRemoveRoute> { route ->
        val id = route.id
        organizationDao.delete(DtId(UUID.fromString(id)))
        call.respond(HttpStatusCode.NoContent)
    }
}