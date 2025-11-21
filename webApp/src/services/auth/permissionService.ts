import { tokenService } from '@/services/auth/tokenService';

type Permission = string;
type Role = 'admin' | 'manager' | 'user' | 'guest';

interface RolePermissions {
    [role: string]: Permission[];
}

class PermissionService {
    private rolePermissions: RolePermissions = {
        admin: ['*'],
        manager: [
            'users.read',
            'users.create',
            'users.update',
            'products.read',
            'products.create',
            'products.update',
            'invoices.read',
            'invoices.create',
            'invoices.update',
            'reports.read'
        ],
        user: [
            'profile.read',
            'profile.update',
            'products.read',
            'invoices.read',
            'invoices.create'
        ],
        guest: [
            'products.read'
        ]
    };

    /**
     * Obtiene el rol del usuario actual
     */
    getUserRole(): Role | null {
        return tokenService.getUserRole() as Role;
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     */
    hasPermission(permission: Permission): boolean {
        const role = this.getUserRole();
        if (!role) return false;

        const permissions = this.rolePermissions[role];
        if (!permissions) return false;

        // Admin tiene todos los permisos
        if (permissions.includes('*')) return true;

        // Verificar permiso exacto o con wildcard
        return permissions.some(p => {
            if (p === permission) return true;
            if (p.endsWith('.*')) {
                const prefix = p.slice(0, -2);
                return permission.startsWith(prefix);
            }
            return false;
        });
    }

    /**
     * Verifica si el usuario tiene alguno de los permisos
     */
    hasAnyPermission(permissions: Permission[]): boolean {
        return permissions.some(p => this.hasPermission(p));
    }

    /**
     * Verifica si el usuario tiene todos los permisos
     */
    hasAllPermissions(permissions: Permission[]): boolean {
        return permissions.every(p => this.hasPermission(p));
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    hasRole(role: Role): boolean {
        return this.getUserRole() === role;
    }

    /**
     * Verifica si el usuario tiene alguno de los rbac
     */
    hasAnyRole(roles: Role[]): boolean {
        const userRole = this.getUserRole();
        return userRole ? roles.includes(userRole) : false;
    }

    /**
     * Verifica si el usuario es admin
     */
    isAdmin(): boolean {
        return this.hasRole('admin');
    }

    /**
     * Verifica si el usuario puede acceder a un recurso
     */
    canAccess(resource: string, action: 'read' | 'create' | 'update' | 'delete'): boolean {
        return this.hasPermission(`${resource}.${action}`);
    }

    /**
     * Obtiene todos los permisos del usuario
     */
    getUserPermissions(): Permission[] {
        const role = this.getUserRole();
        if (!role) return [];

        return this.rolePermissions[role] || [];
    }

    /**
     * Verifica si el recurso pertenece al usuario
     */
    isOwner(resourceOwnerId: string): boolean {
        const userId = tokenService.getUserId();
        return userId === resourceOwnerId;
    }

    /**
     * Verifica si el usuario puede editar el recurso
     */
    canEdit(resourceOwnerId: string): boolean {
        return this.isAdmin() || this.isOwner(resourceOwnerId);
    }
}

export const permissionService = new PermissionService();
export default permissionService;