import React from 'react';
import AppLogoIcon from "@components/common/app-logo-icon";
import FallingCard from '@/components/shared/FallingCard';
import GlowEffect from '@/components/shared/GlowEffect';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { home } from '@/routes';
import { Link } from 'react-router-dom';
import { Mail, ArrowRight } from 'lucide-react';
import {
    AuthLayoutProps,
    AuthCardHeaderProps,
    AuthLayoutFooterProps,
    FooterLink,
    ContactInfo,
    CompanyInfo
} from "@/types/auth";
import { DotPattern } from "@components/ui/dot-pattern";

const DecorativeSVG: React.FC<{
    className?: string;
    position: 'left' | 'right';
    'aria-hidden'?: boolean;
}> = ({ className, position, ...props }) => (
    <svg
        fill="none"
        viewBox="0 0 301 692"
        className={cn(
            "absolute w-72 h-full pointer-events-none",
            "text-ceramic-gray-50 dark:text-ceramic-gray-1200/20",
            "[mask-image:radial-gradient(80%_100%_at_5%_20%,black,transparent)]",
            position === 'left' ? "left-full top-5 -ml-2" : "right-full top-5 -mr-2 -scale-x-100",
            className
        )}
        aria-hidden="true"
        {...props}
    >
        <g filter="url(#filter0_d_35_2)">
            <path
                fill="currentColor"
                stroke="currentColor"
                className="stroke-ceramic-gray-200 dark:stroke-ceramic-gray-900"
                d="M298,11.8v666.8c0,3.3-2.7,6-6,6h-43.8c-1.6,0-3.2-.7-4.3-1.8l-91.6-95.7c-1.1-1.2-2.7-1.9-4.3-1.9H45.5c-1.6,0-3.2-.6-4.3-1.8l-36.5-37.4c-1.1-1.1-1.7-2.6-1.7-4.2V148.6c0-1.6.6-3.1,1.7-4.2l36.5-37.4c1.1-1.2,2.7-1.8,4.3-1.8h102.4c1.6,0,3.2-.7,4.3-1.9L243.8,7.6c1.1-1.2,2.7-1.9,4.3-1.9h43.8c3.3,0,6,2.7,6,6Z"
            />
        </g>
        <defs>
            <filter
                id="filter0_d_35_2"
                width="300"
                height="692"
                x=".5"
                y=".125"
                colorInterpolationFilters="sRGB"
                filterUnits="userSpaceOnUse"
            >
                <feFlood floodOpacity="0" result="BackgroundImageFix" />
                <feColorMatrix
                    in="SourceAlpha"
                    result="hardAlpha"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
                />
                <feOffset />
                <feGaussianBlur stdDeviation="1.25" />
                <feComposite in2="hardAlpha" operator="out" />
                <feColorMatrix values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.3 0" />
                <feBlend in2="BackgroundImageFix" result="effect1_dropShadow_35_2" />
                <feBlend in="SourceGraphic" in2="effect1_dropShadow_35_2" result="shape" />
            </filter>
        </defs>
    </svg>
);

