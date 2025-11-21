import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { useInitials } from '@/hooks/use-initials';
import { cn } from '@/lib/utils';
import { ROUTES } from '@/routes';
import { type BreadcrumbItem, type NavItem } from '@/types';
import { Link, useLocation } from 'react-router-dom';
import { BookOpen, Folder, LayoutGrid, Menu, Search } from 'lucide-react';
import AppLogo from './app-logo';
import AppLogoIcon from './app-logo-icon';
import { NavigationMenuItem, NavigationMenuList, navigationMenuTriggerStyle } from "@components/ui/navigation-menu.tsx";
import { NavMain } from "./nav-main";
import { useAuth } from "@/contexts/AuthContext";
import {Icon} from "@components/common/icon.tsx";
import {UserMenuContent} from "@components/common/user-menu-content.tsx";
import {Breadcrumbs} from "@components/common/breadcrumbs.tsx";

const mainNavItems: NavItem[] = [
    {
        title: 'Dashboard',
        href: ROUTES.dashboard,
        icon: LayoutGrid,
    },
];

const rightNavItems: NavItem[] = [
    {
        title: 'Repository',
        href: 'https://github.com/yourusername/kuaipiao',
        icon: Folder,
    },
    {
        title: 'Documentation',
        href: '/docs',
        icon: BookOpen,
    },
];

const activeItemStyles = 'text-neutral-900 dark:bg-neutral-800 dark:text-neutral-100';

interface AppHeaderProps {
    breadcrumbs?: BreadcrumbItem[];
}

