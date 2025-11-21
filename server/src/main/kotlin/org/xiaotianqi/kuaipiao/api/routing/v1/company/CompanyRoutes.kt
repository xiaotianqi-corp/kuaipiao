package org.xiaotianqi.kuaipiao.api.routing.v1.company

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.company.routes.*
import kotlin.time.ExperimentalTime

@Resource("/create")
class CompanyCreateRoute

@Resource("/find/id/{id}")
class CompanySearchByIdRoute(val id: String)

@Resource("/find/tax-id/{taxId}")
class CompanySearchByTaxIdRoute(val taxId: String)

@Resource("/find/industry/{id}")
class CompanySearchByIndustry(val id: String)

@Resource("/update/{id}/industry")
class CompanyUpdateIndustryRoute(val id: String)

@Resource("/delete/{id}")
data class CompanyRemoveRoute(val id: String)

@ExperimentalTime
fun Route.companyRoutesV1() {
    route("/company") {
        withPermissions("MANAGE", "CREATE", "UPDATE", "DELETE") {
            companyCreateRoute()
            companyRemoveRoute()
            companyUpdateIndustryRoute()
        }

        withPermissions("VIEW") {
            companySearchByIdRoute()
            companySearchByTaxIdRoute()
            companySearchByIndustryRoute()
        }
    }
}