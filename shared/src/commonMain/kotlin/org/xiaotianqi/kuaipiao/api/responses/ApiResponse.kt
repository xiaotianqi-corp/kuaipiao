package org.xiaotianqi.kuaipiao.api.responses

import kotlinx.serialization.Serializable

sealed class ApiResponse<out T> {
    @Serializable
    data class Success<T>(val data: T) : ApiResponse<T>()

    @Serializable
    data class Error(
        val code: Int,
        val message: String
    ) : ApiResponse<Nothing>()

    @Serializable
    data class Loading(val progress: Float = 0f) : ApiResponse<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw ApiException(code, message)
        is Loading -> throw IllegalStateException("Response is still loading")
    }

    inline fun <R> map(transform: (T) -> R): ApiResponse<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    inline fun onSuccess(action: (T) -> Unit): ApiResponse<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (code: Int, message: String) -> Unit): ApiResponse<T> {
        if (this is Error) action(code, message)
        return this
    }

    inline fun onLoading(action: (progress: Float) -> Unit): ApiResponse<T> {
        if (this is Loading) action(progress)
        return this
    }
}

class ApiException(
    val code: Int,
    override val message: String
) : Exception("API Error $code: $message")