import { AppShell } from '@/components/common/app-shell';
import { AppSidebar } from '@/components/common/app-sidebar';
import { AppSidebarHeader } from '@/components/common/app-sidebar-header';
import {AppContent} from "@components/common/app-content";
import { type BreadcrumbItem } from '@/types';
import { type PropsWithChildren } from 'react';

export default function AppSidebarLayout({ children, breadcrumbs = [] }: PropsWithChildren<{ breadcrumbs?: BreadcrumbItem[] }>) {
    return (
        <AppShell variant="sidebar">
            <AppSidebar />
            <AppContent variant="sidebar" className="overflow-x-hidden">
                <AppSidebarHeader breadcrumbs={breadcrumbs} />
                {children}
            </AppContent>
        </AppShell>
    );
}
