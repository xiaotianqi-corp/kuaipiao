import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { SharedData } from '@/types';

interface SharedDataContextType extends SharedData {
    setSharedData: (data: Partial<SharedData>) => void;
}

const SharedDataContext = createContext<SharedDataContextType | undefined>(undefined);

export function SharedDataProvider({ children }: { children: ReactNode }) {
    const [sharedData, setSharedDataState] = useState<SharedData>({
        name: 'Kuaipiao App',
        quote: { message: '', author: '' },
        auth: {
            user: null as any
        },
        sidebarOpen: false
    });

    useEffect(() => {
        // Obtener los datos compartidos desde una API
        const fetchSharedData = async () => {
            try {
                const response = await fetch('/api/shared-data');
                const data = await response.json();
                setSharedDataState(data);
            } catch (error) {
                console.error('Failed to fetch shared data:', error);
            }
        };

        fetchSharedData();
    }, []);

    const setSharedData = (data: Partial<SharedData>) => {
        setSharedDataState(prev => ({ ...prev, ...data }));
    };

    return (
        <SharedDataContext.Provider value={{ ...sharedData, setSharedData }}>
            {children}
        </SharedDataContext.Provider>
    );
}

export function useSharedData() {
    const context = useContext(SharedDataContext);
    if (context === undefined) {
        throw new Error('useSharedData must be used within a SharedDataProvider');
    }
    return context;
}