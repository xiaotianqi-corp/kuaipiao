import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from '@/types';

interface AuthState {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}

interface AuthContextType extends AuthState {
    SignIn: (email: string, password: string) => Promise<void>;
    SignOut: () => Promise<void>;
    SignUp: (name: string, email: string, password: string) => Promise<void>;
    updateUser: (user: Partial<User>) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function AuthProvider({ children }: { children: ReactNode }) {
    const [authState, setAuthState] = useState<AuthState>({
        user: null,
        isAuthenticated: false,
        isLoading: true
    });

    useEffect(() => {
        checkAuth();
    }, []);

    const checkAuth = async () => {
        try {
            const token = localStorage.getItem('auth_token');
            if (token) {
                // Validate token with backend
                const response = await fetch('/api/auth/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const user = await response.json();
                    setAuthState({
                        user,
                        isAuthenticated: true,
                        isLoading: false
                    });
                } else {
                    localStorage.removeItem('auth_token');
                    setAuthState({
                        user: null,
                        isAuthenticated: false,
                        isLoading: false
                    });
                }
            } else {
                setAuthState({
                    user: null,
                    isAuthenticated: false,
                    isLoading: false
                });
            }
        } catch (error) {
            console.error('Auth check failed:', error);
            setAuthState({
                user: null,
                isAuthenticated: false,
                isLoading: false
            });
        }
    };

    const SignIn = async (email: string, password: string) => {
        try {
            const response = await fetch('/api/auth/Sign-in', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'SignIn failed');
            }

            const data = await response.json();
            localStorage.setItem('auth_token', data.token);

            setAuthState({
                user: data.user,
                isAuthenticated: true,
                isLoading: false
            });
        } catch (error) {
            throw error;
        }
    };

    const SignUp = async (name: string, email: string, password: string) => {
        try {
            const response = await fetch('/api/auth/sign-up', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name, email, password })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Registration failed');
            }

            const data = await response.json();
            localStorage.setItem('auth_token', data.token);

            setAuthState({
                user: data.user,
                isAuthenticated: true,
                isLoading: false
            });
        } catch (error) {
            throw error;
        }
    };

    const SignOut = async () => {
        try {
            const token = localStorage.getItem('auth_token');
            if (token) {
                await fetch('/api/auth/Sign-out', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            }
        } catch (error) {
            console.error('SignOut error:', error);
        } finally {
            localStorage.removeItem('auth_token');
            setAuthState({
                user: null,
                isAuthenticated: false,
                isLoading: false
            });
        }
    };

    const updateUser = (userData: Partial<User>) => {
        setAuthState(prev => ({
            ...prev,
            user: prev.user ? { ...prev.user, ...userData } : null
        }));
    };

    return (
        <AuthContext.Provider value={{ ...authState, SignIn, SignOut, SignUp, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
}

export default AuthProvider

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}