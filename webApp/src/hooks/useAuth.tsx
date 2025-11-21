import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService } from '@/services/auth/authService';
import { tokenService } from '@/services/auth/tokenService';

interface User {
    id: string;
    email: string;
    name: string;
    role: string;
    enterpriseId?: string;
    organizationId?: string;
}

interface AuthContextType {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (email: string, password: string, remember?: boolean) => Promise<void>;
    register: (data: any) => Promise<void>;
    logout: () => Promise<void>;
    refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const initAuth = async () => {
            try {
                if (tokenService.hasValidToken()) {
                    const currentUser = await authService.getCurrentUser();
                    setUser(currentUser);
                }
            } catch (error) {
                console.error('Auth initialization error:', error);
                tokenService.clearTokens();
            } finally {
                setIsLoading(false);
            }
        };

        initAuth();
    }, []);

    useEffect(() => {
        let timeoutId: number;

        if (user) {
            timeoutId = tokenService.scheduleTokenRefresh(async () => {
                try {
                    await authService.refreshToken();
                } catch (error) {
                    console.error('Token refresh failed:', error);
                    await logout();
                }
            });
        }

        return () => {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
        };
    }, [user]);

    const login = async (email: string, password: string, remember = false) => {
        const response = await authService.login({ email, password, remember });
        setUser(response.user);
    };

    const register = async (data: any) => {
        const response = await authService.register(data);
        setUser(response.user);
    };

    const logout = async () => {
        await authService.logout();
        setUser(null);
    };

    const refreshUser = async () => {
        const currentUser = await authService.getCurrentUser();
        setUser(currentUser);
    };

    return (
        <AuthContext.Provider
            value={{
        user,
            isAuthenticated: !!user,
            isLoading,
            login,
            register,
            logout,
            refreshUser
    }}
>
    {children}
    </AuthContext.Provider>
);
}

export default AuthProvider

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
}