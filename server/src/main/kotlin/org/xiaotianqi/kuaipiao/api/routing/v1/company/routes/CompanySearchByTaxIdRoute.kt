package org.xiaotianqi.kuaipiao.api.routing.v1.company.routes

import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanySearchByTaxIdRoute
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.companySearchByTaxIdRoute() {
    val companyDao by inject<CompanyDao>()

    get<CompanySearchByTaxIdRoute> { route ->
        val company = companyDao.getByTaxId(route.taxId)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Company not found for taxId: ${route.taxId}")
        call.respond(HttpStatusCode.OK, company)
    }
}