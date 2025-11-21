import React from 'react';
import ReactDOM from 'react-dom/client';
import App from '@/pages/app';
import './styles/globals.css';
import {AppProviders} from "@components/common/app-providers";

const root = document.getElementById('root');
if (!root) throw new Error('Root element not found');

ReactDOM.createRoot(root).render(
    <React.StrictMode>
        <AppProviders>
            <App />
        </AppProviders>
    </React.StrictMode>
);
