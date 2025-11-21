import { useAuth } from '@/contexts/AuthContext';
import { useForm } from '@/hooks/useForm';
import { useNavigate, useLocation } from 'react-router-dom';
import InputError from '@/components/common/input-error';
import TextLink from '@/components/common/text-link';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { ContainerEffect } from "@components/ui/container-effect.tsx";
import { LabelEffect } from "@components/ui/label-effect.tsx";
import { InputEffect } from "@components/ui/input-effect.tsx";
import { BottomEffect } from "@components/ui/bottom-effect.tsx";
import SignInLayout from "@/layouts/SignInLayout.tsx";
import { useState, useEffect } from "react";
import {Tooltip, TooltipContent, TooltipTrigger} from "@/components/ui/tooltip";
import {useDynamicDialog} from "@components/shared/dinamicIsland/DynamicToast.tsx";
import {
    IconArrowNarrowLeft,
    IconArrowNarrowRight,
    IconBrandAppleFilled,
    IconBrandGoogleFilled,
    IconBrandMeta, IconCircleCheck, IconEye, IconEyeClosed, IconInfoTriangleFilled, IconMailCheck,
    IconMailCog,
    IconMailFilled, IconPencilMinus, IconX
} from "@tabler/icons-react";
import { Spinner } from "@/components/ui/spinner";

interface SignInFactorOneProps {
    canResetPassword: boolean;
}

