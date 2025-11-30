# KuaiPiao API - Quick Reference

## Endpoint Overview

### Base URL
- **Development**: `http://localhost:8080/api/v1`
- **Production**: `https://api.kuaipiao.com/api/v1`

### Authentication Endpoints (No Auth Required)
```
POST   /oauth/sign-up                    Register new user
POST   /oauth/sign-in                    Login user
GET    /oauth/logout                     Logout user
GET    /oauth/verify-email               Verify email
POST   /oauth/is-email-verified          Check verification
GET    /oauth/password-forgotten         Request password reset
POST   /oauth/reset-password             Reset password
POST   /oauth/verification-notification  Resend verification
```

### Company Endpoints (Auth Required)
```
POST   /company/create                   Create company
GET    /company/find/id/{id}             Get by ID
GET    /company/find/tax-id/{taxId}      Get by tax ID
GET    /company/find/industry/{id}       Get by industry
PUT    /company/update/{id}/industry     Update industry
DELETE /company/delete/{id}              Delete company
```

### Organization Endpoints (Auth Required)
```
POST   /org/create                       Create organization
GET    /org/find/id/{id}                 Get by ID
GET    /org/find/code/{code}             Get by code
PUT    /org/update/{id}/status           Update status
DELETE /org/delete/{id}                  Delete organization
```

### Enterprise Endpoints (Auth Required)
```
POST   /enterprise/create                Create enterprise
GET    /enterprise/find/{id}             Get by ID
GET    /enterprise/find/subdomain/{sub}  Get by subdomain
PUT    /enterprise/update/{id}/status    Update status
PUT    /enterprise/update/{id}/plan      Update plan
DELETE /enterprise/delete/{id}           Delete enterprise
```

## Common Request/Response Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/oauth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/oauth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

### Create Company
```bash
curl -X POST http://localhost:8080/api/v1/company/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Acme Inc",
    "taxId": "TAX123456",
    "industry": "Technology"
  }'
```

### Get Company
```bash
curl -X GET http://localhost:8080/api/v1/company/find/id/comp-001 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Response Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success (GET, PUT, PATCH) |
| 201 | Created (POST) |
| 204 | No Content (DELETE) |
| 400 | Bad Request |
| 401 | Unauthorized (missing/invalid auth) |
| 422 | Validation Error |
| 500 | Server Error |

## Common Response Format

### Success Response
```json
{
  "id": "comp-001",
  "name": "Acme Inc",
  "taxId": "TAX123456",
  "industry": "Technology",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "errors": [
    {
      "message": "Validation failed",
      "long_message": "The provided data failed validation checks",
      "code": "validation_error",
      "meta": {}
    }
  ],
  "meta": {},
  "clerk_trace_id": "trace_12345"
}
```

## Object Models

### User
```json
{
  "id": "usr-001",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "enterpriseId": "ent-001",
  "organizationIds": ["org-001"],
  "roleIds": ["role-admin"],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Company
```json
{
  "id": "comp-001",
  "name": "Acme Inc",
  "taxId": "TAX123456",
  "industry": "Technology",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Organization
```json
{
  "id": "org-001",
  "name": "Engineering Dept",
  "code": "ENG-001",
  "status": "active",
  "enterpriseId": "ent-001",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Enterprise
```json
{
  "id": "ent-001",
  "name": "Tech Corp",
  "subdomain": "techcorp",
  "status": "active",
  "plan": "professional",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

## Enums & Constants

### Status Values
- `active` - Active status
- `inactive` - Inactive status
- `archived` - Archived status
- `suspended` - Suspended (Enterprise only)

### Organization Status
- `active`
- `inactive`
- `archived`

### Enterprise Status
- `active`
- `inactive`
- `suspended`
- `archived`

### Plan Values
- `starter` - Starter plan
- `professional` - Professional plan
- `enterprise` - Enterprise plan

## Common Fields

### Timestamps
- Format: ISO 8601
- Examples: `2024-01-15T10:30:00Z`
- Fields: `createdAt`, `updatedAt`

### IDs
- Format: String with prefix
- Company: `comp-001`
- Organization: `org-001`
- Enterprise: `ent-001`
- User: `usr-001`

## Documentation Tools

### Interactive Documentation
- **Scalar UI**: `http://localhost:8080/docs/scalar`
- Test endpoints directly
- View examples and schemas

### OpenAPI Files
- **JSON**: `/server/src/main/resources/api.json`
- **YAML**: `/server/src/main/resources/api.yaml`
- Import to Postman, Insomnia, or VSCode

### Code
- **Source**: `/server/src/main/kotlin/org/xiaotianqi/kuaipiao/`
- Look for `@ApiRoute` annotations
- Contains complete documentation in code

## Development Workflow

### 1. View API Docs
```bash
# Start server
./gradlew :server:run

# Open browser
# http://localhost:8080/docs/scalar
```

### 2. Test Endpoint
- Click endpoint in Scalar UI
- Fill in parameters
- Click "Send"
- View response

### 3. Use Postman
```bash
# Import api.json into Postman
# All endpoints with examples ready
# Supports authentication headers
```

### 4. Use cURL
```bash
curl -X METHOD http://localhost:8080/api/v1/endpoint \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"field":"value"}'
```

## Error Handling

### Standard Error Response
```json
{
  "errors": [
    {
      "message": "Short error message",
      "long_message": "Detailed explanation of what went wrong",
      "code": "error_code",
      "meta": {
        "field": "fieldName"
      }
    }
  ],
  "meta": {},
  "clerk_trace_id": "unique_trace_id"
}
```

### Common Error Codes
- `validation_error` - Request validation failed
- `authentication_required` - Missing authentication
- `invalid_credentials` - Wrong password/email
- `resource_not_found` - ID doesn't exist
- `unauthorized` - Insufficient permissions
- `conflict` - Resource already exists
- `validation_failed` - Business logic validation

## Tips & Tricks

### 1. Get Authorization Token
```bash
# After login
POST /oauth/sign-in
# Response includes token in Set-Cookie (session) or in body (JWT)
```

### 2. Use Token in Requests
```bash
# Session cookie (automatic in browser)
# Or bearer token in header
Authorization: Bearer <token>
```

### 3. Check Email Verification Status
```bash
POST /oauth/is-email-verified
# Returns whether current user's email is verified
```

### 4. Resend Verification
```bash
POST /oauth/verification-notification
# Sends new verification email to user
```

### 5. Request Password Reset
```bash
GET /oauth/password-forgotten?email=user@example.com
# Sends reset email
```

### 6. Reset Password with Token
```bash
POST /oauth/reset-password?token=RESET_TOKEN
-d '{"password": "NewPassword123"}'
```

## Rate Limits

Currently no rate limiting implemented. Check production environment settings before deployment.

## Support

For issues or questions:
- Review: `/API_DOCUMENTATION_GUIDE.md`
- Check: `/API_DOCUMENTATION_CHECKLIST.md`
- Look at: Existing endpoint implementations
- Contact: support@kuaipiao.com

## Version Info

- **API Version**: 1.0.0
- **OpenAPI Version**: 3.0.0
- **Last Updated**: 2024-01-25
- **Status**: Production Ready ✅

---

**Bookmark this page for quick reference while developing!**
