package org.xiaotianqi.kuaipiao.domain.password

import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.pattern
import org.xiaotianqi.kuaipiao.validation.RegexPatterns
import org.xiaotianqi.kuaipiao.validation.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest(
    val password: String,
) : Validatable<PasswordResetRequest> {
    override fun validate(): ValidationResult<PasswordResetRequest> =
        Validation {
            PasswordResetRequest::password {
                minLength(8) hint "Password min length is 8 characters"
                maxLength(100) hint "Password max length is 100 characters"
                pattern(RegexPatterns.passwordPatterns) hint "Password needs at least an uppercase character, a lowercase one and a number"
            }
        }.invoke(this)
}
