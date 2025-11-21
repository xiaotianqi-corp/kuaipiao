interface TokenPayload {
    userId: string;
    email: string;
    role: string;
    enterpriseId?: string;
    organizationId?: string;
    exp: number;
    iat: number;
}

class TokenService {
    private readonly ACCESS_TOKEN_KEY = 'auth_token';
    private readonly REFRESH_TOKEN_KEY = 'refresh_token';
    private readonly TOKEN_EXPIRY_KEY = 'token_expiry';

    /**
     * Guarda los tokens en localStorage
     */
    setTokens(accessToken: string, refreshToken: string, expiresIn?: number): void {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
        localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);

        if (expiresIn) {
            const expiryTime = Date.now() + expiresIn * 1000;
            localStorage.setItem(this.TOKEN_EXPIRY_KEY, expiryTime.toString());
        }
    }

    /**
     * Obtiene el access token
     */
    getAccessToken(): string | null {
        return localStorage.getItem(this.ACCESS_TOKEN_KEY);
    }

    /**
     * Obtiene el refresh token
     */
    getRefreshToken(): string | null {
        return localStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Elimina todos los tokens
     */
    clearTokens(): void {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        localStorage.removeItem(this.TOKEN_EXPIRY_KEY);
    }

    /**
     * Verifica si el token está expirado
     */
    isTokenExpired(): boolean {
        const expiryTime = localStorage.getItem(this.TOKEN_EXPIRY_KEY);
        if (!expiryTime) return true;

        return Date.now() > parseInt(expiryTime);
    }

    /**
     * Verifica si hay un token válido
     */
    hasValidToken(): boolean {
        const token = this.getAccessToken();
        return !!token && !this.isTokenExpired();
    }

    /**
     * Decodifica el JWT (sin verificar la firma)
     */
    decodeToken(token: string): TokenPayload | null {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );

            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error('Error decoding token:', error);
            return null;
        }
    }

    /**
     * Obtiene el payload del access token actual
     */
    getTokenPayload(): TokenPayload | null {
        const token = this.getAccessToken();
        if (!token) return null;

        return this.decodeToken(token);
    }

    /**
     * Obtiene el userId del token
     */
    getUserId(): string | null {
        const payload = this.getTokenPayload();
        return payload?.userId || null;
    }

    /**
     * Obtiene el rol del usuario
     */
    getUserRole(): string | null {
        const payload = this.getTokenPayload();
        return payload?.role || null;
    }

    /**
     * Obtiene el enterpriseId del usuario
     */
    getEnterpriseId(): string | null {
        const payload = this.getTokenPayload();
        return payload?.enterpriseId || null;
    }

    /**
     * Tiempo restante hasta la expiración (en segundos)
     */
    getTimeUntilExpiry(): number {
        const expiryTime = localStorage.getItem(this.TOKEN_EXPIRY_KEY);
        if (!expiryTime) return 0;

        const remaining = parseInt(expiryTime) - Date.now();
        return Math.max(0, Math.floor(remaining / 1000));
    }

    /**
     * Programa la renovación automática del token
     */
    scheduleTokenRefresh(callback: () => Promise<void>): number {
        const timeUntilExpiry = this.getTimeUntilExpiry();

        // Renovar 5 minutos antes de que expire
        const refreshTime = Math.max(0, (timeUntilExpiry - 300) * 1000);

        return window.setTimeout(async () => {
            try {
                await callback();
            } catch (error) {
                console.error('Token refresh failed:', error);
            }
        }, refreshTime);
    }
}

export const tokenService = new TokenService();
export default tokenService;