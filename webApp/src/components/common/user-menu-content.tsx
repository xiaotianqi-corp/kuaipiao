import { DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator } from '@/components/ui/dropdown-menu';
import { useMobileNavigation } from '@/hooks/use-mobile-navigation';
import { edit } from '@/routes/profile';
import { useNavigate } from 'react-router-dom';
import { LogOut, Settings } from 'lucide-react';
import { UserInfo } from "@components/common/user-info";
import { useAuth } from "@/contexts/AuthContext";

export function UserMenuContent() {
    const cleanup = useMobileNavigation();
    const { user, logout: authLogout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        cleanup();
        try {
            await authLogout();
            navigate('/login');
        } catch (error) {
            console.error('Logout failed:', error);
            navigate('/login');
        }
    };

    const handleSettings = () => {
        cleanup();
        navigate(edit().url);
    };

    if (!user) {
        return null;
    }

    return (
        <>
            <DropdownMenuLabel className="p-0 font-normal">
                <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                    <UserInfo user={user} showEmail={true} />
                </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
                <DropdownMenuItem onClick={handleSettings} className="cursor-pointer">
                    <Settings className="mr-2 h-4 w-4" />
                    <span>Settings</span>
                </DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleLogout} className="cursor-pointer">
                <LogOut className="mr-2 h-4 w-4" />
                <span>Log out</span>
            </DropdownMenuItem>
        </>
    );
}