const AuthCardHeader: React.FC<AuthCardHeaderProps> = ({
                                                           title,
                                                           description,
                                                           variant = 'default'
                                                       }) => {
    const logoVariants = {
        default: "bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-ceramic-gray-1100 dark:to-ceramic-gray-1200 border-ceramic-gray-200 dark:border-ceramic-gray-900",
        minimal: "bg-ceramic-gray-100 dark:bg-ceramic-gray-1100 border-ceramic-gray-300 dark:border-ceramic-gray-900",
        branded: "bg-gradient-to-br from-blue-500 to-indigo-600 border-blue-400 dark:border-indigo-500"
    };

    const iconVariants = {
        default: "text-blue-600 dark:text-blue-400 group-hover:text-blue-700 dark:group-hover:text-blue-300",
        minimal: "text-ceramic-gray-700 dark:text-ceramic-gray-300 group-hover:text-ceramic-gray-900 dark:group-hover:text-ceramic-gray-100",
        branded: "text-white dark:text-white group-hover:text-blue-50"
    };

    const titleVariants = {
        default: "text-black dark:text-ceramic-gray-100",
        minimal: "text-ceramic-gray-900 dark:text-ceramic-gray-50",
        branded: "bg-gradient-to-r from-blue-600 to-indigo-600 dark:from-blue-400 dark:to-indigo-400 bg-clip-text text-transparent font-extrabold"
    };

    const descriptionVariants = {
        default: "text-ceramic-gray-900 dark:text-ceramic-gray-400",
        minimal: "text-ceramic-gray-700 dark:text-ceramic-gray-500",
        branded: "text-ceramic-gray-800 dark:text-ceramic-gray-300 font-medium"
    };

    return (
        <div className="flex flex-col gap-4">
            <div className={cn(
                "flex h-3 w-12 flex-col self-center rounded-full shadow-sm",
                variant === 'branded'
                    ? "bg-gradient-to-r from-blue-400 to-indigo-500"
                    : "bg-ceramic-gray-300"
            )}></div>
            <>
                <div className="flex flex-col items-center gap-2 text-center">
                    <Link
                        to={home().url}
                        className="group flex flex-col items-center gap-2 font-medium z-10 transition-transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 rounded-md"
                    >
                        <div className={cn(
                            "mb-1 flex h-10 w-10 items-center justify-center rounded-lg shadow-sm border group-hover:shadow-md transition-all",
                            logoVariants[variant]
                        )}>
                            <AppLogoIcon className={cn(
                                "size-6 fill-current transition-colors",
                                iconVariants[variant]
                            )} />
                        </div>
                        <span className="sr-only">Ir a inicio - {title}</span>
                    </Link>
                    {title && (
                        <h1 className={cn(
                            "text-center text-md font-bold leading-tight md:text-md",
                            titleVariants[variant]
                        )}>
                            {title}
                        </h1>
                    )}
                    <div>
                        {description && (
                            <p className={cn(
                                "text-center text-sm max-w-sm mx-auto leading-relaxed",
                                descriptionVariants[variant]
                            )}>
                                {description}
                            </p>
                        )}
                    </div>
                </div>
            </>
        </div>
    );
};

const AuthFooter: React.FC<AuthLayoutFooterProps & { className?: string }> = ({
                                                                                  companyInfo,
                                                                                  links,
                                                                                  contactInfo,
                                                                                  className
                                                                              }) => (
    <footer className={cn("w-full min-w-7xl mx-auto px-4", className)}>
        <div className="flex flex-col lg:flex-row items-center justify-between gap-6 py-6 border-t border-ceramic-gray-200 dark:border-ceramic-gray-800">
            <div className="flex flex-col sm:flex-row items-center gap-4 text-sm text-ceramic-gray-600 dark:text-ceramic-gray-400">
                <span className="font-medium">
                    © {companyInfo.year} {companyInfo.name}. Todos los derechos reservados.
                </span>

                <nav className="flex items-center gap-1" role="navigation" aria-label="Enlaces legales">
                    {links.map((link, index) => (
                        <React.Fragment key={`legal-${index}`}>
                            <a
                                href={link.href}
                                className="px-2 py-1 hover:text-ceramic-gray-900 dark:hover:text-ceramic-gray-100 transition-colors rounded focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
                                {...(link.external && {
                                    target: "_blank",
                                    rel: "noopener noreferrer"
                                })}
                            >
                                {link.text}
                            </a>
                            {index < links.length - 1 && (
                                <span className="hidden sm:inline text-ceramic-gray-400 dark:text-ceramic-gray-600 mx-1">•</span>
                            )}
                        </React.Fragment>
                    ))}
                </nav>
            </div>

            <div className="flex flex-col sm:flex-row items-center gap-4">
                <div className="flex items-center gap-2 text-sm text-ceramic-gray-600 dark:text-ceramic-gray-400">
                    <Mail className="h-4 w-4 flex-shrink-0" aria-hidden="true" />
                    <a
                        href={`mailto:${contactInfo.email}`}
                        className="hover:text-ceramic-gray-900 dark:hover:text-ceramic-gray-100 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded"
                    >
                        {contactInfo.email}
                    </a>
                </div>

                <Button
                    variant="outline"
                    size="sm"
                    asChild
                    className="group hover:border-blue-500 hover:text-blue-600 dark:hover:text-blue-400 transition-all"
                >
                    <a href={contactInfo.salesUrl} className="flex items-center gap-2">
                        {contactInfo.salesText}
                        <ArrowRight className="h-3 w-3 transition-transform group-hover:translate-x-0.5" />
                    </a>
                </Button>
            </div>
        </div>
    </footer>
);

