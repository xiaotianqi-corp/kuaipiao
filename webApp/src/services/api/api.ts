const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

class ApiService {
    private baseUrl: string;

    constructor() {
        this.baseUrl = API_URL;
    }

    private getHeaders(): HeadersInit {
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        const token = localStorage.getItem('authToken');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        return headers;
    }

    async post(endpoint: string, data: any) {
        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Request failed');
        }

        return response.json();
    }

    async get(endpoint: string) {
        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            method: 'GET',
            headers: this.getHeaders()
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Request failed');
        }

        return response.json();
    }

    // Métodos específicos
    async login(email: string, password: string, remember: boolean) {
        return this.post('/api/auth/sign-in', { email, password, remember });
    }

    async logout() {
        const result = await this.post('/api/auth/logout', {});
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        return result;
    }

    async refreshToken() {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
            throw new Error('No refresh token available');
        }

        const result = await this.post('/api/auth/refresh', { refreshToken });
        localStorage.setItem('authToken', result.tokens.accessToken);
        return result;
    }
}

export const apiService = new ApiService();