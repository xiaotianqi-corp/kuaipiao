# Kuaipiao - Multi-enterprise Invoicing System

A modern multi-enterprise invoicing and product management system built with Kotlin Multiplatform (KMP) backend and React frontend.

## 🚀 Features

- **Multi-enterprise Architecture**: Isolated data per organization
- **Invoice Management**: Create, edit, and manage invoices
- **Product Catalog**: Comprehensive product management
- **Country-specific Configurations**: Support for different tax systems
- **User Authentication**: Secure JWT-based authentication
- **Role-based Access Control**: Granular permissions system
- **Responsive UI**: Modern, accessible interface built with React and Tailwind CSS

## 🏗️ Tech Stack

### Frontend
- **React 18** - UI library
- **TypeScript** - Type safety
- **React Router v6** - Client-side routing
- **Tailwind CSS** - Utility-first styling
- **Radix UI** - Accessible component primitives
- **Vite** - Build tool and dev server

### Backend (Expected)
- **Kotlin Multiplatform** - Backend logic
- **Ktor** - HTTP server framework
- **Exposed** - Database ORM
- **PostgreSQL** - Primary database
- **JWT** - Authentication tokens

## 📦 Installation

### Prerequisites
- Node.js 18+ and npm/yarn
- Git

### Frontend Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd webApp
```

2. Install dependencies:
```bash
npm install
```

3. Create environment file:
```bash
cp .env.example .env
```

4. Update `.env` with your configuration:
```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Kuaipiao
```

5. Start development server:
```bash
npm run dev
```

The app will be available at `http://localhost:5173`

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_URL` | Backend API URL | `http://localhost:8080` |
| `VITE_APP_NAME` | Application name | `Kuaipiao` |
| `VITE_APP_ENV` | Environment | `development` |
| `VITE_ENABLE_DEBUG` | Enable debug mode | `true` |

## 📁 Project Structure

```
webApp/
├── src/
│   ├── components/       # Reusable UI components
│   │   ├── common/      # Common components
│   │   ├── ui/          # Radix UI components
│   │   └── ...
│   ├── contexts/        # React contexts
│   │   └── AuthContext.tsx
│   ├── hooks/           # Custom React hooks
│   │   ├── useForm.ts
│   │   ├── useAuth.ts
│   │   └── ...
│   ├── layouts/         # Page layouts
│   │   ├── app-layout.tsx
│   │   └── auth-layout.tsx
│   ├── lib/            # Utilities and helpers
│   │   ├── apiClient.ts
│   │   └── utils.ts
│   ├── pages/          # Page components
│   │   ├── auth/
│   │   ├── settings/
│   │   └── dashboard.tsx
│   ├── styles/         # Global styles
│   │   └── globals.css
│   ├── types/          # TypeScript type definitions
│   └── router.tsx      # Route configuration
├── public/             # Static assets
├── .env.example        # Environment template
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 🔐 Authentication Flow

1. **Login**: User submits credentials → Backend validates → Returns JWT token
2. **Token Storage**: Token stored in localStorage
3. **API Requests**: Token included in Authorization header
4. **Protected Routes**: PrivateRoute wrapper checks authentication
5. **Logout**: Token removed from localStorage

### Authentication State Management

```typescript
// Using AuthContext
const { user, isAuthenticated, login, logout } = useAuth();

// Login
await login(email, password);

// Logout
await logout();

// Check authentication
if (isAuthenticated) {
  // User is logged in
}
```

## 🎨 Component Usage

### Form Handling

```typescript
import { useForm } from '@/hooks/useForm';

const form = useForm({
    initialData: { name: '', email: '' },
    endpoint: '/api/users',
    validate: (data) => {
        const errors = {};
        if (!data.name) errors.name = 'Required';
        return errors;
    },
    onSuccess: (result) => {
        console.log('Success:', result);
    }
});

<form onSubmit={form.submit}>
    <input 
        value={form.data.name}
        onChange={e => form.setField('name', e.target.value)}
    />
    {form.errors.name && <span>{form.errors.name}</span>}
    <button disabled={form.processing}>Submit</button>
</form>
```

### API Client

```typescript
import apiClient from '@/lib/apiClient';

// GET request
const users = await apiClient.get('/api/users');

// POST request
const newUser = await apiClient.post('/api/users', { name: 'John' });

// PUT request
const updated = await apiClient.put('/api/users/1', { name: 'Jane' });

// DELETE request
await apiClient.delete('/api/users/1');
```

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run E2E tests
npm run test:e2e
```

## 🏗️ Build

```bash
# Production build
npm run build

# Preview production build
npm run preview
```

## 📚 API Documentation

### Expected Backend Endpoints

#### Authentication
- `POST /api/auth/login` - Authenticate user
- `POST /api/auth/register` - Register new user
- `POST /api/auth/logout` - Logout user
- `GET /api/auth/me` - Get current user
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password
- `POST /api/auth/resend-verification` - Resend verification email

#### Users
- `GET /api/user/profile` - Get user profile
- `PATCH /api/user/profile` - Update profile
- `PUT /api/user/password` - Change password
- `DELETE /api/user/account` - Delete account

#### Products
- `GET /api/products` - List products
- `POST /api/products` - Create product
- `GET /api/products/:id` - Get product details
- `PUT /api/products/:id` - Update product
- `DELETE /api/products/:id` - Delete product

#### Invoices
- `GET /api/invoices` - List invoices
- `POST /api/invoices` - Create invoice
- `GET /api/invoices/:id` - Get invoice details
- `PUT /api/invoices/:id` - Update invoice
- `DELETE /api/invoices/:id` - Delete invoice

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Code Style

- Use TypeScript for type safety
- Follow React hooks best practices
- Use functional components
- Keep components small and focused
- Write meaningful commit messages
- Add JSDoc comments for complex functions

## 🐛 Troubleshooting

### Common Issues

**CORS Errors**
- Ensure backend allows requests from frontend origin
- Check CORS configuration in KMP backend

**401 Unauthorized**
- Verify token is being sent in Authorization header
- Check token expiration
- Ensure backend validates tokens correctly

**Routes Not Found**
- Verify React Router configuration
- Check backend route definitions
- Ensure API endpoints match frontend calls

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Radix UI for accessible components
- Tailwind CSS for styling utilities
- React Router for routing
- Vite for blazing fast builds
- Kotlin Multiplatform community

## 📞 Support

For support, email support@kuaipiao.com or open an issue in the repository.

## 🗺️ Roadmap

- [ ] Complete KMP backend integration
- [ ] Add invoice PDF generation
- [ ] Implement real-time notifications
- [ ] Add multi-language support
- [ ] Implement advanced reporting
- [ ] Mobile app (KMP shared code)
- [ ] Payment gateway integration
- [ ] Advanced analytics dashboard

## 📊 Performance

The application is optimized for performance:
- Code splitting for faster initial load
- Lazy loading of routes and components
- Optimized bundle size with tree-shaking
- Efficient re-rendering with React hooks
- API response caching

## 🔒 Security

- JWT-based authentication
- Secure HTTP-only cookies (optional)
- CSRF protection
- XSS prevention
- SQL injection protection (backend)
- Rate limiting (backend)
- Input validation and sanitization