export default function AuthSimpleLayout({
                                             children,
                                             name,
                                             title,
                                             description,
                                             backgroundImage,
                                             showFooter = true,
                                             variant = 'default'
                                         }: React.PropsWithChildren<AuthLayoutProps>) {
    const currentYear = new Date().getFullYear();

    const legalLinks: FooterLink[] = React.useMemo(() => [
        {text: 'Política de Privacidad', href: '/privacy'},
        {text: 'Términos de Servicio', href: '/terms'},
        {text: 'Política de Cookies', href: '/cookies'},
        {text: 'Soporte', href: '/support'},
    ], []);

    const contactInfo: ContactInfo = React.useMemo(() => ({
        email: 'support@kuaipiao.com',
        salesUrl: '/contact-sales',
        salesText: 'Contactar Ventas'
    }), []);

    const companyInfo: CompanyInfo = React.useMemo(() => ({
        name: name || 'Kuaipiao, Inc',
        year: currentYear
    }), [currentYear, name]);

    const layoutVariants = {
        default: "bg-gradient-to-br from-ceramic-gray-50 via-ceramic-gray-100 to-ceramic-gray-200 dark:from-ceramic-gray-1300 dark:via-ceramic-gray-1250 dark:to-ceramic-gray-1200",
        minimal: "bg-ceramic-gray-50 dark:bg-ceramic-gray-1300",
        branded: "bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-ceramic-gray-1300 dark:via-blue-900/10 dark:to-purple-900/10"
    };

    const cardVariants = {
        default: "bg-white/80 dark:bg-ceramic-gray-1200/80 backdrop-blur-sm border-ceramic-gray-200/50 dark:border-ceramic-gray-900/50",
        minimal: "bg-white dark:bg-ceramic-gray-1200 border-ceramic-gray-200 dark:border-ceramic-gray-900",
        branded: "bg-white/90 dark:bg-ceramic-gray-1200/90 backdrop-blur-md border-blue-200/50 dark:border-indigo-500/30"
    };

    const backgroundStyle = backgroundImage
        ? {backgroundImage: `url(${backgroundImage})`, backgroundSize: 'cover', backgroundPosition: 'center'}
        : {};

    return (
        <div
            className={cn(
                "flex min-h-screen flex-col items-center justify-center relative w-full transition-colors duration-300",
                !backgroundImage && layoutVariants[variant]
            )}
            style={backgroundStyle}
        >
            <GlowEffect/>

            {variant === 'default' && !backgroundImage && (
                <DotPattern
                    className="absolute inset-0 opacity-30 dark:opacity-20"
                    width={20}
                    height={20}
                />
            )}

            <main className="relative grid w-full max-w-md flex-1 content-center z-10 px-6 py-8">
                <div className="relative z-20 [--animation-duration:300ms] [&>div]:animate-fade-in">
                    <FallingCard type="login">
                        <div className={cn(
                            "flex flex-col space-y-8 p-8 rounded-xl shadow-xl",
                            cardVariants[variant]
                        )}>
                            <AuthCardHeader
                                title={title}
                                description={description}
                                variant={variant}
                            />
                            <div className="space-y-6">
                                {children}
                            </div>
                        </div>
                    </FallingCard>
                </div>
                <div className="pointer-events-none absolute inset-0" aria-hidden="true">
                    <DecorativeSVG position="left"/>
                    <DecorativeSVG position="right"/>
                </div>
            </main>

            {showFooter && (
                <AuthFooter
                    companyInfo={companyInfo}
                    links={legalLinks}
                    contactInfo={contactInfo}
                    className="mt-auto"
                />
            )}
        </div>
    );
}