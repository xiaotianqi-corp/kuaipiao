# KuaiPiao API Documentation Guide

## Overview

This guide explains the comprehensive API documentation standards for KuaiPiao. All endpoints are fully documented using OpenAPI 3.0.0 specification with automatic generation from Kotlin annotations.

## Documentation Standards

### 1. Endpoint Annotation Pattern

Every API endpoint must use the `@ApiRoute` annotation with complete documentation:

```kotlin
@Resource("/endpoint-path")
@ApiRoute(
    method = "POST",                    // HTTP method
    summary = "Clear, concise description of what the endpoint does",
    tag = "Category",                   // Logical grouping (Authentication, Company, etc.)
    requiresAuth = true/false,          // Whether authentication is required
    authType = "SessionAuth/JWTAuth",  // Type of auth (default: SessionAuth)
    requestSchema = "SchemaName",       // Request body schema reference or inline JSON schema
    responseSchema = "SchemaName",      // Response body schema reference
    exampleRequest = """{"field":"value"}""",    // Real-world example request
    exampleResponse = """{"field":"value"}"""    // Real-world example response
)
class EndpointRoute
```

### 2. Key Fields Explained

#### method
- **GET**: Retrieve data
- **POST**: Create new resource
- **PUT**: Update entire resource
- **PATCH**: Partial update
- **DELETE**: Remove resource

#### summary
- Be clear and specific
- Start with verb: "Retrieve", "Create", "Update", "Delete"
- Include object type: "Retrieve company by ID"
- Keep under 100 characters
- ❌ Bad: "Get company"
- ✅ Good: "Retrieve company by ID"

#### tag
Standard categories:
- `Authentication` - Login, register, password reset
- `Company` - Company management
- `Organization` - Organization/Department management
- `Enterprise` - Enterprise/Tenant management

#### requestSchema
Use **schema references** for common types:
```kotlin
requestSchema = "RegistrationCredentials"  // References #/components/schemas/RegistrationCredentials
```

For inline schemas:
```kotlin
requestSchema = """{
    "type":"object",
    "properties":{
        "name":{"type":"string"},
        "email":{"type":"string"}
    }
}"""
```

#### responseSchema
- Always specify for successful responses
- Use schema references when possible
- For arrays:
```kotlin
responseSchema = """{"type":"array","items":{"${'$'}ref":"#/components/schemas/CompanyResponse"}}"""
```

#### Examples
- Use realistic, concrete values
- Follow the same structure as schema
- Include relevant IDs, timestamps, enums
- ✅ Good example:
```kotlin
exampleRequest = """{"firstName":"John","lastName":"Doe","email":"john.doe@example.com","password":"SecurePass123"}"""
exampleResponse = """{"id":"usr-001","email":"john.doe@example.com","firstName":"John","lastName":"Doe","createdAt":"2024-01-15T10:30:00Z"}"""
```

### 3. Response Schema Definitions

All response objects must be defined in `GenerateOpenApiSpec.kt` under the `schemas` section:

```kotlin
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
```

### 4. Standard Response Status Codes

The system automatically includes these response codes:

| Code | Meaning | When Applied |
|------|---------|--------------|
| 200 | Success | GET, PUT, PATCH operations |
| 201 | Created | POST operations |
| 204 | No Content | DELETE operations |
| 400 | Bad Request | Invalid parameters or validation error |
| 401 | Unauthorized | Missing/invalid authentication (if `requiresAuth=true`) |
| 422 | Unprocessable Entity | Business logic validation failed |
| 500 | Server Error | Unexpected server error |

### 5. Naming Conventions

#### Endpoint Paths
- Use lowercase with hyphens: `/sign-up`, `/verify-email`
- Resource paths use singular form: `/company`, `/organization`
- Path parameters in braces: `/company/{id}`

#### Schema Names
- Use PascalCase with suffix: `CompanyResponse`, `UserCredentials`
- For request objects: `RegistrationCredentials`, `LoginCredentials`
- For response objects: `CompanyResponse`, `UserResponse`

#### Field Names
- Use camelCase: `firstName`, `taxId`, `createdAt`
- Use ISO 8601 for timestamps: `2024-01-15T10:30:00Z`

### 6. Complete Example

Here's a complete, well-documented endpoint:

```kotlin
@Resource("/create")
@ApiRoute(
    method = "POST",
    summary = "Create a new company",
    tag = "Company",
    requiresAuth = true,
    requestSchema = """{
        "type":"object",
        "properties":{
            "name":{"type":"string"},
            "taxId":{"type":"string"},
            "industry":{"type":"string"}
        }
    }""",
    responseSchema = "CompanyResponse",
    exampleRequest = """{"name":"Acme Inc","taxId":"TAX123456","industry":"Technology"}""",
    exampleResponse = """{"id":"comp-001","name":"Acme Inc","taxId":"TAX123456","industry":"Technology","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class CompanyCreateRoute
```

## Generating API Documentation

### Automatic Generation

The API documentation is automatically generated from annotations:

```bash
# In GenerateOpenApiSpec.kt main function, run:
./gradlew server:runGenerateOpenApiSpec
```

