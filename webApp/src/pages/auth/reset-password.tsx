import { useForm } from '@/hooks/useForm';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { LoaderCircle } from 'lucide-react';
import InputError from '@/components/common/input-error';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import AuthLayout from '@/layouts/auth-layout';

interface ResetPasswordProps {
    token: string;
    email: string;
}

export default function ResetPassword({ token: initialToken, email: initialEmail }: ResetPasswordProps) {
    const navigate = useNavigate();
    const { token: paramToken } = useParams();
    const [searchParams] = useSearchParams();

    const token = paramToken || initialToken;
    const email = searchParams.get('email') || initialEmail;

    const form = useForm({
        initialData: {
            token,
            email,
            password: '',
            password_confirmation: ''
        },
        endpoint: '/api/auth/reset-password',
        validate: (data) => {
            const errors: Record<string, string> = {};
            if (!data.password) errors.password = 'Password is required';
            if (data.password.length < 8) errors.password = 'Password must be at least 8 characters';
            if (data.password !== data.password_confirmation) {
                errors.password_confirmation = 'Passwords do not match';
            }
            return errors;
        },
        onSuccess: () => {
            navigate('/login?reset=true');
        }
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await form.submit();
    };

    return (
        <AuthLayout title="Reset password" description="Please enter your new password below">
            <form onSubmit={handleSubmit}>
                <div className="grid gap-6">
                    <div className="grid gap-2">
                        <Label htmlFor="email">Email</Label>
                        <Input
                            id="email"
                            type="email"
                            value={form.data.email}
                            autoComplete="email"
                            readOnly
                            className="bg-muted"
                        />
                        <InputError message={form.errors.email} />
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="password">Password</Label>
                        <Input
                            id="password"
                            type="password"
                            value={form.data.password}
                            onChange={e => form.setField('password', e.target.value)}
                            autoComplete="new-password"
                            autoFocus
                            placeholder="Password"
                        />
                        <InputError message={form.errors.password} />
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="password_confirmation">Confirm password</Label>
                        <Input
                            id="password_confirmation"
                            type="password"
                            value={form.data.password_confirmation}
                            onChange={e => form.setField('password_confirmation', e.target.value)}
                            autoComplete="new-password"
                            placeholder="Confirm password"
                        />
                        <InputError message={form.errors.password_confirmation} />
                    </div>

                    <Button type="submit" className="mt-4 w-full" disabled={form.processing}>
                        {form.processing && <LoaderCircle className="h-4 w-4 animate-spin" />}
                        Reset password
                    </Button>
                </div>
            </form>
        </AuthLayout>
    );
}