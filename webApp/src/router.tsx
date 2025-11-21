import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import AppLayout from '@/layouts/app-layout';
import SignIn from '@/pages/auth/SignIn.tsx';
import Register from '@/pages/auth/register';
import ForgotPassword from '@/pages/auth/forgot-password';
import ResetPassword from '@/pages/auth/reset-password';
import VerifyEmail from '@/pages/auth/verify-email';
import Dashboard from '@/pages/dashboard';
import Profile from '@/pages/settings/profile';
import Password from '@/pages/settings/password';
import Appearance from '@/pages/settings/appearance';
import SignInFactorOne from "@/pages/auth/SignInFactorOne.tsx";
import Api from "@/pages/docs/api.tsx";

function PrivateRoute({ children }: { children: React.ReactNode }) {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="text-center">
                    <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-current border-r-transparent"></div>
                    <p className="mt-2 text-sm text-muted-foreground">Loading...</p>
                </div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/sign-in" replace />;
    }

    return <>{children}</>;
}

export default function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/docs" element={<Api />} />
            <Route path="/sign-in" element={<SignIn />} />
            <Route path="/sign-in/factor-one" element={<SignInFactorOne canResetPassword={true} />} />
            <Route path="/sign-up" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password/:token" element={<ResetPassword token="" email="" />} />
            <Route path="/verify-email" element={<VerifyEmail />} />

            <Route
                path="/dashboard"
                element={
                    <PrivateRoute>
                        <AppLayout>
                            <Dashboard />
                        </AppLayout>
                    </PrivateRoute>
                }
            />

            <Route
                path="/settings"
                element={
                    <PrivateRoute>
                        <Navigate to="/settings/profile" replace />
                    </PrivateRoute>
                }
            />

            <Route
                path="/settings/profile"
                element={
                    <PrivateRoute>
                        <AppLayout>
                            <Profile mustVerifyEmail={false} />
                        </AppLayout>
                    </PrivateRoute>
                }
            />

            <Route
                path="/settings/password"
                element={
                    <PrivateRoute>
                        <AppLayout>
                            <Password />
                        </AppLayout>
                    </PrivateRoute>
                }
            />

            <Route
                path="/settings/appearance"
                element={
                    <PrivateRoute>
                        <AppLayout>
                            <Appearance />
                        </AppLayout>
                    </PrivateRoute>
                }
            />
        </Routes>
    );
}