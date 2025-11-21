// services/auth/authService.ts

import { apiClient } from '@/lib/apiClient';
import { tokenService } from './tokenService';

interface LoginCredentials {
    email: string;
    password: string;
    remember?: boolean;
}

interface RegisterData {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    organizationName?: string;
}

interface AuthResponse {
    success: boolean;
    user: {
        id: string;
        email: string;
        name: string;
        role: string;
        enterpriseId?: string;
        organizationId?: string;
    };
    tokens: {
        accessToken: string;
        refreshToken: string;
        expiresIn: number;
    };
}

interface User {
    id: string;
    email: string;
    name: string;
    role: string;
    enterpriseId?: string;
    organizationId?: string;
}

class AuthService {
    /**
     * Inicia sesión con email y contraseña
     */
    async login(credentials: LoginCredentials): Promise<AuthResponse> {
        const response = await apiClient.post<AuthResponse>('/api/auth/sign-in', credentials);

        // Guardar tokens
        if (response.tokens) {
            tokenService.setTokens(
                response.tokens.accessToken,
                response.tokens.refreshToken,
                response.tokens.expiresIn
            );
        }

        // Guardar usuario
        if (response.user) {
            this.setUser(response.user);
        }

        return response;
    }

    /**
     * Registra un nuevo usuario
     */
    async register(data: RegisterData): Promise<AuthResponse> {
        const response = await apiClient.post<AuthResponse>('/api/auth/register', data);

        if (response.tokens) {
            tokenService.setTokens(
                response.tokens.accessToken,
                response.tokens.refreshToken,
                response.tokens.expiresIn
            );
        }

        if (response.user) {
            this.setUser(response.user);
        }

        return response;
    }

    /**
     * Cierra sesión
     */
    async logout(): Promise<void> {
        try {
            await apiClient.post('/api/auth/logout');
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            this.clearAuth();
        }
    }

    /**
     * Refresca el access token
     */
    async refreshToken(): Promise<AuthResponse> {
        const refreshToken = tokenService.getRefreshToken();

        if (!refreshToken) {
            throw new Error('No refresh token available');
        }

        const response = await apiClient.post<AuthResponse>('/api/auth/refresh', {
            refreshToken
        });

        if (response.tokens) {
            tokenService.setTokens(
                response.tokens.accessToken,
                response.tokens.refreshToken,
                response.tokens.expiresIn
            );
        }

        return response;
    }

    /**
     * Solicita restablecimiento de contraseña
     */
    async forgotPassword(email: string): Promise<{ success: boolean; message: string }> {
        return apiClient.post('/api/auth/forgot-password', { email });
    }

    /**
     * Restablece la contraseña
     */
    async resetPassword(token: string, password: string): Promise<{ success: boolean; message: string }> {
        return apiClient.post('/api/auth/reset-password', { token, password });
    }

    /**
     * Verifica el email
     */
    async verifyEmail(token: string): Promise<{ success: boolean; message: string }> {
        return apiClient.post('/api/auth/verify-email', { token });
    }

    /**
     * Reenvía el email de verificación
     */
    async resendVerification(email: string): Promise<{ success: boolean; message: string }> {
        return apiClient.post('/api/auth/resend-verification', { email });
    }

    /**
     * OAuth Login
     */
    initiateOAuthLogin(provider: 'google' | 'apple' | 'meta'): void {
        window.location.href = `/api/auth/oauth/${provider}`;
    }

    /**
     * Maneja el callback de OAuth
     */
    async handleOAuthCallback(code: string, provider: string): Promise<AuthResponse> {
        const response = await apiClient.post<AuthResponse>(`/api/auth/oauth/${provider}/callback`, {
            code
        });

        if (response.tokens) {
            tokenService.setTokens(
                response.tokens.accessToken,
                response.tokens.refreshToken,
                response.tokens.expiresIn
            );
        }

        if (response.user) {
            this.setUser(response.user);
        }

        return response;
    }

    /**
     * Obtiene el usuario actual
     */
    async getCurrentUser(): Promise<User> {
        const response = await apiClient.get<{ user: User }>('/api/auth/me');
        this.setUser(response.user);
        return response.user;
    }

    /**
     * Verifica si el usuario está autenticado
     */
    isAuthenticated(): boolean {
        return tokenService.hasValidToken();
    }

    /**
     * Obtiene el usuario del localStorage
     */
    getUser(): User | null {
        const userStr = localStorage.getItem('user');
        if (!userStr) return null;

        try {
            return JSON.parse(userStr);
        } catch {
            return null;
        }
    }

    /**
     * Guarda el usuario en localStorage
     */
    private setUser(user: User): void {
        localStorage.setItem('user', JSON.stringify(user));
    }

    /**
     * Limpia toda la autenticación
     */
    private clearAuth(): void {
        tokenService.clearTokens();
        localStorage.removeItem('user');
    }
}

export const authService = new AuthService();
export default authService;