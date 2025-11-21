import { useState } from 'react';
import { useForm } from '@/hooks/useForm';
import { useAuth } from '@/contexts/AuthContext';
import { Link } from 'react-router-dom';
import { Transition } from '@headlessui/react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import AppLayout from '@/layouts/app-layout';
import SettingsLayout from '@/layouts/settings/layout';
import { edit } from '@/routes/profile';
import { send } from '@/routes/verification';
import { type BreadcrumbItem } from '@/types';
import HeadingSmall from '@components/common/heading-small';
import InputError from '@components/common/input-error';
import DeleteUser from '@components/common/delete-user';

const breadcrumbs: BreadcrumbItem[] = [
    {
        title: 'Profile settings',
        href: edit().url,
    },
];

interface ProfileFormData {
    name: string;
    email: string;
}

interface ProfileProps {
    mustVerifyEmail?: boolean;
    status?: string;
}

export default function Profile({ mustVerifyEmail = false, status }: ProfileProps) {
    const { user, updateUser } = useAuth();
    const [recentlySuccessful, setRecentlySuccessful] = useState(false);

    const form = useForm<ProfileFormData>({
        initialData: {
            name: user?.name || '',
            email: user?.email || ''
        },
        endpoint: '/api/settings/profile',
        method: 'PUT',
        validate: (data) => {
            const errors: Record<string, string> = {};

            if (!data.name) {
                errors.name = 'Name is required';
            }

            if (!data.email) {
                errors.email = 'Email is required';
            } else if (!/\S+@\S+\.\S+/.test(data.email)) {
                errors.email = 'Email is invalid';
            }

            return errors;
        },
        onSuccess: (result) => {
            setRecentlySuccessful(true);
            // Actualizar el usuario en el contexto de autenticación
            if (result.user) {
                updateUser(result.user);
            }

            // Reset recentlySuccessful after 2 seconds
            setTimeout(() => {
                setRecentlySuccessful(false);
            }, 2000);
        }
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        form.submit();
    };

    return (
        <AppLayout title="Profile settings" description="Profile settings" breadcrumbs={breadcrumbs}>
            <SettingsLayout>
                <div className="space-y-6">
                    <HeadingSmall
                        title="Profile information"
                        description="Update your name and email address"
                    />

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid gap-2">
                            <Label htmlFor="name">Name</Label>

                            <Input
                                id="name"
                                className="mt-1 block w-full"
                                value={form.data.name}
                                onChange={(e) => form.setField('name', e.target.value)}
                                required
                                autoComplete="name"
                                placeholder="Full name"
                                disabled={form.processing}
                            />

                            <InputError className="mt-2" message={form.errors.name} />
                        </div>

                        <div className="grid gap-2">
                            <Label htmlFor="email">Email address</Label>

                            <Input
                                id="email"
                                type="email"
                                className="mt-1 block w-full"
                                value={form.data.email}
                                onChange={(e) => form.setField('email', e.target.value)}
                                required
                                autoComplete="username"
                                placeholder="Email address"
                                disabled={form.processing}
                            />

                            <InputError className="mt-2" message={form.errors.email} />
                        </div>

                        {mustVerifyEmail && user?.email_verified_at === null && (
                            <div>
                                <p className="-mt-4 text-sm text-muted-foreground">
                                    Your email address is unverified.{' '}
                                    <Link
                                        to={send().url}
                                        className="text-foreground underline decoration-neutral-300 underline-offset-4 transition-colors duration-300 ease-out hover:decoration-current dark:decoration-neutral-500"
                                    >
                                        Click here to resend the verification email.
                                    </Link>
                                </p>

                                {status === 'verification-link-sent' && (
                                    <div className="mt-2 text-sm font-medium text-green-600">
                                        A new verification link has been sent to your email address.
                                    </div>
                                )}
                            </div>
                        )}

                        <div className="flex items-center gap-4">
                            <Button type="submit" disabled={form.processing}>
                                {form.processing ? 'Saving...' : 'Save'}
                            </Button>

                            <Transition
                                show={recentlySuccessful}
                                enter="transition ease-in-out"
                                enterFrom="opacity-0"
                                leave="transition ease-in-out"
                                leaveTo="opacity-0"
                            >
                                <p className="text-sm text-neutral-600">Saved</p>
                            </Transition>
                        </div>
                    </form>
                </div>

                <DeleteUser />
            </SettingsLayout>
        </AppLayout>
    );
}