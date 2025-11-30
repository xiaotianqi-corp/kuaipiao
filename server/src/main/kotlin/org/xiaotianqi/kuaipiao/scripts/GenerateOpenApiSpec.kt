package org.xiaotianqi.kuaipiao.scripts

import io.ktor.resources.*
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlin.reflect.full.findAnnotation
import java.io.File

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiRoute(
    val method: String = "GET",
    val summary: String = "",
    val tag: String = "General",
    val requiresAuth: Boolean = false,
    val authType: String = "SessionAuth",
    val requestSchema: String = "",
    val responseSchema: String = "",
    val exampleRequest: String = "",
    val exampleResponse: String = ""
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiResponse(
    val statusCode: Int = 200,
    val description: String = "",
    val schema: String = ""
)

fun main() {
    generateOpenApiFromClassPath()
}

fun generateOpenApiFromClassPath() {
    println("📄 Scanning classpath for @Resource classes...")

    val basePath = "/api/v1"
    val pathsMap = mutableMapOf<String, MutableMap<String, JsonElement>>()

    val resourcePackages = listOf(
        "org.xiaotianqi.kuaipiao.api.routing.v1.auth",
        "org.xiaotianqi.kuaipiao.api.routing.v1.company",
        "org.xiaotianqi.kuaipiao.api.routing.v1.organization",
        "org.xiaotianqi.kuaipiao.api.routing.v1.enterprise"
    )

    // Mapa de prefijos por clase
    val classPrefixes = mutableMapOf<String, String>()

    // Primero, extraer todos los prefijos de los archivos fuente
    resourcePackages.forEach { packageName ->
        val routeFiles = findSourceRouteFiles(packageName)
        routeFiles.forEach { file ->
            val prefixes = extractAllRoutePrefixes(file)
            prefixes.forEach { (className, prefix) ->
                classPrefixes[className] = prefix
                println("  🔍 Encontrado prefijo para $className: $prefix")
            }
        }
    }

    resourcePackages.forEach { packageName ->
        findClassesInPackage(packageName).forEach { clazz ->
            try {
                val kClass = clazz.kotlin
                val resourceAnnotation = kClass.findAnnotation<Resource>()
                val apiRoute = kClass.findAnnotation<ApiRoute>()

                if (resourceAnnotation != null && apiRoute != null) {
                    // Obtener el prefijo de la clase
                    val prefix = classPrefixes[clazz.simpleName] ?: ""
                    val fullPath = basePath + prefix + resourceAnnotation.path
                    val method = apiRoute.method.lowercase()
                    val summary = apiRoute.summary
                    val tag = apiRoute.tag
                    val requiresAuth = apiRoute.requiresAuth
                    val authType = apiRoute.authType
                    val requestSchema = apiRoute.requestSchema
                    val responseSchema = apiRoute.responseSchema
                    val exampleRequest = apiRoute.exampleRequest
                    val exampleResponse = apiRoute.exampleResponse

                    println("  📍 $method $fullPath - $summary")

                    val endpoint = buildJsonObject {
                        put("summary", summary)
                        put("operationId", "${method}${clazz.simpleName}".lowercase())
                        putJsonArray("tags") { add(tag) }

                        putJsonArray("x-code-samples") {
                            addJsonObject {
                                put("lang", "curl")
                                put("source", buildCurlExample(fullPath, method, exampleRequest, requiresAuth))
                            }
                        }

                        if (requestSchema.isNotEmpty() && method in listOf("post", "put", "patch")) {
                            putJsonObject("requestBody") {
                                put("required", true)
                                putJsonObject("content") {
                                    putJsonObject("application/json") {
                                        if (requestSchema.startsWith("{")) {
                                            put("schema", parseToJsonElement(requestSchema))
                                        } else {
                                            putJsonObject("schema") {
                                                put("\$ref", "#/components/schemas/$requestSchema")
                                            }
                                        }
                                        if (exampleRequest.isNotEmpty()) {
                                            put("example", parseToJsonElement(exampleRequest))
                                        }
                                    }
                                }
                            }
                        }

                        if (requiresAuth) {
                            putJsonArray("security") {
                                addJsonObject { put(authType, JsonArray(emptyList())) }
                            }
                        }

                        putJsonObject("responses") {
                            if (responseSchema.isNotEmpty()) {
                                val statusCode = when (method) {
                                    "post" -> "201"
                                    "delete" -> "204"
                                    else -> "200"
                                }
                                putJsonObject(statusCode) {
                                    put("description", "Success")
                                    putJsonObject("content") {
                                        putJsonObject("application/json") {
                                            if (responseSchema.startsWith("{")) {
                                                put("schema", parseToJsonElement(responseSchema))
                                            } else {
                                                putJsonObject("schema") {
                                                    put("\$ref", "#/components/schemas/$responseSchema")
                                                }
                                            }
                                            if (exampleResponse.isNotEmpty()) {
                                                put("example", parseToJsonElement(exampleResponse))
                                            }
                                        }
                                    }
                                }
                            } else {
                                val statusCode = when (method) {
                                    "post" -> "201"
                                    "delete" -> "204"
                                    else -> "200"
                                }
                                putJsonObject(statusCode) {
                                    put("description", "Successful operation")
                                }
                            }

                            putJsonObject("400") {
                                put("description", "Bad request")
                                putJsonObject("content") {
                                    putJsonObject("application/json") {
                                        putJsonObject("schema") {
                                            put("\$ref", "#/components/schemas/ErrorResponse")
                                        }
                                        putJsonObject("example") {
                                            putJsonArray("errors") {
                                                addJsonObject {
                                                    put("message", "Invalid request parameters")
                                                    put("long_message", "The request contains invalid or missing parameters")
                                                    put("code", "validation_error")
                                                    put("meta", JsonObject(emptyMap()))
                                                }
                                            }
                                            put("meta", JsonObject(emptyMap()))
                                            put("clerk_trace_id", "trace_12345")
                                        }
                                    }
                                }
                            }

                            if (requiresAuth) {
                                putJsonObject("401") {
                                    put("description", "Unauthorized")
                                    putJsonObject("content") {
                                        putJsonObject("application/json") {
                                            putJsonObject("schema") {
                                                put("\$ref", "#/components/schemas/ErrorResponse")
                                            }
                                            putJsonObject("example") {
                                                putJsonArray("errors") {
                                                    addJsonObject {
                                                        put("message", "Authentication required")
                                                        put("long_message", "Valid authentication credentials are required")
                                                        put("code", "authentication_required")
                                                        put("meta", JsonObject(emptyMap()))
                                                    }
                                                }
                                                put("meta", JsonObject(emptyMap()))
                                                put("clerk_trace_id", "trace_12345")
                                            }
                                        }
                                    }
                                }
                            }

                            putJsonObject("422") {
                                put("description", "Unprocessable entity")
                                putJsonObject("content") {
                                    putJsonObject("application/json") {
                                        putJsonObject("schema") {
                                            put("\$ref", "#/components/schemas/ErrorResponse")
                                        }
                                        putJsonObject("example") {
                                            putJsonArray("errors") {
                                                addJsonObject {
                                                    put("message", "Validation failed")
                                                    put("long_message", "The provided data failed validation checks")
                                                    put("code", "validation_failed")
                                                    put("meta", JsonObject(emptyMap()))
                                                }
                                            }
                                            put("meta", JsonObject(emptyMap()))
                                            put("clerk_trace_id", "trace_12345")
                                        }
                                    }
                                }
                            }

                            putJsonObject("500") {
                                put("description", "Internal server error")
                                putJsonObject("content") {
                                    putJsonObject("application/json") {
                                        putJsonObject("schema") {
                                            put("\$ref", "#/components/schemas/ErrorResponse")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    pathsMap.getOrPut(fullPath) { mutableMapOf() }[method] = endpoint
                }
            } catch (e: Exception) {
                println("  ⚠️  ${clazz.name}: ${e.message}")
            }
        }
    }

    val paths = buildJsonObject {
        pathsMap.forEach { (path, methods) ->
            putJsonObject(path) {
                methods.forEach { (method, endpoint) ->
                    put(method, endpoint)
                }
            }
        }
    }

    val spec = buildJsonObject {
        put("openapi", "3.0.0")

        putJsonObject("info") {
            put("title", "KuaiPiao OpenAPI")
            put("version", "1.0.0")
            put("description", "Comprehensive API documentation for KuaiPiao - Invoice Processing & Management Platform")
            putJsonObject("contact") {
                put("name", "KuaiPiao Support")
                put("url", "https://kuaipiao.com")
                put("email", "support@kuaipiao.com")
            }
            putJsonObject("license") {
                put("name", "Apache 2.0")
                put("url", "https://www.apache.org/licenses/LICENSE-2.0.html")
            }
        }

        putJsonArray("servers") {
            addJsonObject { put("url", "http://localhost:8080"); put("description", "Development") }
            addJsonObject { put("url", "https://api.kuaipiao.com"); put("description", "Production") }
        }

        put("paths", paths)

        putJsonObject("components") {
            putJsonObject("securitySchemes") {
                putJsonObject("SessionAuth") {
                    put("type", "apiKey")
                    put("name", "user_session_id")
                    put("in", "cookie")
                    put("description", "Session cookie")
                }
                putJsonObject("JWTAuth") {
                    put("type", "http")
                    put("scheme", "bearer")
                    put("bearerFormat", "JWT")
                    put("description", "JWT token")
                }
            }
            putJsonObject("schemas") {
                putJsonObject("ErrorResponse") {
                    put("type", "object")
                    put("additionalProperties", false)
                    putJsonObject("properties") {
                        putJsonObject("errors") {
                            put("type", "array")
                            putJsonObject("items") {
                                put("\$ref", "#/components/schemas/Error")
                            }
                        }
                        putJsonObject("meta") {
                            put("type", "object")
                        }
                        putJsonObject("clerk_trace_id") {
                            put("type", "string")
                        }
                    }
                    putJsonArray("required") { add("errors"); add("meta") }
                }
                putJsonObject("Error") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("message") {
                            put("type", "string")
                        }
                        putJsonObject("long_message") {
                            put("type", "string")
                        }
                        putJsonObject("code") {
                            put("type", "string")
                        }
                        putJsonObject("meta") {
                            put("type", "object")
                        }
                    }
                    putJsonArray("required") { add("message"); add("code") }
                }

                putJsonObject("UserResponse") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("id") { put("type", "string") }
                        putJsonObject("username") { put("type", "string") }
                        putJsonObject("email") { put("type", "string") }
                        putJsonObject("firstName") { put("type", "string") }
                        putJsonObject("lastName") { put("type", "string") }
                        putJsonObject("enterpriseId") { put("type", "string") }
                        putJsonObject("organizationIds") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                        }
                        putJsonObject("roleIds") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                        }
                        putJsonObject("createdAt") { put("type", "string"); put("format", "date-time") }
                        putJsonObject("updatedAt") { put("type", "string"); put("format", "date-time") }
                    }
                    putJsonArray("required") { add("id"); add("email"); add("firstName"); add("lastName") }
                }

                putJsonObject("LoginCredentials") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("email") { put("type", "string") }
                        putJsonObject("password") { put("type", "string") }
                    }
                    putJsonArray("required") { add("email"); add("password") }
                }

                putJsonObject("RegistrationCredentials") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("firstName") { put("type", "string") }
                        putJsonObject("lastName") { put("type", "string") }
                        putJsonObject("email") { put("type", "string") }
                        putJsonObject("password") { put("type", "string") }
                        putJsonObject("enterpriseId") { put("type", "string") }
                    }
                    putJsonArray("required") { add("firstName"); add("lastName"); add("email"); add("password") }
                }

                putJsonObject("CompanyResponse") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("id") { put("type", "string") }
                        putJsonObject("name") { put("type", "string") }
                        putJsonObject("taxId") { put("type", "string") }
                        putJsonObject("industry") { put("type", "string") }
                        putJsonObject("createdAt") { put("type", "string"); put("format", "date-time") }
                        putJsonObject("updatedAt") { put("type", "string"); put("format", "date-time") }
                    }
                    putJsonArray("required") { add("id"); add("name"); add("taxId") }
                }

                putJsonObject("OrganizationResponse") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("id") { put("type", "string") }
                        putJsonObject("name") { put("type", "string") }
                        putJsonObject("code") { put("type", "string") }
                        putJsonObject("status") { put("type", "string") }
                        putJsonObject("enterpriseId") { put("type", "string") }
                        putJsonObject("createdAt") { put("type", "string"); put("format", "date-time") }
                        putJsonObject("updatedAt") { put("type", "string"); put("format", "date-time") }
                    }
                    putJsonArray("required") { add("id"); add("name"); add("code"); add("status") }
                }

                putJsonObject("EnterpriseResponse") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("id") { put("type", "string") }
                        putJsonObject("name") { put("type", "string") }
                        putJsonObject("subdomain") { put("type", "string") }
                        putJsonObject("status") { put("type", "string") }
                        putJsonObject("plan") { put("type", "string") }
                        putJsonObject("createdAt") { put("type", "string"); put("format", "date-time") }
                        putJsonObject("updatedAt") { put("type", "string"); put("format", "date-time") }
                    }
                    putJsonArray("required") { add("id"); add("name"); add("subdomain"); add("status") }
                }
            }
        }
    }

    val outputDir = File("src/main/resources")
    outputDir.mkdirs()

    File(outputDir, "api.json").writeText(Json { prettyPrint = true }.encodeToString(spec))
    println("\n✅ api.json: ${outputDir.absolutePath}/api.json")

    File(outputDir, "api.yaml").writeText(convertToYaml(spec))
    println("✅ api.yaml: ${outputDir.absolutePath}/api.yaml")
}

