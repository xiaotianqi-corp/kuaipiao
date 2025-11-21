export type Role = {
  createdAt: string;
  description: string;
  id: string;
  isDefault: boolean;
  name: string;
  updatedAt: string;
};


export type RolesTableProps = {
  roles: Role[] | undefined;
};

export type SearchProps = {
  onSearch: (searchTerm: string) => void;
};

export type RequestErrorProps = {
  requestError: boolean;
};
