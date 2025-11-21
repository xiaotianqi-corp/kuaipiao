// layouts/SignInLayout.tsx
import AuthLayoutTemplate from '@/layouts/auth/auth-simple-layout';
import {Toaster} from "sonner";

export default function SignInLayout({
                                         children,
                                         title,
                                         description,
                                         variant = 'default',
                                         showFooter = true,
                                         ...props
                                     }: {
    children: React.ReactNode;
    title: string;
    description: string;
    variant?: 'default' | 'minimal' | 'branded';
    showFooter?: boolean;
}) {
    return (
        <AuthLayoutTemplate
            title={title}
            description={description}
            variant={variant}
            showFooter={showFooter}
            {...props}
        >
            <Toaster
                position="top-center"
                expand
                duration={5000}
                offset={12}
            />
            {children}
        </AuthLayoutTemplate>
    );
}