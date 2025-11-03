package org.xiaotianqi.kuaipiao.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.xiaotianqi.kuaipiao.api.responses.ApiResponse

class ApiClient(
    @PublishedApi
    internal val baseUrl: String = "http://localhost:8080"
) {
    @PublishedApi
    internal val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                isLenient = true
            })
        }
    }

    suspend inline fun <reified T> get(
        endpoint: String,
        params: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        return try {
            val response = client.get("$baseUrl$endpoint") {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> ApiResponse.Success(response.body<T>())
                else -> ApiResponse.Error(
                    code = response.status.value,
                    message = response.bodyAsText()
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                code = 0,
                message = e.message ?: "Unknown error"
            )
        }
    }

    suspend inline fun <reified T, reified R> post(
        endpoint: String,
        body: T
    ): ApiResponse<R> {
        return try {
            val response = client.post("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> ApiResponse.Success(response.body<R>())
                else -> ApiResponse.Error(
                    code = response.status.value,
                    message = response.bodyAsText()
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                code = 0,
                message = e.message ?: "Unknown error"
            )
        }
    }

    suspend inline fun <reified T, reified R> put(
        endpoint: String,
        body: T
    ): ApiResponse<R> {
        return try {

            val response = client.put("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            when (response.status) {
                HttpStatusCode.OK -> ApiResponse.Success(response.body<R>())
                else -> ApiResponse.Error(
                    code = response.status.value,
                    message = response.bodyAsText()
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                code = 0,
                message = e.message ?: "Unknown error"
            )
        }
    }

    suspend fun delete(endpoint: String): ApiResponse<Unit> {
        return try {

            val response = client.delete("$baseUrl$endpoint")

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.NoContent -> ApiResponse.Success(Unit)
                else -> ApiResponse.Error(
                    code = response.status.value,
                    message = response.bodyAsText()
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                code = 0,
                message = e.message ?: "Unknown error"
            )
        }
    }

    fun close() {
        client.close()
    }
}
