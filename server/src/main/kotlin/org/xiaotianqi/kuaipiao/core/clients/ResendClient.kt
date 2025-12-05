package org.xiaotianqi.kuaipiao.core.clients

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.config.ResendConfig
import org.xiaotianqi.kuaipiao.di.IClosableComponent
import org.xiaotianqi.kuaipiao.domain.email.*

private val log = KotlinLogging.logger { }

@Single(createdAtStart = true)
class ResendClient : IClosableComponent {

    private val httpClient = HttpClient(Apache) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        defaultRequest {
            url("https://api.resend.com")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("Authorization", "Bearer ${ResendConfig.apiKey}")
        }
    }

    /**
     * Sends an email using Resend
     * @return Email ID if successful, null otherwise
     */
    suspend fun sendEmail(request: ResendEmailRequest): String? {
        return try {
            val response: HttpResponse = httpClient.post("/emails") {
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResendEmailResponse>()
                log.debug { "Email sent successfully: ${responseBody.id}" }
                responseBody.id
            } else {
                log.error { "Failed to send email. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error sending email: ${e.message}" }
            null
        }
    }

    /**
     * Sends batch emails (up to 100)
     */
    suspend fun sendBatchEmails(requests: List<ResendEmailRequest>): List<String>? {
        return try {
            if (requests.size > 100) {
                log.error { "Batch size exceeds 100 emails" }
                return null
            }

            val response: HttpResponse = httpClient.post("/emails/batch") {
                setBody(requests)
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResendBatchEmailResponse>()
                log.debug { "Batch emails sent successfully" }
                responseBody.data.map { it.id }
            } else {
                log.error { "Failed to send batch emails. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error sending batch emails: ${e.message}" }
            null
        }
    }

    /**
     * Retrieves a single email by ID
     */
    suspend fun getEmail(emailId: String): ResendEmailDetail? {
        return try {
            val response: HttpResponse = httpClient.get("/emails/$emailId")

            if (response.status.isSuccess()) {
                response.body<ResendEmailDetail>()
            } else {
                log.error { "Failed to retrieve email. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error retrieving email: ${e.message}" }
            null
        }
    }

    /**
     * Creates a domain
     */
    suspend fun createDomain(domainName: String, region: String = "us-east-1"): ResendDomainResponse? {
        return try {
            val response: HttpResponse = httpClient.post("/domains") {
                setBody(ResendCreateDomainRequest(name = domainName, region = region))
            }

            if (response.status.isSuccess()) {
                response.body<ResendDomainResponse>()
            } else {
                log.error { "Failed to create domain. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error creating domain: ${e.message}" }
            null
        }
    }

    /**
     * Retrieves all domains
     */
    suspend fun listDomains(): List<ResendDomainItem>? {
        return try {
            val response: HttpResponse = httpClient.get("/domains")

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResendDomainsListResponse>()
                responseBody.data
            } else {
                log.error { "Failed to retrieve domains. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error retrieving domains: ${e.message}" }
            null
        }
    }

    /**
     * Verifies a domain
     */
    suspend fun verifyDomain(domainId: String): Boolean {
        return try {
            val response: HttpResponse = httpClient.post("/domains/$domainId/verify")

            if (response.status.isSuccess()) {
                log.debug { "Domain verified: $domainId" }
                true
            } else {
                log.error { "Failed to verify domain. Status: ${response.status}" }
                false
            }
        } catch (e: Exception) {
            log.error { "Error verifying domain: ${e.message}" }
            false
        }
    }

    /**
     * Creates an API key
     */
    suspend fun createApiKey(name: String, permission: String = "full_access"): ResendApiKeyResponse? {
        return try {
            val response: HttpResponse = httpClient.post("/api-keys") {
                setBody(ResendCreateApiKeyRequest(name = name, permission = permission))
            }

            if (response.status.isSuccess()) {
                response.body<ResendApiKeyResponse>()
            } else {
                log.error { "Failed to create API key. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error creating API key: ${e.message}" }
            null
        }
    }

    /**
     * Creates a template
     */
    suspend fun createTemplate(request: ResendCreateTemplateRequest): ResendTemplateResponse? {
        return try {
            val response: HttpResponse = httpClient.post("/templates") {
                setBody(request)
            }

            if (response.status.isSuccess()) {
                response.body<ResendTemplateResponse>()
            } else {
                log.error { "Failed to create template. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error creating template: ${e.message}" }
            null
        }
    }

    /**
     * Retrieves all templates
     */
    suspend fun listTemplates(): List<ResendTemplateListItem>? {
        return try {
            val response: HttpResponse = httpClient.get("/templates")

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResendTemplatesListResponse>()
                responseBody.data
            } else {
                log.error { "Failed to retrieve templates. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error retrieving templates: ${e.message}" }
            null
        }
    }

    /**
     * Gets a single template
     */
    suspend fun getTemplate(templateId: String): ResendTemplateDetail? {
        return try {
            val response: HttpResponse = httpClient.get("/templates/$templateId")

            if (response.status.isSuccess()) {
                response.body<ResendTemplateDetail>()
            } else {
                log.error { "Failed to retrieve template. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error retrieving template: ${e.message}" }
            null
        }
    }

    /**
     * Creates an audience
     */
    suspend fun createAudience(name: String): ResendAudienceResponse? {
        return try {
            val response: HttpResponse = httpClient.post("/audiences") {
                setBody(ResendCreateAudienceRequest(name = name))
            }

            if (response.status.isSuccess()) {
                response.body<ResendAudienceResponse>()
            } else {
                log.error { "Failed to create audience. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error creating audience: ${e.message}" }
            null
        }
    }

    /**
     * Creates a contact in an audience
     */
    suspend fun createContact(audienceId: String, email: String, firstName: String? = null, lastName: String? = null): ResendContactResponse? {
        return try {
            val response: HttpResponse = httpClient.post("/audiences/$audienceId/contacts") {
                setBody(ResendCreateContactRequest(email = email, first_name = firstName, last_name = lastName))
            }

            if (response.status.isSuccess()) {
                response.body<ResendContactResponse>()
            } else {
                log.error { "Failed to create contact. Status: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error { "Error creating contact: ${e.message}" }
            null
        }
    }

    suspend fun createWebhook(
        endpoint: String,
        events: List<String>
    ): ResendWebhookResponse? {

        return try {
            val response: HttpResponse = httpClient.post("/webhooks") {
                setBody(
                    ResendCreateWebhookRequest(
                        endpoint = endpoint,
                        events = events
                    )
                )
            }

            if (response.status.isSuccess()) {
                response.body<ResendWebhookResponse>()
            } else {
                log.error { "Failed to create webhook. Status: ${response.status}" }
                null
            }

        } catch (e: Exception) {
            log.error { "Error creating webhook: ${e.message}" }
            null
        }
    }


    override suspend fun close() {
        httpClient.close()
    }
}