export function AppHeader({ breadcrumbs = [] }: AppHeaderProps) {
    const location = useLocation();
    const { user } = useAuth();
    const getInitials = useInitials();

    return (
        <>
            <header className="sticky top-0 z-40 border-b bg-background">
                <div className="container flex h-16 items-center justify-between space-x-4 sm:space-x-0">
                    <div className="flex gap-6 md:gap-8">
                        <Link to={ROUTES.home} className="items-center space-x-2 md:flex">
                            <span className="font-bold">Kuaipiao</span>
                        </Link>
                        <nav className="hidden gap-6 md:flex">
                            <NavMain />
                        </nav>
                    </div>

                    <div className="flex items-center space-x-3">
                        <div className="hidden md:block">
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                                        <Avatar className="h-8 w-8">
                                            <AvatarImage src={user?.avatar} alt={user?.name} />
                                            <AvatarFallback>{user?.name ? getInitials(user.name) : 'U'}</AvatarFallback>
                                        </Avatar>
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end" forceMount>
                                    <div className="flex flex-col space-y-1 p-2">
                                        {user?.name && <p className="text-sm font-medium leading-none">{user.name}</p>}
                                        {user?.email && <p className="text-xs leading-none text-muted-foreground">{user.email}</p>}
                                    </div>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </div>

                        {/* Mobile Menu */}
                        <div className="md:hidden">
                            <Sheet>
                                <SheetTrigger asChild>
                                    <Button variant="ghost" size="icon" className="mr-2 h-[34px] w-[34px]">
                                        <Menu className="h-5 w-5" />
                                    </Button>
                                </SheetTrigger>
                                <SheetContent side="left" className="flex h-full w-64 flex-col items-stretch justify-between bg-sidebar">
                                    <SheetTitle className="sr-only">Navigation Menu</SheetTitle>
                                    <SheetHeader className="flex justify-start text-left">
                                        <AppLogoIcon className="h-6 w-6 fill-current text-black dark:text-white" />
                                    </SheetHeader>
                                    <div className="flex h-full flex-1 flex-col space-y-4 p-4">
                                        <div className="flex h-full flex-col justify-between text-sm">
                                            <div className="flex flex-col space-y-4">
                                                {mainNavItems.map((item) => (
                                                    <Link
                                                        key={item.title}
                                                        to={item.href}
                                                        className="flex items-center space-x-2 font-medium"
                                                    >
                                                        {item.icon && <Icon iconNode={item.icon} className="h-5 w-5" />}
                                                        <span>{item.title}</span>
                                                    </Link>
                                                ))}
                                            </div>

                                            <div className="flex flex-col space-y-4">
                                                {rightNavItems.map((item) => (
                                                    <a
                                                        key={item.title}
                                                        href={item.href}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="flex items-center space-x-2 font-medium"
                                                    >
                                                        {item.icon && <Icon iconNode={item.icon} className="h-5 w-5" />}
                                                        <span>{item.title}</span>
                                                    </a>
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                </SheetContent>
                            </Sheet>
                        </div>

                        <Link to={ROUTES.dashboard} className="flex items-center space-x-2">
                            <AppLogo />
                        </Link>

                        {/* Desktop Navigation */}
                        <div className="ml-6 hidden h-full items-center space-x-6 lg:flex">
                            <NavigationMenuList className="flex h-full items-stretch">
                                <NavigationMenuList className="flex h-full items-stretch space-x-2">
                                    {mainNavItems.map((item, index) => (
                                        <NavigationMenuItem key={index} className="relative flex h-full items-center">
                                            <Link
                                                to={item.href}
                                                className={cn(
                                                    navigationMenuTriggerStyle(),
                                                    location.pathname === item.href && activeItemStyles,
                                                    'h-9 cursor-pointer px-3',
                                                )}
                                            >
                                                {item.icon && <Icon iconNode={item.icon} className="mr-2 h-4 w-4" />}
                                                {item.title}
                                            </Link>
                                            {location.pathname === item.href && (
                                                <div className="absolute bottom-0 left-0 h-0.5 w-full translate-y-px bg-black dark:bg-white"></div>
                                            )}
                                        </NavigationMenuItem>
                                    ))}
                                </NavigationMenuList>
                            </NavigationMenuList>
                        </div>

                        <div className="ml-auto flex items-center space-x-2">
                            <div className="relative flex items-center space-x-1">
                                <Button variant="ghost" size="icon" className="group h-9 w-9 cursor-pointer">
                                    <Search className="!size-5 opacity-80 group-hover:opacity-100" />
                                </Button>
                                <div className="hidden lg:flex">
                                    {rightNavItems.map((item) => (
                                        <TooltipProvider key={item.title} delayDuration={0}>
                                            <Tooltip>
                                                <TooltipTrigger>
                                                    <a
                                                        href={item.href}
                                                        target={item.href.startsWith('http') ? "_blank" : undefined}
                                                        rel={item.href.startsWith('http') ? "noopener noreferrer" : undefined}
                                                        className="group ml-1 inline-flex h-9 w-9 items-center justify-center rounded-md bg-transparent p-0 text-sm font-medium text-accent-foreground ring-offset-background transition-colors hover:bg-accent hover:text-accent-foreground focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50"
                                                    >
                                                        <span className="sr-only">{item.title}</span>
                                                        {item.icon && <Icon iconNode={item.icon} className="size-5 opacity-80 group-hover:opacity-100" />}
                                                    </a>
                                                </TooltipTrigger>
                                                <TooltipContent>
                                                    <p>{item.title}</p>
                                                </TooltipContent>
                                            </Tooltip>
                                        </TooltipProvider>
                                    ))}
                                </div>
                            </div>
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant="ghost" className="size-10 rounded-full p-1">
                                        <Avatar className="size-8 overflow-hidden rounded-full">
                                            <AvatarImage src={user?.avatar} alt={user?.name} />
                                            <AvatarFallback className="rounded-lg bg-neutral-200 text-black dark:bg-neutral-700 dark:text-white">
                                                {user?.name ? getInitials(user.name) : 'U'}
                                            </AvatarFallback>
                                        </Avatar>
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent className="w-56" align="end">
                                    <UserMenuContent/>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </div>
                    </div>
                </div>
            </header>
            {breadcrumbs.length > 1 && (
                <div className="flex w-full border-b border-sidebar-border/70">
                    <div className="mx-auto flex h-12 w-full items-center justify-start px-4 text-neutral-500 md:max-w-7xl">
                        <Breadcrumbs breadcrumbs={breadcrumbs} />
                    </div>
                </div>
            )}
        </>
    );
}