This generates:
- `src/main/resources/api.json` - OpenAPI JSON format
- `src/main/resources/api.yaml` - OpenAPI YAML format

### Viewing Documentation

1. **Scalar UI** (Interactive)
   - Available at: `http://localhost:8080/docs/scalar`
   - Browse and test endpoints
   - View schemas and examples

2. **OpenAPI Files**
   - JSON: `src/main/resources/api.json`
   - YAML: `src/main/resources/api.yaml`
   - Use with Postman, Swagger UI, or IntelliJ REST Client

## File Organization

```
server/src/main/kotlin/org/xiaotianqi/kuaipiao/
├── api/routing/v1/
│   ├── auth/AuthRoutes.kt           # Auth endpoints
│   ├── company/CompanyRoutes.kt      # Company endpoints
│   ├── organization/OrganizationRoutes.kt  # Organization endpoints
│   └── enterprise/EnterpriseRoutes.kt      # Enterprise endpoints
└── scripts/GenerateOpenApiSpec.kt   # Schema definitions
```

## Best Practices

### ✅ DO

1. **Always include examples**
   - Realistic values reflecting actual use
   - All fields present in examples
   - Proper JSON formatting

2. **Be descriptive in summaries**
   - Start with action verb
   - Include "what" and "how"
   - "Retrieve company by ID" not "Get company"

3. **Update schemas when adding fields**
   - Add new fields to schema definitions
   - Update required fields list
   - Add example responses

4. **Use schema references**
   - `"CompanyResponse"` instead of inline schema
   - Reduces duplication
   - Easier maintenance

5. **Group related endpoints with tags**
   - All company operations use `"Company"` tag
   - Improves API documentation organization

### ❌ DON'T

1. **Avoid vague summaries**
   - ❌ "Get data"
   - ✅ "Retrieve company details by ID"

2. **Don't omit examples**
   - Examples help developers understand actual payload structure
   - Essential for API adoption

3. **Don't mix unrelated endpoints with one tag**
   - One tag per logical grouping
   - Use consistent tag names across endpoints

4. **Don't use placeholder schemas**
   - Define actual schemas in GenerateOpenApiSpec
   - Include all relevant fields

## Validation Rules

Every endpoint must satisfy:

```
✓ @ApiRoute annotation present
✓ summary: Clear and descriptive (< 100 chars)
✓ tag: Appropriate category
✓ For POST/PUT/PATCH: requestSchema defined
✓ For any response: responseSchema defined
✓ For non-GET: exampleRequest provided
✓ For any response: exampleResponse provided
✓ requiresAuth: Set correctly based on endpoint
✓ All response schemas defined in GenerateOpenApiSpec
```

## Updating API Documentation

### When Adding New Endpoint

1. Create route class with `@Resource` and `@ApiRoute`
2. Add complete `@ApiRoute` annotation with all fields
3. Define request/response schemas if new
4. Add schemas to `GenerateOpenApiSpec.kt`
5. Run generation script
6. Verify in Scalar UI

### When Modifying Endpoint

1. Update `@ApiRoute` annotation
2. Update examples if behavior changed
3. Update schema if fields changed
4. Run generation script
5. Test with REST client

## Security & Authentication

### Authentication Types

```kotlin
// Session-based (cookie)
requiresAuth = true,
authType = "SessionAuth"

// JWT Bearer token
requiresAuth = true,
authType = "JWTAuth"
```

### Protected Endpoints

All administrative and user-specific endpoints require authentication:
- ✓ Creating resources
- ✓ Updating resources
- ✓ Deleting resources
- ✓ Accessing user-specific data

### Public Endpoints

These don't require authentication:
- User registration (`/sign-up`)
- User login (`/sign-in`)
- Email verification
- Password reset

## Output Files

### api.json

Complete OpenAPI specification in JSON format:
```json
{
  "openapi": "3.0.0",
  "info": {
    "title": "KuaiPiao OpenAPI",
    "version": "1.0.0",
    "description": "..."
  },
  "servers": [...],
  "paths": {...},
  "components": {...}
}
```

### api.yaml

Same specification in YAML format for readability:
```yaml
openapi: 3.0.0
info:
  title: KuaiPiao OpenAPI
  version: 1.0.0
  description: ...
servers: [...]
paths: {...}
components: {...}
```

## Troubleshooting

### Missing Examples
**Error**: Endpoint has no example in documentation

**Solution**: Add `exampleRequest` and `exampleResponse` to `@ApiRoute`

### Schema Not Found
**Error**: `$ref: "#/components/schemas/CustomSchema"` but schema undefined

**Solution**: Add schema definition to `GenerateOpenApiSpec.kt` `schemas` section

### Incorrect Response Code
**Error**: Endpoint returns 201 but documentation shows 200

**Solution**: Generator automatically sets 201 for POST, 204 for DELETE. Verify method type in `@ApiRoute`

## Additional Resources

- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Ktor Resources Documentation](https://ktor.io/docs/resources.html)
- [Scalar Documentation](https://scalar.com/)
- Project root: `/Users/zhengxi/IdeaProjects/kuaipiao`