fun findSourceRouteFiles(packageName: String): List<File> {
    val files = mutableListOf<File>()
    val srcPath = "src/main/kotlin/${packageName.replace(".", "/")}"
    val srcDir = File(srcPath)

    if (srcDir.exists() && srcDir.isDirectory) {
        srcDir.walk().forEach { f ->
            if (f.isFile && f.name.endsWith("Routes.kt")) {
                files.add(f)
            }
        }
    }

    return files
}

fun extractAllRoutePrefixes(file: File): Map<String, String> {
    val prefixes = mutableMapOf<String, String>()
    val content = file.readText()

    val classRegex = """(?:class|data class)\s+(\w+)""".toRegex()
    val routeCallRegex = """route\s*\(\s*"([^"]+)"\s*\)\s*\{""".toRegex()

    val routeMatch = routeCallRegex.find(content)
    val routePrefix = routeMatch?.groupValues?.get(1) ?: ""

    val lines = content.split("\n")
    var inApiRoute = false

    for (i in lines.indices) {
        val line = lines[i]

        if (line.contains("@ApiRoute")) {
            inApiRoute = true
        }

        if (inApiRoute && (line.contains("class ") || line.contains("data class "))) {
            val classMatch = classRegex.find(line)
            if (classMatch != null) {
                val currentClass = classMatch.groupValues[1]
                if (routePrefix.isNotEmpty()) {
                    prefixes[currentClass] = routePrefix
                }
                inApiRoute = false
            }
        }
    }

    return prefixes
}

