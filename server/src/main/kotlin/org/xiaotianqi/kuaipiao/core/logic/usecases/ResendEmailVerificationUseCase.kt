package org.xiaotianqi.kuaipiao.core.logic.usecases

import kotlinx.serialization.json.JsonPrimitive
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.data.daos.auth.EmailVerificationDao
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.email.*
import org.xiaotianqi.kuaipiao.config.ResendConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.ExperimentalTime
import java.net.URLEncoder

@ExperimentalTime
object ResendEmailVerificationUseCase : KoinComponent {
    private val emailVerificationDao by inject<EmailVerificationDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val resendClient by inject<ResendClient>()

    /**
     * Sends a verification email to the provided email using Resend
     * and returns true if the email was sent successfully, false otherwise
     *
     * @return true if the email was sent, false otherwise
     */
    suspend fun createAndSend(user: UserData): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val emailVerificationData = EmailVerificationData(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000, // 1 hour
            createdAt = DatetimeUtils.currentMillis(),
        )

        val verificationUrl = buildVerificationUrl(user.email, token)

        val sent = if (ResendConfig.emailVerificationTemplateId.isNotEmpty()) {
            sendWithTemplate(user.email, verificationUrl)
        } else {
            sendWithHtml(user.email, verificationUrl)
        }

        if (sent) {
            emailVerificationDao.create(emailVerificationData)
        }

        return sent
    }

    private suspend fun sendWithTemplate(email: String, verificationUrl: String): Boolean {
        val emailRequest = ResendEmailRequest(
            from = ResendConfig.getFromAddress(),
            to = listOf(email),
            subject = "Verify your email address",
            template = ResendEmailTemplate(
                id = ResendConfig.emailVerificationTemplateId,
                variables = mapOf(
                    "verification_url" to JsonPrimitive(verificationUrl),
                    "email" to JsonPrimitive(email),
                ),
            ),
        )

        return resendClient.sendEmail(emailRequest) != null
    }

    private suspend fun sendWithHtml(email: String, verificationUrl: String): Boolean {
        val htmlContent = buildVerificationEmailHtml(verificationUrl, email)

        val emailRequest = ResendEmailRequest(
            from = ResendConfig.getFromAddress(),
            to = listOf(email),
            subject = "Verify your email address",
            html = htmlContent,
            text = "Click here to verify your email: $verificationUrl",
        )

        return resendClient.sendEmail(emailRequest) != null
    }

    private fun buildVerificationUrl(email: String, token: String): String {
        return "${ResendConfig.emailVerificationUrl}?email=${
            URLEncoder.encode(email, "utf-8")
        }&token=${URLEncoder.encode(token, "utf-8")}"
    }

    private fun buildVerificationEmailHtml(verificationUrl: String, email: String): String {
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f5f5f5;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            background-color: #ffffff;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 40px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .header h1 {
                            color: #333;
                            margin: 0;
                        }
                        .content {
                            color: #666;
                            line-height: 1.6;
                            margin-bottom: 30px;
                        }
                        .button {
                            display: inline-block;
                            background-color: #007bff;
                            color: white;
                            padding: 12px 30px;
                            text-decoration: none;
                            border-radius: 4px;
                            margin: 20px 0;
                        }
                        .button:hover {
                            background-color: #0056b3;
                        }
                        .footer {
                            text-align: center;
                            color: #999;
                            font-size: 12px;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #eee;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Verify your email address</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>Thank you for signing up! To complete your registration, please verify your email address by clicking the button below.</p>
                            <p>This link will expire in 1 hour.</p>
                            <a href="$verificationUrl" class="button">Verify Email</a>
                            <p>Or copy and paste this link in your browser:</p>
                            <p><code>$verificationUrl</code></p>
                        </div>
                        <div class="footer">
                            <p>If you didn't sign up for this account, you can safely ignore this email.</p>
                            <p>&copy; 2024 Kuaipiao. All rights reserved.</p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }
}