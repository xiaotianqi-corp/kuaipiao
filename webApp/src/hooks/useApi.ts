import { useState, useEffect, useCallback } from 'react';
import { PerformanceMonitor } from '../utils/performance';

interface ApiState<T> {
    data: T | null;
    loading: boolean;
    error: string | null;
}

interface ApiOptions {
    cache?: boolean;
    cacheTimeout?: number; // in milliseconds
    retries?: number;
    retryDelay?: number; // in milliseconds
}

// Simple cache implementation
const apiCache = new Map<string, { data: any; timestamp: number; ttl: number }>();

export function useApi<T>(
    url: string,
    options: ApiOptions = {}
): ApiState<T> & {
    refetch: () => Promise<void>;
    clearCache: () => void;
} {
    const {
        cache = true,
        cacheTimeout = 5 * 60 * 1000, // 5 minutes default
        retries = 3,
        retryDelay = 1000
    } = options;

    const [state, setState] = useState<ApiState<T>>({
        data: null,
        loading: false,
        error: null
    });

    const getCacheKey = useCallback(() => url, [url]);

    const fetchData = useCallback(async (attempt = 1): Promise<void> => {
        const cacheKey = getCacheKey();
        
        // Check cache first
        if (cache) {
            const cached = apiCache.get(cacheKey);
            if (cached && Date.now() - cached.timestamp < cached.ttl) {
                setState(prev => ({
                    ...prev,
                    data: cached.data,
                    loading: false,
                    error: null
                }));
                return;
            }
        }

        setState(prev => ({ ...prev, loading: true, error: null }));

        try {
            const response = await PerformanceMonitor.measureAsync(`api-${url}`, async () => {
                const res = await fetch(url);
                if (!res.ok) {
                    throw new Error(`HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            });

            // Cache the response
            if (cache) {
                apiCache.set(cacheKey, {
                    data: response,
                    timestamp: Date.now(),
                    ttl: cacheTimeout
                });
            }

            setState({
                data: response,
                loading: false,
                error: null
            });
        } catch (error) {
            if (attempt < retries) {
                setTimeout(() => fetchData(attempt + 1), retryDelay * attempt);
                return;
            }

            setState(prev => ({
                ...prev,
                loading: false,
                error: error instanceof Error ? error.message : 'Unknown error occurred'
            }));
        }
    }, [url, cache, cacheTimeout, retries, retryDelay, getCacheKey]);

    const clearCache = useCallback(() => {
        const cacheKey = getCacheKey();
        apiCache.delete(cacheKey);
    }, [getCacheKey]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return {
        ...state,
        refetch: fetchData,
        clearCache
    };
}

// Global cache management
export const ApiCache = {
    clear: () => apiCache.clear(),
    remove: (key: string) => apiCache.delete(key),
    size: () => apiCache.size,
    cleanup: (maxAge: number = 30 * 60 * 1000) => { // 30 minutes default
        const now = Date.now();
        for (const [key, value] of apiCache.entries()) {
            if (now - value.timestamp > maxAge) {
                apiCache.delete(key);
            }
        }
    }
};