fun buildCurlExample(path: String, method: String, exampleRequest: String, requiresAuth: Boolean): String {
    return buildString {
        append("curl https://api.kuaipiao.com$path")
        append(" \\\n  --request ${method.uppercase()}")
        append(" \\\n  --header 'Content-Type: application/json'")

        if (requiresAuth) {
            append(" \\\n  --header 'Authorization: Bearer YOUR_JWT_TOKEN'")
        }

        if (exampleRequest.isNotEmpty() && method in listOf("post", "put", "patch")) {
            append(" \\\n  --data '$exampleRequest'")
        }
    }
}

fun findClassesInPackage(packageName: String): List<Class<*>> {
    val classes = mutableListOf<Class<*>>()
    val classLoader = Thread.currentThread().contextClassLoader
    val path = packageName.replace(".", "/")

    try {
        val resources = classLoader.getResources(path)
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            if (resource.protocol == "file") {
                val file = File(resource.toURI())
                if (file.isDirectory) {
                    file.walk().forEach { f ->
                        if (f.isFile && f.name.endsWith(".class")) {
                            val className = packageName + "." +
                                    f.name.substring(0, f.name.length - 6).replace(File.separator, ".")
                            try {
                                classes.add(Class.forName(className))
                            } catch (e: Exception) {
                                // Skip
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        println("⚠️  Could not scan: $packageName")
    }

    return classes
}

fun convertToYaml(element: JsonElement, indent: Int = 0): String {
    val spaces = " ".repeat(indent)
    return when (element) {
        is JsonObject -> {
            element.entries.joinToString("\n") { (key, value) ->
                "$spaces$key:${formatYamlValue(value, indent + 2)}"
            }
        }
        is JsonArray -> {
            element.joinToString("\n") { item ->
                "$spaces- ${item.toString().trim('"')}"
            }
        }
        is JsonPrimitive -> element.toString().trim('"')
        else -> element.toString()
    }
}

fun formatYamlValue(value: JsonElement, indent: Int): String {
    val spaces = " ".repeat(indent)
    return when (value) {
        is JsonObject -> if (value.isEmpty()) " {}" else "\n${convertToYaml(value, indent)}"
        is JsonArray -> if (value.isEmpty()) " []" else "\n${value.joinToString("\n") { "$spaces- $it" }}"
        is JsonPrimitive -> {
            val content = value.toString()
            " " + when {
                content == "true" || content == "false" || content == "null" -> content
                content.toIntOrNull() != null -> content
                else -> content.trim('"')
            }
        }
    }
}