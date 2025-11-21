interface ApiConfig {
    baseURL: string;
    timeout?: number;
}

class ApiClient {
    private baseURL: string;
    private timeout: number;

    constructor(config: ApiConfig) {
        this.baseURL = config.baseURL;
        this.timeout = config.timeout || 30000;
    }

    private getHeaders(): HeadersInit {
        const token = localStorage.getItem('auth_token');
        return {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        };
    }

    private async request<T>(
        endpoint: string,
        options: RequestInit = {}
    ): Promise<T> {
        const url = `${this.baseURL}${endpoint}`;

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), this.timeout);

        try {
            const response = await fetch(url, {
                ...options,
                headers: {
                    ...this.getHeaders(),
                    ...options.headers
                },
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                const error = await response.json().catch(() => ({
                    message: 'Request failed'
                }));
                throw new Error(error.message || `HTTP ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            if (error instanceof Error) {
                if (error.name === 'AbortError') {
                    throw new Error('Request timeout');
                }
                throw error;
            }
            throw new Error('Unknown error occurred');
        }
    }

    async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
        const queryString = params
            ? '?' + new URLSearchParams(params).toString()
            : '';
        return this.request<T>(`${endpoint}${queryString}`, {
            method: 'GET'
        });
    }

    async post<T>(endpoint: string, data?: any): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async put<T>(endpoint: string, data?: any): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async patch<T>(endpoint: string, data?: any): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data)
        });
    }

    async delete<T>(endpoint: string): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'DELETE'
        });
    }
}

// Default API client instance
export const apiClient = new ApiClient({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    timeout: 30000
});

export default apiClient;