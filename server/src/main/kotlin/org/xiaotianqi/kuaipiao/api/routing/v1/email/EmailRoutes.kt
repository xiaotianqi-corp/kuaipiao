package org.xiaotianqi.kuaipiao.api.routing.v1.email

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.email.routes.*
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/send")
@ApiRoute(
    method = "POST",
    summary = "Send an email",
    tag = "Email",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"from":{"type":"string"},"to":{"type":"array","items":{"type":"string"}},"subject":{"type":"string"},"html":{"type":"string"},"text":{"type":"string"},"cc":{"type":"array","items":{"type":"string"}},"bcc":{"type":"array","items":{"type":"string"}},"replyTo":{"type":"array","items":{"type":"string"}},"tags":{"type":"array","items":{"type":"object"}}},"required":["to","subject"]}""",
    responseSchema = """{"type":"object","properties":{"id":{"type":"string"}}}""",
    exampleRequest = """{"from":"onboarding@resend.dev","to":["delivered@resend.dev"],"subject":"Hello","html":"<p>Test email</p>","tags":[{"name":"tag1","value":"value1"}]}""",
    exampleResponse = """{"id":"009d4a47-87d8-41a7-97e5-29a9581a401c"}"""
)
class EmailSendRoute

@Resource("/batch")
@ApiRoute(
    method = "POST",
    summary = "Send batch emails (up to 100)",
    tag = "Email",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"emails":{"type":"array","items":{"type":"object","properties":{"from":{"type":"string"},"to":{"type":"array","items":{"type":"string"}},"subject":{"type":"string"},"html":{"type":"string"},"text":{"type":"string"}},"required":["to","subject"]}}},"required":["emails"]}""",
    responseSchema = """{"type":"object","properties":{"ids":{"type":"array","items":{"type":"string"}},"count":{"type":"integer"}}}""",
    exampleRequest = """{"emails":[{"from":"onboarding@resend.dev","to":["user1@example.com"],"subject":"Batch Test","html":"<p>Batch email</p>"},{"from":"onboarding@resend.dev","to":["user2@example.com"],"subject":"Batch Test 2","html":"<p>Second email</p>"}]}""",
    exampleResponse = """{"ids":["email1","email2"],"count":2}"""
)
class EmailBatchRoute

@Resource("/{emailId}")
@ApiRoute(
    method = "GET",
    summary = "Get email details by ID",
    tag = "Email",
    requiresAuth = true,
    authType = "BearerAuth",
    responseSchema = """{"type":"object","properties":{"object":{"type":"string"},"id":{"type":"string"},"to":{"type":"array","items":{"type":"string"}},"from":{"type":"string"},"subject":{"type":"string"},"html":{"type":"string"},"text":{"type":"string"},"created_at":{"type":"string"},"last_event":{"type":"string"}}}""",
    exampleResponse = """{"object":"email","id":"009d4a47-87d8-41a7-97e5-29a9581a401c","to":["delivered@resend.dev"],"from":"onboarding@resend.dev","subject":"Hello","html":"<p>Test</p>","created_at":"2025-01-01T00:00:00Z","last_event":"delivered"}"""
)
class EmailDetailByIdRoute

@Resource("/templates")
@ApiRoute(
    method = "POST",
    summary = "Create email template",
    tag = "Email Templates",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"name":{"type":"string"},"html":{"type":"string"},"alias":{"type":"string"},"from":{"type":"string"},"subject":{"type":"string"},"replyTo":{"type":"array","items":{"type":"string"}},"text":{"type":"string"}},"required":["name","html"]}""",
    responseSchema = """{"type":"object","properties":{"id":{"type":"string"},"object":{"type":"string"}}}""",
    exampleRequest = """{"name":"Verification Template","html":"<p>Verify your email: {{verification_url}}</p>","alias":"email_verification","subject":"Verify Email"}""",
    exampleResponse = """{"id":"template-123","object":"template"}"""
)
class CreateEmailtemplateRoute

@Resource("/templates")
@ApiRoute(
    method = "GET",
    summary = "List all templates",
    tag = "Email Templates",
    requiresAuth = true,
    authType = "BearerAuth",
    responseSchema = """{"type":"object","properties":{"object":{"type":"string"},"data":{"type":"array","items":{"type":"object"}},"has_more":{"type":"boolean"}}}""",
    exampleResponse = """{"object":"list","data":[{"id":"tpl-1","name":"Verification"}],"has_more":false}"""
)
class FindEmailtemplateRoute

@Resource("/templates/{templateId}")
@ApiRoute(
    method = "GET",
    summary = "Get template by ID",
    tag = "Email Templates",
    requiresAuth = true,
    authType = "BearerAuth",
    responseSchema = """{"type":"object","properties":{"object":{"type":"string"},"id":{"type":"string"},"name":{"type":"string"},"html":{"type":"string"},"status":{"type":"string"}}}""",
    exampleResponse = """{"object":"template","id":"tpl-123","name":"Verification","html":"<p>Verify</p>","status":"published"}"""
)
class FindEmailtemplateByIdRoute

@Resource("/audiences")
@ApiRoute(
    method = "POST",
    summary = "Create audience",
    tag = "Audiences",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"name":{"type":"string"}},"required":["name"]}""",
    responseSchema = """{"type":"object","properties":{"id":{"type":"string"},"object":{"type":"string"},"name":{"type":"string"}}}""",
    exampleRequest = """{"name":"Newsletter Subscribers"}""",
    exampleResponse = """{"id":"aud-123","object":"audience","name":"Newsletter Subscribers"}"""
)
class CreateEmailAudienceRoute

