import { type BreadcrumbItem } from '@/types';
import AppLayout from '@/layouts/app-layout';
import SettingsLayout from '@/layouts/settings/layout';
import { appearance } from '@/routes';
import HeadingSmall from "@components/common/heading-small";
import AppearanceTabs from "@/components/common/appearance-tabs";

const breadcrumbs: BreadcrumbItem[] = [
    {
        title: 'Appearance settings',
        href: appearance().url,
    },
];

export default function Appearance() {
    return (
        <AppLayout title="Appearance settings" description="Appearance settings" breadcrumbs={breadcrumbs}>
            <SettingsLayout>
                <div className="space-y-6">
                    <HeadingSmall title="Appearance settings" description="Update your account's appearance settings" />
                    <AppearanceTabs />
                </div>
            </SettingsLayout>
        </AppLayout>
    );
}
