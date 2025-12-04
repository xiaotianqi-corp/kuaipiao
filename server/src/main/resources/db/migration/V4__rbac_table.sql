CREATE TABLE IF NOT EXISTS roles (
    id uuid PRIMARY KEY,
    "name" VARCHAR(100) NOT NULL,
    description TEXT NULL
);

ALTER TABLE roles ADD CONSTRAINT roles_name_unique UNIQUE ("name");

CREATE TABLE IF NOT EXISTS permissions (
    id uuid PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    description TEXT NULL
);

ALTER TABLE permissions ADD CONSTRAINT permissions_code_unique UNIQUE (code);

ALTER TABLE permissions ADD CONSTRAINT permissions_name_unique UNIQUE ("name");

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id uuid,
    permission_id uuid,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id,
    permission_id),
    CONSTRAINT fk_role_permissions_role_id__id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_role_permissions_permission_id__id FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id uuid,
    role_id uuid,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id,
    role_id),
    CONSTRAINT fk_user_roles_user_id__id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_user_roles_role_id__id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE RESTRICT
);
