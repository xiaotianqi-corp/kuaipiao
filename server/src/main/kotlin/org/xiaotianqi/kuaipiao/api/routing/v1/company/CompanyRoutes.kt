package org.xiaotianqi.kuaipiao.api.routing.v1.company

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.company.routes.*
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/create")
@ApiRoute(
    method = "POST",
    summary = "Create company",
    tag = "Company",
    requiresAuth = true
)
class CompanyCreateRoute

@Resource("/find/id/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get company by ID",
    tag = "Company",
    requiresAuth = true
)
class CompanySearchByIdRoute(val id: String)

@Resource("/find/tax-id/{taxId}")
@ApiRoute(
    method = "GET",
    summary = "Get company by tax ID",
    tag = "Company",
    requiresAuth = true
)
class CompanySearchByTaxIdRoute(val taxId: String)

@Resource("/find/industry/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get company by Industry",
    tag = "Company",
    requiresAuth = true
)
class CompanySearchByIndustry(val id: String)

@Resource("/update/{id}/industry")
@ApiRoute(
    method = "PUT",
    summary = "Update company by ID",
    tag = "Company",
    requiresAuth = true
)
class CompanyUpdateIndustryRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete company",
    tag = "Company",
    requiresAuth = true
)
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