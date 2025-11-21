package org.xiaotianqi.kuaipiao.api.routing.v1.benchmark.routes

import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.OrganizationCreateRoute
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationResponse
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.organizationCreateRoute() {
    val organizationDao by inject<OrganizationDao>()

    post<OrganizationCreateRoute> {
        val data = call.receive<OrganizationCreateData>()
        val created = organizationDao.create(
            data.copy(
                id = UUID.randomUUID().toString(),
                createdAt = Clock.System.now()
            )
        )
        call.respond(HttpStatusCode.Created, OrganizationResponse(
            id = created.id,
            name = created.name,
            code = created.code,
            address = created.address,
            phone = created.phone,
            email = created.email,
            country = created.country,
            city = created.city,
            status = created.status,
            metadata = created.metadata,
            userIds = created.userIds,
            createdAt = created.createdAt,
            updatedAt = created.updatedAt
        ))
    }
}
