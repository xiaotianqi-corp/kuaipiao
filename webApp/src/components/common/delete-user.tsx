import { useRef, useState } from 'react';
import { useForm } from '@/hooks/useForm';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import InputError from "@components/common/input-error";
import HeadingSmall from "@components/common/heading-small";

interface DeleteUserFormData {
    password: string;
}

export default function DeleteUser() {
    const passwordInput = useRef<HTMLInputElement>(null);
    const { logout } = useAuth();
    const [isOpen, setIsOpen] = useState(false);

    const form = useForm<DeleteUserFormData>({
        initialData: {
            password: ''
        },
        endpoint: '/api/settings/profile',
        method: 'DELETE',
        validate: (data) => {
            const errors: Record<string, string> = {};

            if (!data.password) {
                errors.password = 'Password is required to confirm account deletion';
            }

            return errors;
        },
        onSuccess: () => {
            // Cerrar el diálogo y redirigir al logout
            setIsOpen(false);
            logout();
        },
        onError: (errors) => {
            if (errors.password) {
                passwordInput.current?.focus();
            }
        }
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        form.submit();
    };

    const handleCancel = () => {
        setIsOpen(false);
        form.reset();
        form.clearErrors();
    };

    return (
        <div className="space-y-6">
            <HeadingSmall title="Delete account" description="Delete your account and all of its resources" />
            <div className="space-y-4 rounded-lg border border-red-100 bg-red-50 p-4 dark:border-red-200/10 dark:bg-red-700/10">
                <div className="relative space-y-0.5 text-red-600 dark:text-red-100">
                    <p className="font-medium">Warning</p>
                    <p className="text-sm">Please proceed with caution, this cannot be undone.</p>
                </div>

                <Dialog open={isOpen} onOpenChange={setIsOpen}>
                    <DialogTrigger asChild>
                        <Button variant="destructive" onClick={() => setIsOpen(true)}>
                            Delete account
                        </Button>
                    </DialogTrigger>
                    <DialogContent>
                        <DialogTitle>Are you sure you want to delete your account?</DialogTitle>
                        <DialogDescription>
                            Once your account is deleted, all of its resources and data will also be permanently deleted. Please enter your password
                            to confirm you would like to permanently delete your account.
                        </DialogDescription>

                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="grid gap-2">
                                <Label htmlFor="password" className="sr-only">
                                    Password
                                </Label>

                                <Input
                                    id="password"
                                    type="password"
                                    value={form.data.password}
                                    onChange={(e) => form.setField('password', e.target.value)}
                                    placeholder="Password"
                                    autoComplete="current-password"
                                    ref={passwordInput}
                                    disabled={form.processing}
                                />

                                <InputError message={form.errors.password} />
                            </div>

                            <DialogFooter className="gap-2">
                                <DialogClose asChild>
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        onClick={handleCancel}
                                        disabled={form.processing}
                                    >
                                        Cancel
                                    </Button>
                                </DialogClose>

                                <Button
                                    type="submit"
                                    variant="destructive"
                                    disabled={form.processing}
                                >
                                    {form.processing ? 'Deleting...' : 'Delete account'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </DialogContent>
                </Dialog>
            </div>
        </div>
    );
}