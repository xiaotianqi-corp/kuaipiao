# Quick Start Guide - Kuaipiao WebApp

## 🚀 Get Started in 5 Minutes

### Step 1: Install Dependencies

```bash
cd webApp
npm install
```

### Step 2: Configure Environment

```bash
cp .env.example .env
```

Edit `.env` and update the API URL:
```env
VITE_API_URL=http://localhost:8080
```

### Step 3: Start Development Server

```bash
npm run dev
```

Open http://localhost:5173 in your browser.

## 📋 Checklist for KMP Backend

Your Kotlin Multiplatform backend needs to implement these endpoints:

### Authentication Endpoints
- [ ] `POST /api/auth/login` - Returns `{ token, user }`
- [ ] `POST /api/auth/register` - Returns `{ token, user }`
- [ ] `POST /api/auth/logout` - Returns `{ message }`
- [ ] `GET /api/auth/me` - Returns `{ user }` (requires auth)
- [ ] `POST /api/auth/forgot-password` - Returns `{ message }`
- [ ] `POST /api/auth/reset-password` - Returns `{ message }`
- [ ] `POST /api/auth/resend-verification` - Returns `{ message }`

### Expected Request/Response Format

#### Login Request
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Login Response (Success)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "email_verified_at": "2024-01-01T00:00:00Z",
    "created_at": "2024-01-01T00:00:00Z",
    "updated_at": "2024-01-01T00:00:00Z"
  }
}
```

#### Error Response
```json
{
  "message": "Invalid credentials",
  "errors": {
    "email": "The email field is required",
    "password": "The password must be at least 8 characters"
  }
}
```

## 🔐 CORS Configuration

Your KMP backend must allow CORS from the frontend origin:

```kotlin
install(CORS) {
    allowHost("localhost:5173")
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.ContentType)
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowMethod(HttpMethod.Patch)
    allowCredentials = true
}
```

## 🧪 Test the Authentication Flow

### 1. Test Login Page
Navigate to http://localhost:5173/login

### 2. Test Registration
Navigate to http://localhost:5173/register

### 3. Test Protected Route
Try accessing http://localhost:5173/dashboard without logging in - should redirect to login

### 4. Test Logout
After logging in, click logout button

## 🛠️ Common Issues

### Issue: "Failed to fetch" errors
**Solution:** Check that your KMP backend is running on the correct port (8080)

### Issue: CORS errors in console
**Solution:** Add CORS configuration to your KMP backend

### Issue: 401 Unauthorized after page refresh
**Solution:** Implement `/api/auth/me` endpoint to validate tokens

### Issue: Token not being sent
**Solution:** Check that `Authorization: Bearer <token>` header is being sent

## 📝 Testing with Mock Data

If your backend isn't ready, you can create a mock API server:

```bash
npm install -D json-server
```

Create `db.json`:
```json
{
  "users": [
    {
      "id": 1,
      "email": "test@example.com",
      "password": "password123",
      "name": "Test User",
      "token": "mock-jwt-token"
    }
  ]
}
```

Start mock server:
```bash
npx json-server --watch db.json --port 8080
```

## 🔄 Development Workflow

1. **Make changes** to React components
2. **Hot reload** happens automatically
3. **Test in browser** at localhost:5173
4. **Check console** for errors
5. **Commit changes** with meaningful messages

## 📚 Next Steps

1. ✅ Complete backend API endpoints
2. ✅ Test all authentication flows
3. ✅ Implement invoice management
4. ✅ Implement product catalog
5. ✅ Add unit tests
6. ✅ Configure production build
7. ✅ Deploy to production

## 🆘 Need Help?

- Check the main [README.md](./README.md)
- Review the [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md)
- Check the browser console for errors
- Verify network requests in browser DevTools

## 🎉 Success!

If you can:
- ✅ See the login page
- ✅ Submit the login form
- ✅ See network requests to /api/auth/login
- ✅ Handle errors properly

Then your frontend is correctly set up! Now focus on implementing the KMP backend.