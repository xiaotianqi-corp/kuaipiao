import TextLink from "@/components/common/text-link";
import { Separator } from "@/components/ui/separator";
import { motion } from "framer-motion";
import { Lock } from "lucide-react";
import React from "react";
import { Link } from "react-router-dom";

interface FooterLink {
    text: string;
    linkText: string;
    href: string;
    tabIndex?: number;
}

interface SecurityProvider {
    text: string;
    description: string;
    url?: string;
    logo?: React.ReactNode;
    showLogo?: boolean;
}

interface FallingCardProps {
    children: React.ReactNode;
    type?: 'login' | 'register' | 'reset' | 'verify' | 'custom' | 'onboarding';
    footerLink?: FooterLink;
    securityProvider?: SecurityProvider;
    showFooter?: boolean;
    showSecurity?: boolean;
    className?: string;
    variant?: 'default' | 'minimal' | 'branded';
}

const getFooterConfig = (type: string): { footerLink: FooterLink; securityProvider: SecurityProvider } => {
    const configs = {
        login: {
            footerLink: {
                text: "Don't have an account?",
                linkText: "Sign up",
                href: "/sign-up",
                tabIndex: 5
            },
            securityProvider: {
                text: "Secured by",
                description: "Xiaotianqi AUTH",
                url: "https://xiaotianqi.com",
                showLogo: true
            }
        },
        register: {
            footerLink: {
                text: "Already have an account?",
                linkText: "Sign in",
                href: "/sign-in", // Cambiado de route('sign-in') a ruta directa
                tabIndex: 5
            },
            securityProvider: {
                text: "Secured by",
                description: "Xiaotianqi AUTH",
                url: "https://xiaotianqi.com",
                showLogo: true
            }
        },
        reset: {
            footerLink: {
                text: "¿Recordaste tu contraseña?",
                linkText: "Volver al inicio",
                href: "/sign-in",
                tabIndex: 5
            },
            securityProvider: {
                text: "Secured by",
                description: "Xiaotianqi AUTH",
                url: "https://xiaotianqi.com",
                showLogo: true
            }
        },
        verify: {
            footerLink: {
                text: "Wrong account or typo?",
                linkText: "Sign out",
                href: "/sign-out",
                tabIndex: 5
            },
            securityProvider: {
                text: "Secured by",
                description: "Xiaotianqi AUTH",
                url: "https://xiaotianqi.com",
                showLogo: true
            }
        },
        onboarding: {
            footerLink: {
                text: "Wrong account or typo?",
                linkText: "Sign out",
                href: "/logout",
                tabIndex: 5
            },
            securityProvider: {
                text: "Secured by",
                description: "Xiaotianqi AUTH",
                url: "https://xiaotianqi.com",
                showLogo: true
            }
        }
    };

    return configs[type as keyof typeof configs] || configs.login;
};

