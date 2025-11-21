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
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.config.ApiConfig

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
            url = "https://docs.xiaotianqi.com/kuaipiao"
            description = "Documentación completa del proyecto"
        }

        server {
            url = "http://localhost:${ApiConfig.port}"
            description = "Servidor de desarrollo local"
        }
        server {
            url = "https://api.xiaotianqi.com/kuaipiao"
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
        route("api.json") {
            openApi()
        }

        route("api.yaml") {
            openApi()
        }

        route("swagger") {
            swaggerUI("/api.json") {
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
