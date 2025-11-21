import { ReactNode } from 'react';
import AppLayoutTemplate from '@/layouts/app/app-sidebar-layout';
import { Meta } from '@/components/common/meta';

export interface BreadcrumbItem {
    title: string;
    href: string;
}

interface AppLayoutProps {
    children: ReactNode;
    breadcrumbs?: BreadcrumbItem[];
    title?: string;
    description?: string;
}

export default function AppLayout({
                                      children,
                                      breadcrumbs,
                                      title,
                                      description,
                                      ...props
                                  }: AppLayoutProps) {
    return (
        <>
            <Meta title={title} description={description} />
            <AppLayoutTemplate breadcrumbs={breadcrumbs} {...props}>
                {children}
            </AppLayoutTemplate>
        </>
    );
}
