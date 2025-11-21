import { useState, useCallback, FormEvent } from 'react';

interface UseFormOptions<T> {
    initialData: T;
    endpoint: string;
    method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
    onSuccess?: (data: any) => void;
    onError?: (errors: Record<string, string>) => void;
    validate?: (data: T) => Record<string, string>;
}

interface FormState<T> {
    data: T;
    errors: Record<string, string>;
    processing: boolean;
    recentlySuccessful: boolean;
}

export function useForm<T extends Record<string, any>>({
                                                           initialData,
                                                           endpoint,
                                                           method = 'POST',
                                                           onSuccess,
                                                           onError,
                                                           validate
                                                       }: UseFormOptions<T>) {
    const [state, setState] = useState<FormState<T>>({
        data: initialData,
        errors: {},
        processing: false,
        recentlySuccessful: false
    });

    const setField = useCallback((field: keyof T, value: any) => {
        setState(prev => ({
            ...prev,
            data: {
                ...prev.data,
                [field]: value
            },
            errors: {
                ...prev.errors,
                [field]: ''
            }
        }));
    }, []);

    const setData = useCallback((data: Partial<T>) => {
        setState(prev => ({
            ...prev,
            data: {
                ...prev.data,
                ...data
            }
        }));
    }, []);

    const setErrors = useCallback((errors: Record<string, string>) => {
        setState(prev => ({
            ...prev,
            errors
        }));
    }, []);

    const clearErrors = useCallback(() => {
        setState(prev => ({
            ...prev,
            errors: {}
        }));
    }, []);

    const reset = useCallback(() => {
        setState({
            data: initialData,
            errors: {},
            processing: false,
            recentlySuccessful: false
        });
    }, [initialData]);

    const submit = useCallback(async (e?: FormEvent) => {
        if (e) {
            e.preventDefault();
        }

        // Client-side validation
        if (validate) {
            const validationErrors = validate(state.data);
            if (Object.keys(validationErrors).length > 0) {
                setErrors(validationErrors);
                if (onError) {
                    onError(validationErrors);
                }
                return;
            }
        }

        setState(prev => ({ ...prev, processing: true, errors: {} }));

        try {
            const token = localStorage.getItem('auth_token');
            const response = await fetch(endpoint, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                },
                body: method !== 'GET' ? JSON.stringify(state.data) : undefined
            });

            const result = await response.json();

            if (!response.ok) {
                if (result.errors) {
                    setState(prev => ({
                        ...prev,
                        errors: result.errors,
                        processing: false
                    }));
                    if (onError) {
                        onError(result.errors);
                    }
                } else {
                    throw new Error(result.message || 'Request failed');
                }
                return;
            }

            setState(prev => ({
                ...prev,
                processing: false,
                recentlySuccessful: true
            }));

            if (onSuccess) {
                onSuccess(result);
            }

            // Reset recentlySuccessful after 2 seconds
            setTimeout(() => {
                setState(prev => ({
                    ...prev,
                    recentlySuccessful: false
                }));
            }, 2000);
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'An error occurred';
            setState(prev => ({
                ...prev,
                errors: { submit: errorMessage },
                processing: false
            }));
            if (onError) {
                onError({ submit: errorMessage });
            }
        }
    }, [state.data, endpoint, method, validate, onSuccess, onError]);

    return {
        data: state.data,
        errors: state.errors,
        processing: state.processing,
        recentlySuccessful: state.recentlySuccessful,
        setField,
        setData,
        setErrors,
        clearErrors,
        reset,
        submit,
        setProcessing(processing: boolean) {
            setState(prev => ({
                ...prev,
                processing
            }));
        },

        onSuccess(result: { success: boolean; error?: string; user?: any }) {
            if (result.success) {
                setState(prev => ({
                    ...prev,
                    processing: false,
                    recentlySuccessful: true
                }));
                // Reset recentlySuccessful after 2 seconds
                setTimeout(() => {
                    setState(prev => ({
                        ...prev,
                        recentlySuccessful: false
                    }));
                }, 2000);
            } else {
                setState(prev => ({
                    ...prev,
                    errors: { submit: result.error || 'An error occurred' },
                    processing: false
                }));
            }
        },
        onError(param: { password: string | undefined }) {
            setState(prev => ({
                ...prev,
                errors: {
                    password: param.password || 'An error occurred',
                    general: 'Network error. Please try again.'
                },
                processing: false
            }));
        },
    };
}