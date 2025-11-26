package org.xiaotianqi.kuaipiao.api.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthKeyLocation
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.RequiredHandling
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.config.ApiConfig
import java.io.File

fun main() {
    embeddedServer(CIO, port = 8080, host = "localhost", module = Application::configureOpenAPI)
        .start(wait = true)
}

fun Application.configureOpenAPI() {

    install(OpenApi) {

        info {
            title = "KuaiPiao API"
            version = "1.0.0"
            description = "API para la aplicación KuaiPiao - Sistema de gestión de usuarios y autenticación"
            termsOfService = "https://kuaipiao.app/terms"
            contact {
                name = "KuaiPiao Team"
                url = "https://kuaipiao.app"
                email = "support@kuaipiao.app"
            }
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        externalDocs {
            url = "https://xiaotianqi.com/kuaipiao"
            description = "Documentación completa del proyecto"
        }

        server {
            url = "http://localhost:${ApiConfig.port}"
            description = "Servidor de desarrollo local"
        }
        server {
            url = "https://xiaotianqi.com/kuaipiao"
            description = "Servidor de producción"
        }

        security {
            defaultUnauthorizedResponse {
                description = "No autenticado - Se requiere iniciar sesión"
            }
            defaultSecuritySchemeNames("SessionAuth")

            securityScheme("SessionAuth") {
                type = AuthType.API_KEY
                scheme = AuthScheme.BASIC
                name = "user_session_id"
                location = AuthKeyLocation.COOKIE
            }
        }

        tags {
            tagGenerator = { url ->
                when {
                    url.contains("auth") -> listOf("Authentication")
                    url.contains("user") -> listOf("User")
                    else -> listOf("General")
                }
            }

            tag("Authentication") {
                description = "Endpoints para registro, login y gestión de autenticación"
            }
            tag("User") {
                description = "Endpoints para gestión de perfiles de usuario"
            }
        }

        schemas {
            generator = SchemaGenerator.kotlinx {
                nullables = RequiredHandling.NON_REQUIRED
                optionals = RequiredHandling.REQUIRED
                title = TitleType.SIMPLE
                explicitNullTypes = false
            }
        }

        pathFilter = { _, url ->
            !url.contains("internal") && !url.contains("admin")
        }

        postBuild = { _, name ->
            println("✅ OpenAPI spec '$name' generado correctamente")
        }
    }

    routing {
        get("/openapi.json") {
            // Try to load from generated spec file first, then from resources
            val specContent = try {
                val classLoader = this::class.java.classLoader
                classLoader.getResourceAsStream("api.json")?.bufferedReader()?.readText()
                    ?: File("build/resources/main/api.json").takeIf { it.exists() }?.readText()
                    ?: "{\"error\": \"OpenAPI spec not found\"}"
            } catch (e: Exception) {
                "{\"error\": \"Error loading OpenAPI spec: ${e.message}\"}"
            }
            call.respondText(
                text = specContent,
                contentType = ContentType.Application.Json
            )
        }

        route("api.json") {
            openApi()
        }

        route("api.yaml") {
            openApi()
        }

        get("/docs") {
            call.respondText(
                contentType = ContentType.Text.Html
            ) {
                """
            <!DOCTYPE html>
            <html>
            <head>
                <title>KuaiPiao API Docs</title>
                <meta charset="utf-8"/>
            </head>
            <body>
                <script 
                    id="api-reference" 
                    data-url="/openapi.json"
                ></script>
                <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
            </body>
            </html>
            """.trimIndent()
            }
        }

        route("swagger") {
            swaggerUI("/openapi.json") {
                deepLinking = true
                displayOperationId = true
                defaultModelsExpandDepth = 1
                defaultModelExpandDepth = 1
                displayRequestDuration = true
                filter = true
                tryItOutEnabled = true
                requestSnippetsEnabled = true
                persistAuthorization = true
            }
        }
    }
}

private fun RoutingCall.respond(
    message: HttpStatusCode,
    typeInfo: Map<String, String>
) {
}
