import { useAuth } from '@/contexts/AuthContext';
import { useForm } from '@/hooks/useForm';
import { useNavigate } from 'react-router-dom';
import { LoaderCircle } from 'lucide-react';
import InputError from '@/components/common/input-error';
import TextLink from '@/components/common/text-link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import AuthLayout from '@/layouts/auth-layout';

export default function Register() {
    const { register } = useAuth();
    const navigate = useNavigate();

    const form = useForm({
        initialData: {
            name: '',
            email: '',
            password: '',
            password_confirmation: ''
        },
        endpoint: '/api/auth/register',
        validate: (data) => {
            const errors: Record<string, string> = {};

            if (!data.name) errors.name = 'Name is required';
            if (!data.email) errors.email = 'Email is required';
            if (!data.password) errors.password = 'Password is required';
            if (data.password.length < 8) errors.password = 'Password must be at least 8 characters';
            if (data.password !== data.password_confirmation) {
                errors.password_confirmation = 'Passwords do not match';
            }

            return errors;
        },
        onSuccess: async () => {
            await register(form.data.name, form.data.email, form.data.password);
            navigate('/dashboard');
        }
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await form.submit();
    };

    return (
        <AuthLayout title="Create an account" description="Enter your details below to create your account">
            <form onSubmit={handleSubmit} className="flex flex-col gap-6">
                <div className="grid gap-6">
                    <div className="grid gap-2">
                        <Label htmlFor="name">Name</Label>
                        <Input
                            id="name"
                            type="text"
                            value={form.data.name}
                            onChange={e => form.setField('name', e.target.value)}
                            required
                            autoFocus
                            tabIndex={1}
                            autoComplete="name"
                            placeholder="Full name"
                        />
                        <InputError message={form.errors.name} />
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="email">Email address</Label>
                        <Input
                            id="email"
                            type="email"
                            value={form.data.email}
                            onChange={e => form.setField('email', e.target.value)}
                            required
                            tabIndex={2}
                            autoComplete="email"
                            placeholder="email@example.com"
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
                            required
                            tabIndex={3}
                            autoComplete="new-password"
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
                            required
                            tabIndex={4}
                            autoComplete="new-password"
                            placeholder="Confirm password"
                        />
                        <InputError message={form.errors.password_confirmation} />
                    </div>

                    <Button type="submit" className="mt-2 w-full" tabIndex={5} disabled={form.processing}>
                        {form.processing && <LoaderCircle className="h-4 w-4 animate-spin" />}
                        Create account
                    </Button>
                </div>

                <div className="text-center text-sm text-muted-foreground">
                    Already have an account?{' '}
                    <TextLink to="/login" tabIndex={6}>
                        Log in
                    </TextLink>
                </div>
            </form>
        </AuthLayout>
    );
}