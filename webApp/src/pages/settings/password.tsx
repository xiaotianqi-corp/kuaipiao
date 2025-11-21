import { useRef, useState } from 'react';
import { useForm } from '@/hooks/useForm';
import AppLayout from '@/layouts/app-layout';
import SettingsLayout from '@/layouts/settings/layout';
import { type BreadcrumbItem } from '@/types';
import { Transition } from '@headlessui/react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { edit } from '@/routes/password';
import HeadingSmall from '@components/common/heading-small';
import InputError from '@components/common/input-error';

const breadcrumbs: BreadcrumbItem[] = [
    {
        title: 'Password settings',
        href: edit().url,
    },
];

interface PasswordFormData {
    current_password: string;
    password: string;
    password_confirmation: string;
}

export default function Password() {
    const passwordInput = useRef<HTMLInputElement>(null);
    const currentPasswordInput = useRef<HTMLInputElement>(null);
    const [recentlySuccessful, setRecentlySuccessful] = useState(false);

    const form = useForm<PasswordFormData>({
        initialData: {
            current_password: '',
            password: '',
            password_confirmation: ''
        },
        endpoint: '/api/settings/password',
        method: 'PUT',
        validate: (data) => {
            const errors: Record<string, string> = {};

            if (!data.current_password) {
                errors.current_password = 'Current password is required';
            }

            if (!data.password) {
                errors.password = 'New password is required';
            } else if (data.password.length < 8) {
                errors.password = 'Password must be at least 8 characters';
            }

            if (!data.password_confirmation) {
                errors.password_confirmation = 'Please confirm your password';
            } else if (data.password !== data.password_confirmation) {
                errors.password_confirmation = 'Passwords do not match';
            }

            return errors;
        },
        onSuccess: () => {
            setRecentlySuccessful(true);
            form.reset();

            // Reset recentlySuccessful after 2 seconds
            setTimeout(() => {
                setRecentlySuccessful(false);
            }, 2000);
        },
        onError: (errors) => {
            if (errors.password) {
                passwordInput.current?.focus();
            }

            if (errors.current_password) {
                currentPasswordInput.current?.focus();
            }
        }
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        form.submit();
    };

    return (
        <AppLayout title="Password settings" description="Password settings" breadcrumbs={breadcrumbs}>

            <SettingsLayout>
                <div className="space-y-6">
                    <HeadingSmall
                        title="Update password"
                        description="Ensure your account is using a long, random password to stay secure"
                    />

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid gap-2">
                            <Label htmlFor="current_password">Current password</Label>

                            <Input
                                id="current_password"
                                ref={currentPasswordInput}
                                type="password"
                                value={form.data.current_password}
                                onChange={(e) => form.setField('current_password', e.target.value)}
                                autoComplete="current-password"
                                placeholder="Current password"
                                disabled={form.processing}
                            />

                            <InputError message={form.errors.current_password} />
                        </div>

                        <div className="grid gap-2">
                            <Label htmlFor="password">New password</Label>

                            <Input
                                id="password"
                                ref={passwordInput}
                                type="password"
                                value={form.data.password}
                                onChange={(e) => form.setField('password', e.target.value)}
                                autoComplete="new-password"
                                placeholder="New password"
                                disabled={form.processing}
                            />

                            <InputError message={form.errors.password} />
                        </div>

                        <div className="grid gap-2">
                            <Label htmlFor="password_confirmation">Confirm password</Label>

                            <Input
                                id="password_confirmation"
                                type="password"
                                value={form.data.password_confirmation}
                                onChange={(e) => form.setField('password_confirmation', e.target.value)}
                                autoComplete="new-password"
                                placeholder="Confirm password"
                                disabled={form.processing}
                            />

                            <InputError message={form.errors.password_confirmation} />
                        </div>

                        <div className="flex items-center gap-4">
                            <Button type="submit" disabled={form.processing}>
                                {form.processing ? 'Saving...' : 'Save password'}
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
            </SettingsLayout>
        </AppLayout>
    );
}