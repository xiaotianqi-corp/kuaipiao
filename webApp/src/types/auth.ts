export interface AuthLayoutProps {
    name?: string;
    title?: string;
    description?: string;
    backgroundImage?: string;
    showFooter?: boolean;
    variant?: 'default' | 'minimal' | 'branded';
}

export interface AuthCardHeaderProps {
    title?: string;
    description?: string;
    variant?: 'default' | 'minimal' | 'branded';
}

export interface FooterLink {
    text: string;
    href: string;
    external?: boolean;
}

export interface ContactInfo {
    email: string;
    salesUrl: string;
    salesText: string;
}

export interface CompanyInfo {
    name: string;
    year: number;
}

export interface AuthLayoutFooterProps {
    links: FooterLink[];
    contactInfo: ContactInfo;
    companyInfo: CompanyInfo;
}