import { User } from '@/types/user';
import axios from 'axios';

const USERS_API_URL = 'http://localhost:3002/users';
const ROLES_API_URL = 'http://localhost:3002/roles';

const fetchRoleName = async (roleId: string) => {
  try {
    const response = await axios.get(`${ROLES_API_URL}/${roleId}`);
    return response.data.name;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      throw error;
    }
  }
};

export const fetchUsersWithRoles = async () => {
  const usersWithRoles: User[] = [];
  let hasError = false;

  try {
    const usersResponse = await axios.get(USERS_API_URL);
    const { data: users } = usersResponse.data;

    const usersWithRolesPromises = users.map(async (user: User) => {
      try {
        const roleName = await fetchRoleName(user.roleId);
        usersWithRoles.push({ ...user, roleName });
      } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 500) {
          console.error('Error fetching role for user:', user, error);
        } else {
          console.error('Unexpected error:', error);
        }
        hasError = true;
        usersWithRoles.push({
          ...user,
          roleName: null,
        });
      }
    });

    await Promise.all(usersWithRolesPromises);

    return { usersWithRoles, hasError };
  } catch (error) {
    if (axios.isAxiosError(error)) {
      return { usersWithRoles: [], hasError: true };
    }
  }
};

export const fetchRoles = async () => {
  try {
    const response = await axios.get(ROLES_API_URL);

    return { roles: response.data };
  } catch (error) {
    if (axios.isAxiosError(error)) {
      throw error;
    }
  }
};
