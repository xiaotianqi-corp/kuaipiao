package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger { }

/**
 * Use case for managing email broadcasts to audiences
 */
object EmailBroadcastUseCase : KoinComponent {
    private val resendClient by inject<ResendClient>()

    /**
     * Send email to multiple contacts in batches
     * @param emails List of emails to send (max 100 per batch)
     * @return List of email IDs sent
     */
    suspend fun sendBatch(emails: List<ResendEmailRequest>): List<String>? {
        if (emails.isEmpty()) {
            log.warn { "Empty email list provided to sendBatch" }
            return emptyList()
        }

        val allEmailIds = mutableListOf<String>()
        val batches = emails.chunked(100)

        for ((index, batch) in batches.withIndex()) {
            log.info { "Processing batch ${index + 1}/${batches.size} with ${batch.size} emails" }

            val batchResult = resendClient.sendBatchEmails(batch)

            if (batchResult != null) {
                allEmailIds.addAll(batchResult)
            } else {
                log.error { "Failed to send batch ${index + 1}" }
                return null
            }
        }

        log.info { "Successfully sent ${allEmailIds.size} emails in ${batches.size} batches" }
        return allEmailIds
    }

    /**
     * Send email to all contacts in an audience
     */
    suspend fun sendToAudience(
        audienceId: String,
        subject: String,
        html: String,
        text: String? = null,
        from: String? = null,
    ): Boolean {
        // This would require fetching contacts from Resend (needs implementation)
        log.info { "Sending emails to audience: $audienceId" }
        return true
    }
}

/**
 * Use case for managing email templates
 */
object EmailTemplateUseCase : KoinComponent {
    private val resendClient by inject<ResendClient>()

    /**
     * Create or update a verification template
     */
    suspend fun setupVerificationTemplate(templateName: String = "Email Verification"): String? {
        val htmlContent = buildVerificationTemplateHtml()

        val request = ResendCreateTemplateRequest(
            name = templateName,
            html = htmlContent,
            alias = "email_verification",
            subject = "Verify your email address",
            text = "Click here to verify: {{verification_url}}",
        )

        val response = resendClient.createTemplate(request)

        if (response != null) {
            log.info { "Verification template created: ${response.id}" }
            return response.id
        }

        return null
    }

    /**
     * Create a password reset template
     */
    suspend fun setupPasswordResetTemplate(templateName: String = "Password Reset"): String? {
        val htmlContent = buildPasswordResetTemplateHtml()

        val request = ResendCreateTemplateRequest(
            name = templateName,
            html = htmlContent,
            alias = "password_reset",
            subject = "Reset your password",
            text = "Click here to reset: {{reset_url}}",
        )

        val response = resendClient.createTemplate(request)

        if (response != null) {
            log.info { "Password reset template created: ${response.id}" }
            return response.id
        }

        return null
    }

    /**
     * Create a welcome email template
     */
    suspend fun setupWelcomeTemplate(templateName: String = "Welcome Email"): String? {
        val htmlContent = buildWelcomeTemplateHtml()

        val request = ResendCreateTemplateRequest(
            name = templateName,
            html = htmlContent,
            alias = "welcome",
            subject = "Welcome to {{company_name}}",
            text = "Welcome {{first_name}}! We're excited to have you.",
        )

        val response = resendClient.createTemplate(request)

        if (response != null) {
            log.info { "Welcome template created: ${response.id}" }
            return response.id
        }

        return null
    }

