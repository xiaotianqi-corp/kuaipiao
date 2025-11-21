package org.xiaotianqi.kuaipiao.api.routing.v1.company.routes

import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanyRemoveRoute
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.companyRemoveRoute() {
    val companyDao by inject<CompanyDao>()

    delete<CompanyRemoveRoute> { route ->
        val id = runCatching { UUID.fromString(route.id) }.getOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid UUID")

        companyDao.delete(DtId<CompanyInfo>(id))
        call.respond(HttpStatusCode.NoContent)
    }
}