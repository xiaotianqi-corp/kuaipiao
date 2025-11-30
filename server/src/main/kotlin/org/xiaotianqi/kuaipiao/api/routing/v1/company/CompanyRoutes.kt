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
    summary = "Create a new company",
    tag = "Company",
    requiresAuth = true,
    requestSchema = """{"type":"object","properties":{"name":{"type":"string"},"taxId":{"type":"string"},"address":{"type":"object","properties":{"street":{"type":"string"},"city":{"type":"string"},"state":{"type":"string"},"postalCode":{"type":"string"},"country":{"type":"string"}}},"contact":{"type":"object","properties":{"phone":{"type":"string"},"email":{"type":"string"},"contactPerson":{"type":"string"}}},"createdAt":{"type":"string","format":"date-time"},"updatedAt":{"type":"string","format":"date-time"}},"required":["name","taxId","address","contact","createdAt"]}""",
    responseSchema = """{"type":"object","properties":{"name":{"type":"string"},"taxId":{"type":"string"},"address":{"type":"object"},"contact":{"type":"object"},"createdAt":{"type":"string","format":"date-time"},"updatedAt":{"type":"string","format":"date-time"}}}""",
    exampleRequest = """{"name":"Acme Inc","taxId":"TAX123456","address":{"street":"123 Main St","city":"Quito","state":"Pichincha","postalCode":"170150","country":"Ecuador"},"contact":{"phone":"+593999999999","email":"contact@acme.com","contactPerson":"John Doe"},"createdAt":"2024-01-01T00:00:00Z"}""",
    exampleResponse = """{"name":"Acme Inc","taxId":"TAX123456","address":{"street":"123 Main St","city":"Quito","state":"Pichincha","postalCode":"170150","country":"Ecuador"},"contact":{"phone":"+593999999999","email":"contact@acme.com","contactPerson":"John Doe"},"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class CompanyCreateRoute

@Resource("/find/id/{id}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve company by ID",
    tag = "Company",
    requiresAuth = true,
    responseSchema = """{"type":"object","properties":{"name":{"type":"string"},"taxId":{"type":"string"},"address":{"type":"object"},"contact":{"type":"object"},"createdAt":{"type":"string","format":"date-time"},"updatedAt":{"type":"string","format":"date-time"}}}""",
    exampleResponse = """{"name":"Acme Inc","taxId":"TAX123456","address":{"street":"123 Main St","city":"Quito","state":"Pichincha","postalCode":"170150","country":"Ecuador"},"contact":{"phone":"+593999999999","email":"contact@acme.com","contactPerson":"John Doe"},"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class CompanySearchByIdRoute(val id: String)

@Resource("/find/tax-id/{taxId}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve company by tax ID",
    tag = "Company",
    requiresAuth = true,
    responseSchema = """{"type":"object","properties":{"name":{"type":"string"},"taxId":{"type":"string"},"address":{"type":"object"},"contact":{"type":"object"},"createdAt":{"type":"string","format":"date-time"},"updatedAt":{"type":"string","format":"date-time"}}}""",
    exampleResponse = """{"name":"Acme Inc","taxId":"TAX123456","address":{"street":"123 Main St","city":"Quito","state":"Pichincha","postalCode":"170150","country":"Ecuador"},"contact":{"phone":"+593999999999","email":"contact@acme.com","contactPerson":"John Doe"},"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class CompanySearchByTaxIdRoute(val taxId: String)

@Resource("/find/industry/{id}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve companies by industry",
    tag = "Company",
    requiresAuth = true,
    responseSchema = """{"type":"object","properties":{"name":{"type":"string"},"taxId":{"type":"string"},"address":{"type":"object"},"contact":{"type":"object"},"createdAt":{"type":"string","format":"date-time"},"updatedAt":{"type":"string","format":"date-time"}}}""",
    exampleResponse = """{"name":"Acme Inc","taxId":"TAX123456","address":{"street":"123 Main St","city":"Quito","state":"Pichincha","postalCode":"170150","country":"Ecuador"},"contact":{"phone":"+593999999999","email":"contact@acme.com","contactPerson":"John Doe"},"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class CompanySearchByIndustry(val id: String)

@Resource("/update/{id}/industry")
@ApiRoute(
    method = "PUT",
    summary = "Update company industry",
    tag = "Company",
    requiresAuth = true,
    requestSchema = """{"type":"object","properties":{"industry":{"type":"string"}},"required":["industry"]}""",
    responseSchema = """{"type":"object","properties":{"message":{"type":"string"}}}""",
    exampleRequest = """{"industry":"Finance"}""",
    exampleResponse = """{"message":"Company industry updated successfully"}"""
)
class CompanyUpdateIndustryRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete company by ID",
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