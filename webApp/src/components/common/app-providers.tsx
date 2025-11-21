import React from 'react';
import { ThemeProvider } from './theme-provider';

interface AppProvidersProps {
    children: React.ReactNode;
    title?: string;
    description?: string;
    favicon?: string;
}

export const AppProviders: React.FC<AppProvidersProps> = ({
                                                              children,
                                                              title = 'Mi Aplicación',
                                                              description = 'Aplicación web moderna con React 19',
                                                              favicon = '/favicon.ico',
                                                          }) => {
    return (
        <ThemeProvider defaultTheme="system" storageKey="app-theme">
            <title>{title}</title>
            <meta name="description" content={description} />
            <link rel="icon" href={favicon} />
            {children}
        </ThemeProvider>
    );
};
