import { useSharedData } from '@/contexts/SharedDataContext';

export function usePage<T = any>() {
    const sharedData = useSharedData();

    return {
        props: sharedData as T & SharedData
    };
}