const CardFooter: React.FC<{
    footerLink?: FooterLink;
    securityProvider?: SecurityProvider;
    showSecurity?: boolean;
    variant?: string;
}> = ({ footerLink, securityProvider, showSecurity = true, variant = 'default' }) => {
    if (!footerLink && (!showSecurity || !securityProvider)) {
        return null;
    }

    const footerVariants = {
        default: "bg-neutral-100 dark:bg-neutral-800",
        minimal: "bg-gray-50 dark:bg-gray-900",
        branded: "bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20"
    };

    return (
        <>
            {/* Footer Link Section */}
            {footerLink && (
                <>
                    <Separator className="opacity-50" />
                    <div
                        className={`
                            flex flex-col justify-center items-center py-4 px-6 text-sm
                            ${footerVariants[variant as keyof typeof footerVariants] || footerVariants.default}
                            ${!showSecurity ? "rounded-b-xl" : ""}
                        `}
                    >
                        <div className="flex items-center gap-2 text-center text-sm text-muted-foreground">
                            <span>{footerLink.text}</span>
                            <TextLink
                                to={footerLink.href}
                                tabIndex={footerLink.tabIndex}
                                underlineOnHover
                                className="text-sm font-semibold text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded px-1"
                            >
                                {footerLink.linkText}
                            </TextLink>
                        </div>
                    </div>
                </>
            )}

            {showSecurity && securityProvider && (
                <>
                    {footerLink && <Separator className="opacity-50" />}
                    <div className={`flex flex-col justify-center items-center py-4 px-6 text-sm rounded-b-xl ${footerVariants[variant as keyof typeof footerVariants] || footerVariants.default}`}>
                        <div className="flex items-center gap-3">
                            <div className="flex items-center gap-2">
                                <span className="text-gray-600 dark:text-gray-400 font-medium text-sm">
                                    {securityProvider.text}
                                </span>
                            </div>

                            {securityProvider.url ? (
                                <Link
                                    to={securityProvider.url}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="group flex items-center gap-2 text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded px-1"
                                    aria-label={`Secured by ${securityProvider.description}`}
                                >
                                    {securityProvider.showLogo && (
                                        <div className="w-4 h-4 bg-blue-600 dark:bg-blue-500 rounded flex items-center justify-center">
                                            <Lock className="w-2.5 h-2.5 text-white" />
                                        </div>
                                    )}
                                    <span className="font-semibold text-sm group-hover:underline">
                                        {securityProvider.description}
                                    </span>
                                </Link>
                            ) : (
                                <div className="flex items-center gap-2">
                                    {securityProvider.showLogo && (
                                        <div className="w-4 h-4 bg-blue-600 dark:bg-blue-500 rounded flex items-center justify-center">
                                            <Lock className="w-2.5 h-2.5 text-white" />
                                        </div>
                                    )}
                                    <span className="font-semibold text-sm text-gray-700 dark:text-gray-300">
                                        {securityProvider.description}
                                    </span>
                                </div>
                            )}
                        </div>
                    </div>
                </>
            )}
        </>
    );
};

export default function FallingCard({
                                        children,
                                        type = 'login',
                                        footerLink,
                                        securityProvider,
                                        showFooter = true,
                                        showSecurity = true,
                                        className = "",
                                        variant = 'default'
                                    }: FallingCardProps) {

    const defaultConfig = getFooterConfig(type);
    const finalFooterLink = footerLink || defaultConfig.footerLink;
    const finalSecurityProvider = securityProvider || defaultConfig.securityProvider;

    const cardVariants = {
        default: "border border-neutral-200 bg-neutral-100 dark:border-neutral-800 dark:bg-neutral-900/50",
        minimal: "border-gray-200 bg-white dark:border-gray-700 dark:bg-gray-900",
        branded: "border-blue-200/50 bg-gradient-to-br from-white to-blue-50/30 dark:border-blue-800/50 dark:from-gray-900 dark:to-blue-900/20"
    };

    const getAnimationConfig = () => {
        const configs = {
            login: { rotate: -8, duration: 0.8 },
            register: { rotate: 8, duration: 0.9 },
            reset: { rotate: -5, duration: 0.7 },
            verify: { rotate: 5, duration: 0.7 },
            custom: { rotate: -8, duration: 0.8 }
        };
        return configs[type as keyof typeof configs] || configs.login;
    };

    const animationConfig = getAnimationConfig();

    return (
        <motion.div
            initial={{
                rotate: animationConfig.rotate,
                opacity: 0,
                y: 20
            }}
            animate={{
                rotate: 0,
                opacity: 1,
                y: 0
            }}
            transition={{
                type: "spring",
                stiffness: 200,
                damping: 15,
                mass: 2,
                duration: animationConfig.duration,
                opacity: { duration: 0.6 },
                y: { duration: 0.6 }
            }}
            style={{
                transformOrigin: "top center"
            }}
            className={`
                relative z-20 mt-10 w-full overflow-visible rounded-xl 
                shadow-2xl shadow-neutral-800/10 dark:shadow-black/20
                backdrop-blur-sm md:mt-12
                ${type !== "onboarding" ? "sm:max-w-md" : "max-w-full"}
                ${cardVariants[variant as keyof typeof cardVariants] || cardVariants.default}
                ${className}
            `}
        >
            <div className="rounded-t-xl">
                {children}
            </div>

            {showFooter && (
                <CardFooter
                    footerLink={finalFooterLink}
                    securityProvider={finalSecurityProvider}
                    showSecurity={showSecurity}
                    variant={variant}
                />
            )}

            <div className="absolute inset-0 rounded-xl bg-gradient-to-t from-transparent via-transparent to-white/10 pointer-events-none" />
        </motion.div>
    );
}