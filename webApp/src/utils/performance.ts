// Performance monitoring utilities
import { useState, useEffect } from 'react';

export class PerformanceMonitor {
    private static measurements = new Map<string, number>();

    static startMeasurement(name: string): void {
        this.measurements.set(name, performance.now());
    }

    static endMeasurement(name: string): number {
        const startTime = this.measurements.get(name);
        if (!startTime) {
            console.warn(`No start measurement found for: ${name}`);
            return 0;
        }
        
        const duration = performance.now() - startTime;
        this.measurements.delete(name);
        
        // Log only in development
        if (!import.meta.env.PROD) {
            console.log(`⏱️ ${name}: ${duration.toFixed(2)}ms`);
        }
        
        return duration;
    }

    static measureAsync<T>(name: string, fn: () => Promise<T>): Promise<T> {
        return new Promise(async (resolve, reject) => {
            this.startMeasurement(name);
            try {
                const result = await fn();
                this.endMeasurement(name);
                resolve(result);
            } catch (error) {
                this.endMeasurement(name);
                reject(error);
            }
        });
    }
}

// Debounce utility for performance optimization
export function debounce<T extends (...args: any[]) => void>(
    func: T,
    wait: number
): (...args: Parameters<T>) => void {
    let timeout: ReturnType<typeof setTimeout>;
    return (...args: Parameters<T>) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => func(...args), wait);
    };
}

// Throttle utility for performance optimization  
export function throttle<T extends (...args: any[]) => void>(
    func: T,
    limit: number
): (...args: Parameters<T>) => void {
    let inThrottle: boolean;
    return (...args: Parameters<T>) => {
        if (!inThrottle) {
            func(...args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// Intersection Observer hook for lazy loading
export function useIntersectionObserver(
    elementRef: React.RefObject<Element>,
    {
        threshold = 0,
        root = null,
        rootMargin = '0%',
    }: IntersectionObserverInit = {}
): IntersectionObserverEntry | undefined {
    const [entry, setEntry] = useState<IntersectionObserverEntry>();

    const frozen = entry?.isIntersecting;

    const updateEntry = ([entry]: IntersectionObserverEntry[]): void => {
        setEntry(entry);
    };

    useEffect(() => {
        const node = elementRef?.current;
        const hasIOSupport = !!window.IntersectionObserver;

        if (!hasIOSupport || frozen || !node) return;

        const observerParams = { threshold, root, rootMargin };
        const observer = new IntersectionObserver(updateEntry, observerParams);

        observer.observe(node);

        return () => observer.disconnect();
    }, [elementRef?.current, JSON.stringify({ threshold, root, rootMargin }), frozen]);

    return entry;
}