import { useState } from 'react';
import { useForm } from '@/hooks/useForm';
import { useAuth } from '@/contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { LoaderCircle } from 'lucide-react';
import TextLink from '@/components/common/text-link';
import { Button } from '@/components/ui/button';
import AuthLayout from '@/layouts/auth-layout';

export default function VerifyEmail() {
    const { logout } = useAuth();
    const navigate = useNavigate();
    const [status, setStatus] = useState<string>('');

    const form = useForm({
        initialData: {},
        endpoint: '/api/auth/resend-verification',
        onSuccess: () => {
            setStatus('verification-link-sent');
        }
    });

    const handleResend = async (e: React.FormEvent) => {
        e.preventDefault();
        await form.submit();
    };

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <AuthLayout
            title="Verify email"
            description="Please verify your email address by clicking on the link we just emailed to you."
        >
            {status === 'verification-link-sent' && (
                <div className="mb-4 text-center text-sm font-medium text-green-600">
                    A new verification link has been sent to the email address you provided during registration.
                </div>
            )}

            <form onSubmit={handleResend} className="space-y-6 text-center">
                <Button
                    type="submit"
                    disabled={form.processing}
                    variant="secondary"
                    className="w-full"
                >
                    {form.processing && <LoaderCircle className="h-4 w-4 animate-spin" />}
                    Resend verification email
                </Button>

                <TextLink
                    to="/login"
                    onClick={handleLogout}
                    className="mx-auto block text-sm"
                >
                    Log out
                </TextLink>
            </form>
        </AuthLayout>
    );
}