package org.xiaotianqi.kuaipiao.api.plugins

import org.xiaotianqi.kuaipiao.domain.auth.RegistrationCredentials
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.xiaotianqi.kuaipiao.validation.Validatable
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetRequest
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Application.configureValidator() {
    install(RequestValidation) {
        validateValidatable<RegistrationCredentials>()
        validateValidatable<PasswordResetRequest>()
    }
}

inline fun <reified T : Validatable<T>> RequestValidationConfig.validateValidatable() =
    validate<T> {
        val validationResult = it.validate()
        if (validationResult is Valid) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                validationResult.errors.map { error -> "${error.dataPath}: ${error.message}" },
            )
        }
    }
