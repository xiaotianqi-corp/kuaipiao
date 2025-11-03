package org.xiaotianqi.kuaipiao.validation

import io.konform.validation.ValidationResult

interface Validatable<T> {
    fun validate(): ValidationResult<T>
}