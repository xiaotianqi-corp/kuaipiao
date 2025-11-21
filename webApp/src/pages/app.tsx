import { BrowserRouter as Router } from 'react-router-dom';
import AuthProvider from '@/contexts/AuthContext';
import MetaTags from "@/contexts/MetaTags";
import AppRoutes from "@/router";
import {SharedDataProvider} from "@/contexts/SharedDataContext";

export default function App() {
    return (
        <SharedDataProvider>
            <Router>
                <AuthProvider>
                    <MetaTags
                        title="Kuaipiao App"
                        description="Kuaipiao - Multi-enterprise invoicing and product management system"
                        keywords="Invoicing, Products, Multi-, KMP"
                        preconnects={['https://fonts.bunny.net']}
                        stylesheets={['https://fonts.bunny.net/css?family=instrument-sans:400,500,600']}
                        ogTitle="Kuaipiao App"
                        ogDescription="Start your journey with Kuaipiao"
                    />
                    <AppRoutes />
                </AuthProvider>
            </Router>
        </SharedDataProvider>
    );
}