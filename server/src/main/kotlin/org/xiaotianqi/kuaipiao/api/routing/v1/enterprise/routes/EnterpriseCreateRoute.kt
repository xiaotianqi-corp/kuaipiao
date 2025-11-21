package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes

import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.EnterpriseCreateRoute
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseResponse
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.enterpriseCreateRoute() {
    val enterpriseDao by inject<EnterpriseDao>()

    post<EnterpriseCreateRoute> {
        val body = call.receive<EnterpriseCreateData>()
        val created = enterpriseDao.create(
            body.copy(
                id = UUID.randomUUID().toString(),
                createdAt = Clock.System.now()
            )
        )

        call.respond(
            HttpStatusCode.Created,
            EnterpriseResponse(
                id = created.id,
                subdomain = created.subdomain,
                domain = created.domain,
                status = created.status,
                plan = created.plan,
                settings = created.settings,
                metadata = created.metadata,
                createdAt = created.createdAt,
                updatedAt = created.updatedAt,
                expiresAt = created.expiresAt
            )
        )
    }
}