export default function SignInFactorOne({ canResetPassword }: SignInFactorOneProps) {
    const {SignIn} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const {showDialog, closeDialog, DialogComponent} = useDynamicDialog();

    const emailFromState = location.state?.email || '';

    const [showPassword, setShowPassword] = useState(false);
    const [showAlternativeMethods, setShowAlternativeMethods] = useState(false);

    useEffect(() => {
        if (!emailFromState) {
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
        }
    }, [emailFromState, navigate, showDialog, closeDialog]);

    const form = useForm({
        initialData: {
            email: emailFromState,
            password: '',
            remember: false
        },
        endpoint: '/api/auth/sign-in',
        onSuccess: async () => {
            showDialog({
                title: 'Sign In Successful',
                description: 'Welcome back! Redirecting to dashboard...',
                icon: <IconCircleCheck className="w-5 h-5 text-green-600"/>,
                actions: [
                    {
                        label: 'Continue',
                        icon: <IconCircleCheck className="w-4 h-4"/>,
                        variant: 'default',
                        onClick: async () => {
                            closeDialog();
                            await SignIn(form.data.email, form.data.password);
                            navigate('/dashboard');
                        },
                        shouldClose: true,
                    }
                ],
                position: 'top-center',
            });
        },
        onError: (errors) => {
            showDialog({
                title: 'Sign In Failed',
                description: errors.password || errors.email || 'Invalid credentials. Please try again.',
                icon: <IconInfoTriangleFilled className="w-5 h-5 text-destructive"/>,
                actions: [
                    {
                        label: 'Try Again',
                        icon: <IconX className="w-4 h-4"/>,
                        onClick: () => closeDialog(),
                        shouldClose: true,
                    },
                    ...(canResetPassword ? [{
                        label: 'Reset Password',
                        icon: <IconMailCog className="w-4 h-4"/>,
                        onClick: () => {
                            closeDialog();
                            navigate('/forgot-password');
                        },
                        shouldClose: true,
                    }] : [])
                ],
                position: 'top-center',
            });
        }
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!form.data.password) {
            showDialog({
                title: 'Password Required',
                description: 'Please enter your password to continue.',
                icon: <IconInfoTriangleFilled className="w-5 h-5 text-destructive"/>,
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
            return;
        }

        await form.submit();
    };

    const handleBackToEmail = () => {
        showDialog({
            title: 'Change Email?',
            description: 'Do you want to go back and use a different email address?',
            icon: <IconMailCog className="w-5 h-5 text-blue-600"/>,
            actions: [
                {
                    label: 'Cancel',
                    icon: <IconX className="w-4 h-4"/>,
                    onClick: () => closeDialog(),
                    shouldClose: true,
                },
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
        return null;
    }

    return (
        <>
            <DialogComponent/>

            <SignInLayout
                title={showAlternativeMethods ? 'Use another method' : 'Enter your password'}
                description={showAlternativeMethods
                    ? 'Facing issues? You can use any of these methods to sign in.'
                    : 'Enter the password associated with your account'}
            >

                {!showAlternativeMethods ? (
                    <>
                        <div className="-mt-14 flex items-center justify-center">
                            <span className="text-sm text-gray-700 dark:text-gray-300">{emailFromState}</span>
                            <Button
                                onClick={handleBackToEmail}
                                className="text-xs font-medium text-blue-600 hover:underline dark:text-blue-400"
                                type="button"
                                variant={'ghost'}
                            >
                                <IconPencilMinus/>
                            </Button>
                        </div>
                        <form onSubmit={handleSubmit} className="space-y-4" noValidate>
                            <ContainerEffect>
                                <div className="flex items-center justify-between">
                                    <LabelEffect htmlFor="password">Password</LabelEffect>
                                    {canResetPassword && (
                                        <TextLink to="/forgot-password" className="text-sm" tabIndex={5}>
                                            Forgot password?
                                        </TextLink>
                                    )}
                                </div>
                                <div className="relative">
                                    <InputEffect
                                        id="password"
                                        type={showPassword ? "text" : "password"}
                                        value={form.data.password}
                                        onChange={e => form.setField('password', e.target.value)}
                                        autoFocus
                                        tabIndex={1}
                                        autoComplete="current-password"
                                        placeholder="••••••••"
                                    />
                                    <Button
                                        onClick={() => setShowPassword(!showPassword)}
                                        className="absolute right-1 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                                        tabIndex={-1}
                                        variant={'link'}
                                        type="button"
                                    >
                                        {showPassword ? <IconEyeClosed className="h-4 w-4"/> : <IconEye className="h-4 w-4"/>}
                                    </Button>
                                </div>
                                <InputError message={form.errors.password}/>
                            </ContainerEffect>

                            <div className="flex items-center space-x-2">
                                <Checkbox
                                    id="remember"
                                    checked={form.data.remember}
                                    onCheckedChange={(checked) => form.setField('remember', checked)}
                                    tabIndex={2}
                                />
                                <Label htmlFor="remember" className="text-sm font-normal">
                                    Remember me
                                </Label>
                            </div>

                            <Button
                                type="submit"
                                className="group/btn relative block h-10 w-full rounded-md bg-gradient-to-br from-black to-neutral-600 font-medium text-white shadow-[0px_1px_0px_0px_#ffffff40_inset,0px_-1px_0px_0px_#ffffff40_inset] dark:bg-zinc-800 dark:from-zinc-900 dark:to-zinc-900 dark:shadow-[0px_1px_0px_0px_#27272a_inset,0px_-1px_0px_0px_#27272a_inset]"
                                tabIndex={3}
                                disabled={form.processing}
                            >
                                {form.processing ? (
                                    <div className={'flex items-center justify-center gap-2'}>
                                        <Spinner />
                                        <Label className="text-sm font-normal">
                                            Signing in...
                                        </Label>
                                    </div>
                                ) : (
                                    'Continue'
                                )}
                                <BottomEffect/>
                            </Button>

                            <Button
                                type="button"
                                onClick={() => setShowAlternativeMethods(true)}
                                variant="outline"
                                className="w-full"
                                tabIndex={4}
                            >
                                Use another method
                            </Button>
                        </form>
                    </>
                ) : (
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
                                onClick={() => setShowAlternativeMethods(false)}
                                className="w-full font-medium text-blue-600 hover:underline dark:text-blue-400"
                            >
                                <IconArrowNarrowLeft className="mr-2 h-4 w-4"/>
                                Back
                            </Button>

                        </div>
                    </div>
                )}
            </SignInLayout>
        </>
    );
}