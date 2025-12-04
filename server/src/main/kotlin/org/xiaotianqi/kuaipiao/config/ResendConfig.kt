package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("resend")
object ResendConfig {
    @ConfigurationProperty("api.key")
    var apiKey: String = "none"

    @ConfigurationProperty("from.email")
    var fromEmail: String = "onboarding@resend.dev"

    @ConfigurationProperty("from.name")
    var fromName: String = "Kuaipiao"

    @ConfigurationProperty("template.email.verification")
    var emailVerificationTemplateId: String = ""

    @ConfigurationProperty("template.password.reset")
    var passwordResetTemplateId: String = ""

    @ConfigurationProperty("template.password.reset.success")
    var passwordResetSuccessTemplateId: String = ""

    @ConfigurationProperty("email.verification.success.url")
    var emailVerificationSuccessUrl: String = "https://dotoo.app/email-verified"

    @ConfigurationProperty("email.verification.error.url")
    var emailVerificationErrorUrl: String = "https://dotoo.app/invalid-email-verification-code"

    @ConfigurationProperty("email.verification.url")
    var emailVerificationUrl: String = "http://localhost:8080/verify-email"

    @ConfigurationProperty("reset.password.url")
    var passwordResetUrl: String = "https://dotoo.app/reset-password"

    @ConfigurationProperty("domain.id")
    var domainId: String = ""

    @ConfigurationProperty("audience.id")
    var audienceId: String = ""

    fun getFromAddress(): String = "$fromName <$fromEmail>"
}