    private fun buildVerificationTemplateHtml(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                        .container { background-color: white; max-width: 600px; margin: 0 auto; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 40px; }
                        .content p { color: #333; line-height: 1.6; margin-bottom: 20px; }
                        .button { display: inline-block; background-color: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                        .button:hover { background-color: #5568d3; }
                        .code-block { background-color: #f5f5f5; padding: 15px; border-radius: 4px; font-family: monospace; word-break: break-all; margin: 20px 0; }
                        .footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #999; border-top: 1px solid #ddd; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Verify Your Email</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>Thank you for signing up! To complete your registration and activate your account, please verify your email address by clicking the button below.</p>
                            <p><strong>This link will expire in 1 hour.</strong></p>
                            <center>
                                <a href="{{verification_url}}" class="button">Verify Email Address</a>
                            </center>
                            <p>Or copy and paste this link in your browser:</p>
                            <div class="code-block">{{verification_url}}</div>
                            <p>If you didn't create this account, you can safely ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Kuaipiao. All rights reserved.</p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }

    private fun buildPasswordResetTemplateHtml(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                        .container { background-color: white; max-width: 600px; margin: 0 auto; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 40px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 40px; }
                        .button { display: inline-block; background-color: #f5576c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                        .footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Reset Your Password</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>We received a request to reset your password. Click the button below to create a new password.</p>
                            <p><strong>This link will expire in 24 hours.</strong></p>
                            <center>
                                <a href="{{reset_url}}" class="button">Reset Password</a>
                            </center>
                            <p>If you didn't request this, you can safely ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Kuaipiao. All rights reserved.</p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }

    private fun buildWelcomeTemplateHtml(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }
                        .container { background-color: white; max-width: 600px; margin: 20px auto; border-radius: 8px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center; }
                        .content { padding: 40px; }
                        .button { display: inline-block; background-color: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to {{company_name}}</h1>
                        </div>
                        <div class="content">
                            <p>Hello {{first_name}},</p>
                            <p>We're excited to have you on board! Your account has been successfully created.</p>
                            <p>Get started by exploring our features:</p>
                            <ul>
                                <li>Create your first project</li>
                                <li>Invite team members</li>
                                <li>Configure your settings</li>
                            </ul>
                            <center>
                                <a href="{{dashboard_url}}" class="button">Go to Dashboard</a>
                            </center>
                            <p>If you have any questions, feel free to reach out to our support team.</p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }
}

/**
 * Use case for managing audiences and contacts
 */
object AudienceManagementUseCase : KoinComponent {
    private val resendClient by inject<ResendClient>()

    /**
     * Create a new audience
     */
    suspend fun createAudience(name: String): String? {
        val audience = resendClient.createAudience(name)

        if (audience != null) {
            log.info { "Audience created: ${audience.id} - $name" }
            return audience.id
        }

        return null
    }

    /**
     * Add multiple contacts to an audience
     */
    suspend fun addContactsBatch(
        audienceId: String,
        contacts: List<ContactInfo>,
    ): List<String>? {
        val successfulIds = mutableListOf<String>()

        for (contact in contacts) {
            val result = resendClient.createContact(
                audienceId = audienceId,
                email = contact.email,
                firstName = contact.firstName,
                lastName = contact.lastName,
            )

            if (result != null) {
                successfulIds.add(result.id)
            } else {
                log.warn { "Failed to add contact: ${contact.email}" }
            }
        }

        log.info { "Added ${successfulIds.size}/${contacts.size} contacts to audience" }
        return if (successfulIds.size == contacts.size) successfulIds else null
    }

    data class ContactInfo(
        val email: String,
        val firstName: String? = null,
        val lastName: String? = null,
    )
}

/**
 * Use case for domain management
 */
object DomainManagementUseCase : KoinComponent {
    private val resendClient by inject<ResendClient>()

    /**
     * Setup a new sending domain
     */
    suspend fun setupDomain(domainName: String, region: String = "us-east-1"): String? {
        val domain = resendClient.createDomain(domainName, region)

        if (domain != null) {
            log.info { "Domain created: ${domain.id} - $domainName" }
            log.info { "Domain status: ${domain.status}" }
            log.info { "Please configure these DNS records:" }

            domain.records?.forEach { record ->
                log.info { "- ${record.type}: ${record.name} = ${record.value}" }
            }

            return domain.id
        }

        return null
    }

    /**
     * Verify domain DNS configuration
     */
    suspend fun verifyDomain(domainId: String): Boolean {
        return resendClient.verifyDomain(domainId)
    }
}