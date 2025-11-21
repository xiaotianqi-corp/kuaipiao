package org.xiaotianqi.kuaipiao.api.routing.v1.company.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanyUpdateIndustryRoute
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import java.util.*
import kotlin.time.ExperimentalTime

@Serializable
data class UpdateIndustryRequest(val industry: String)

@ExperimentalTime
fun Route.companyUpdateIndustryRoute() {
    val companyDao by inject<CompanyDao>()

    put<CompanyUpdateIndustryRoute> { route ->
        val id = runCatching { UUID.fromString(route.id) }.getOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid UUID")

        val request = try {
            call.receive<UpdateIndustryRequest>()
        } catch (e: Exception) {
            return@put call.respond(HttpStatusCode.BadRequest, "Invalid body")
        }

        companyDao.updateIndustry(DtId<CompanyInfo>(id), request.industry)

        call.respond(
            HttpStatusCode.OK,
            mapOf("message" to "Company industry updated successfully")
        )
    }
}