@Resource("/audiences/{audienceId}/contacts")
@ApiRoute(
    method = "POST",
    summary = "Add contact to audience",
    tag = "Contacts",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"email":{"type":"string"},"firstName":{"type":"string"},"lastName":{"type":"string"}},"required":["email"]}""",
    responseSchema = """{"type":"object","properties":{"object":{"type":"string"},"id":{"type":"string"}}}""",
    exampleRequest = """{"email":"user@example.com","firstName":"John","lastName":"Doe"}""",
    exampleResponse = """{"object":"contact","id":"contact-123"}"""
)
class CreateEmailContactRoute

@Resource("/domains")
@ApiRoute(
    method = "GET",
    summary = "List all domains",
    tag = "Domains",
    requiresAuth = true,
    authType = "BearerAuth",
    responseSchema = """{"type":"object","properties":{"data":{"type":"array","items":{"type":"object"}}}}""",
    exampleResponse = """{"data":[{"id":"dom-123","name":"example.com","status":"verified"}]}"""
)
class FindAllEmailDomainRoute

@Resource("/domains")
@ApiRoute(
    method = "POST",
    summary = "Create domain",
    tag = "Domains",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"name":{"type":"string"},"region":{"type":"string"}},"required":["name"]}""",
    responseSchema = """{"type":"object","properties":{"id":{"type":"string"},"name":{"type":"string"},"status":{"type":"string"},"records":{"type":"array"}}}""",
    exampleRequest = """{"name":"mail.example.com","region":"us-east-1"}""",
    exampleResponse = """{"id":"dom-123","name":"mail.example.com","status":"pending","records":[]}"""
)
class CreateEmailDomainRoute

@Resource("/domains/{domainId}/verify")
@ApiRoute(
    method = "POST",
    summary = "Verify domain",
    tag = "Domains",
    requiresAuth = true,
    authType = "BearerAuth",
    responseSchema = """{"type":"object","properties":{"verified":{"type":"boolean"}}}""",
    exampleResponse = """{"verified":true}"""
)
class VerifyEmailDomainRoute

@Resource("/webhooks")
@ApiRoute(
    method = "POST",
    summary = "Create webhook",
    tag = "Webhooks",
    requiresAuth = true,
    authType = "BearerAuth",
    requestSchema = """{"type":"object","properties":{"endpoint":{"type":"string"},"events":{"type":"array","items":{"type":"string"}}},"required":["endpoint","events"]}""",
    responseSchema = """{"type":"object","properties":{"object":{"type":"string"},"id":{"type":"string"},"signing_secret":{"type":"string"}}}""",
    exampleRequest = """{"endpoint":"https://xiaotianqi.com/webhooks/resend","events":["email.sent","email.delivered"]}""",
    exampleResponse = """{"object":"webhook","id":"wh-123","signing_secret":"whsec_..."}"""
)
class CreateEmailWebhookRoute

@ExperimentalTime
fun Route.emailRoutesV1() {
    route("/email") {
        withPermissions("MANAGE", "CREATE", "UPDATE", "DELETE", "VIEW") {
            emailSendRoute()
            emailBatchRoute()
            emailDetailByIdRoute()
            createEmailtemplateRoute()
            findEmailtemplateRoute()
            findEmailtemplateByIdRoute()
            createEmailDomainRoute()
            findAllEmailDomainRoute()
            verifyEmailDomainRoute()
            createEmailAudienceRoute()
            createEmailContactRoute()
            createEmailWebhookRoute()
        }
    }
}