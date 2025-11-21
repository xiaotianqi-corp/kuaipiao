import { useState } from "react";
import { useForm } from '@/hooks/useForm';
import TextLink from '@/components/common/text-link';
import { Button } from '@/components/ui/button';
import AuthLayout from '@/layouts/auth-layout';
import {Tooltip, TooltipContent, TooltipTrigger} from "@components/ui/tooltip.tsx";
import {
    IconArrowNarrowLeft, IconArrowNarrowRight,
    IconBrandAppleFilled, IconBrandGoogleFilled,
    IconBrandMeta, IconCircleCheck, IconInfoTriangleFilled,
    IconMailCheck, IconMailFilled, IconX
} from "@tabler/icons-react";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {useDynamicDialog} from "@components/shared/dinamicIsland/DynamicToast.tsx";
import { Spinner } from "@/components/ui/spinner";

export default function ForgotPassword() {
    const [status, setStatus] = useState<string>('');
    const navigate = useNavigate();
    const location = useLocation();
    const {showDialog, closeDialog, DialogComponent} = useDynamicDialog();

    const emailFromState = location.state?.email || '';


    const form = useForm({
        initialData: {
            email: emailFromState
        },
        endpoint: '/api/auth/forgot-password',
        validate: (data) => {
            const errors: Record<string, string> = {};
            if (!data.email) errors.email = 'Email is required';
            return errors;
        },
        onSuccess: () => {
            setStatus('We have emailed your password reset link!');
            form.reset();
        }
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!form.data.email) {
            showDialog({
                title: 'Email Required',
                description: 'We need your email to reset your password.',
                icon: <IconInfoTriangleFilled className="w-5 h-5 text-destructive" />,
                actions: [
                    {
                        label: 'OK',
                        icon: <IconCircleCheck className="w-4 h-4" />,
                        onClick: () => closeDialog(),
                        shouldClose: true,
                    }
                ],
                position: 'top-center',
            });
            return;
        }

        await form.submit();
    };

    const handleOAuthLogin = (provider: string) => {
        showDialog({
            title: `Sign in with ${provider}`,
            description: 'This feature is coming soon. Use email/password for now.',
            icon: <IconInfoTriangleFilled className="w-5 h-5 text-amber-600"/>,
            actions: [
                {
                    label: 'OK',
                    icon: <IconX className="w-4 h-4"/>,
                    onClick: () => closeDialog(),
                    shouldClose: true,
                }
            ],
            position: 'top-center',
        });
    };

    const handleEmailCode = () => {
        showDialog({
            title: 'Email Code Sent',
            description: `A verification code has been sent to ${emailFromState}. Please check your inbox.`,
            icon: <IconMailCheck className="w-5 h-5 text-blue-600"/>,
            actions: [
                {
                    label: 'OK',
                    icon: <IconCircleCheck className="w-4 h-4"/>,
                    onClick: () => closeDialog(),
                    shouldClose: true,
                }
            ],
            position: 'top-center',
        });
    };

    if (!emailFromState) {
        return <Navigate to="/sign-in" replace />;
    }

    return (
        <>
            <DialogComponent/>

        <AuthLayout title="Forgot password" description="Enter your email to receive a password reset link">

            {status && <div className="mb-4 text-center text-sm font-medium text-green-600">{status}</div>}

            <div className="space-y-6">
                <form onSubmit={handleSubmit}>
                    <div className="my-6 flex items-center justify-start">
                        <Button className="w-full" disabled={form.processing}>
                            {form.processing && <Spinner />}
                            Reset your password
                        </Button>
                    </div>
                </form>

                <div className="space-x-1 text-center text-sm text-muted-foreground">
                    <span>Or, return to</span>
                    <TextLink to="/sign-in">log in</TextLink>
                </div>

                <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                        <span className="bg-card text-muted-foreground relative z-10 px-2">
                            Or, sign in with another method
                        </span>
                </div>

                <div className="-mt-10 items-center">
                    <div className="space-y-4">
                        <div className="space-y-3 flex flex-col items-center justify-center">
                            <div className="space-x-3">
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Button variant="outline" onClick={() => handleOAuthLogin('Google')} >
                                            <IconBrandGoogleFilled/>
                                            <p>Google</p>
                                        </Button>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Continue with Google</p>
                                    </TooltipContent>
                                </Tooltip>
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Button variant="outline" onClick={() => handleOAuthLogin('Apple')} >
                                            <IconBrandAppleFilled/>
                                            <p>Apple</p>
                                        </Button>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Continue with Apple</p>
                                    </TooltipContent>
                                </Tooltip>
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Button variant="outline" onClick={() => handleOAuthLogin('Meta')} >
                                            <IconBrandMeta/>
                                            <p>Meta</p>
                                        </Button>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Continue with Meta</p>
                                    </TooltipContent>
                                </Tooltip>
                            </div>

                            <Button variant="outline" onClick={handleEmailCode}>
                                <IconMailFilled />
                                <div className="font-medium text-gray-900 dark:text-gray-50">Email code</div>
                                <div className="text-sm text-gray-600 dark:text-gray-400">to {emailFromState}</div>
                            </Button>
                        </div>
                    </div>
                    <div className="text-sm text-gray-600 dark:text-gray-400 items-center justify-center mt-2 flex">
                        Don't have any of these?{' '}
                        <Button
                            variant="link"
                            onClick={() => {
                                showDialog({
                                    title: 'Need Help?',
                                    description: 'Our support team is here to help you regain access to your account.',
                                    icon: <IconMailFilled className="w-5 h-5 text-blue-600"/>,
                                    actions: [
                                        {
                                            label: 'Contact Support',
                                            icon: <IconMailFilled className="w-4 h-4"/>,
                                            onClick: () => {
                                                closeDialog();
                                                window.location.href = 'mailto:support@kuaipiao.com';
                                            },
                                            shouldClose: true,
                                        },
                                        {
                                            label: 'Cancel',
                                            icon: <IconX className="w-4 h-4"/>,
                                            onClick: () => closeDialog(),
                                            shouldClose: true,
                                        }
                                    ],
                                    position: 'top-center',
                                });
                            }}
                            className="group font-medium text-blue-600 hover:underline dark:text-blue-400 flex items-center gap-1"
                        >
                            <span>Get help</span>

                            <IconArrowNarrowRight
                                className="w-4 h-4 opacity-0 translate-x-[-4px] transition-all duration-200 group-hover:opacity-100 group-hover:translate-x-0"
                            />
                        </Button>
                    </div>

                    <div className="mt-2 border-t border-gray-200 pt-2 dark:border-gray-700 flex">
                        <Button
                            variant="ghost"
                            onClick={() => {
                                showDialog({
                                    title: 'Email Required',
                                    description: 'Please enter your email address first.',
                                    icon: <IconInfoTriangleFilled className="w-5 h-5 text-destructive"/>,
                                    actions: [
                                        {
                                            label: 'Go Back',
                                            icon: <IconArrowNarrowLeft className="w-4 h-4"/>,
                                            onClick: () => {
                                                closeDialog();
                                                navigate('/sign-in');
                                            },
                                            shouldClose: true,
                                        }
                                    ],
                                    position: 'top-center',
                                });
                            }}
                            className="w-full font-medium text-blue-600 hover:underline dark:text-blue-400"
                        >
                            <IconArrowNarrowLeft className="mr-2 h-4 w-4"/>
                            Back
                        </Button>
                    </div>
                </div>
            </div>
        </AuthLayout>
        </>
    );
}