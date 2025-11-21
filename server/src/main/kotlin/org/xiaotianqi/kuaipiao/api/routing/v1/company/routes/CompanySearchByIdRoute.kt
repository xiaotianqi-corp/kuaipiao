package org.xiaotianqi.kuaipiao.api.routing.v1.company.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanySearchByIdRoute
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.companySearchByIdRoute() {
    val companyDao by inject<CompanyDao>()

    get<CompanySearchByIdRoute> { route ->
        val company = companyDao.getById(route.id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Company not found")
        call.respond(HttpStatusCode.OK, company)
    }
}