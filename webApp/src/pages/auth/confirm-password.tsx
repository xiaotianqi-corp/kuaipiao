import { useState } from 'react';
import { useForm } from '@/hooks/useForm';
import { useAuth } from '@/contexts/AuthContext';
import { LoaderCircle } from 'lucide-react';
import InputError from '@/components/common/input-error';
import TextLink from '@/components/common/text-link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import AuthLayout from '@/layouts/auth-layout';

export default function ConfirmPassword() {
    const [status, setStatus] = useState<string>('');
    const { user } = useAuth();

    const form = useForm({
        initialData: {
            password: ''
        },
        endpoint: '/api/auth/confirm-password',
        method: 'POST',
        validate: (data) => {
            const errors: Record<string, string> = {};
            if (!data.password) errors.password = 'Password is required';
            return errors;
        },
        onSuccess: () => {
            setStatus('Password confirmed successfully!');
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1500);
        },
        onError: (errors) => {
            if (errors.submit) {
                setStatus(errors.submit);
            } else if (errors.password) {
                setStatus('Please check your password and try again.');
            }
        }
    });

    return (
        <AuthLayout
            title="Confirm your password"
            description="This is a secure area of the application. Please confirm your password before continuing."
        >
            {status && (
                <div className={`mb-4 text-center text-sm font-medium ${
                    status.includes('successfully')
                        ? 'text-green-600'
                        : 'text-red-600'
                }`}>
                    {status}
                </div>
            )}

            {form.recentlySuccessful && (
                <div className="mb-4 text-center text-sm font-medium text-green-600">
                    Password confirmed! Redirecting...
                </div>
            )}

            {user && (
                <div className="mb-4 text-center text-sm text-muted-foreground">
                    Currently logged in as: <strong>{user.email}</strong>
                </div>
            )}

            <div className="space-y-6">
                <form onSubmit={form.submit}>
                    <div className="grid gap-2">
                        <Label htmlFor="password">Password</Label>
                        <Input
                            id="password"
                            type="password"
                            name="password"
                            value={form.data.password}
                            onChange={e => form.setField('password', e.target.value)}
                            placeholder="Enter your current password"
                            autoComplete="current-password"
                            autoFocus
                            disabled={form.processing}
                        />
                        <InputError message={form.errors.password} />
                    </div>

                    <div className="flex items-center mt-6">
                        <Button
                            className="w-full"
                            disabled={form.processing}
                            type="submit"
                        >
                            {form.processing && <LoaderCircle className="h-4 w-4 animate-spin mr-2" />}
                            Confirm password
                        </Button>
                    </div>
                </form>

                <div className="space-x-1 text-center text-sm text-muted-foreground">
                    <span>Having trouble?</span>
                    <TextLink to="/forgot-password">Reset your password</TextLink>
                </div>
            </div>
        </AuthLayout>
    );
}