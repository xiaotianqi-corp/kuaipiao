package org.xiaotianqi.kuaipiao.api.routing.v1.company.routes

import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanyCreateRoute
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.companyCreateRoute() {
    val companyDao by inject<CompanyDao>()

    post<CompanyCreateRoute> {
        val data = call.receive<CompanyInfo>()
        val created = companyDao.create(data)
        call.respond(HttpStatusCode.Created, created)
    }
}