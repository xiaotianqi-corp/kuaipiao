CREATE TABLE IF NOT EXISTS enterprises (
    id uuid PRIMARY KEY,
    subdomain VARCHAR(63) NOT NULL,
    "domain" VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL,
    plan VARCHAR(20) NOT NULL,
    settings TEXT NULL,
    metadata TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL
);

ALTER TABLE enterprises ADD CONSTRAINT enterprises_subdomain_unique UNIQUE (subdomain);

CREATE TABLE IF NOT EXISTS organizations (
    id uuid PRIMARY KEY,
    enterprise_id uuid NOT NULL,
    "name" VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    metadata TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_organizations_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

ALTER TABLE organizations ADD CONSTRAINT organizations_code_unique UNIQUE (code);

CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enterprise_id uuid NOT NULL,
    organization_id uuid NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    CONSTRAINT fk_users_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_users_organization_id__id FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);

CREATE TABLE IF NOT EXISTS passwordreset (
    id SERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    id_user uuid NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_passwordreset_id_user__id FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE passwordreset ADD CONSTRAINT passwordreset_token_unique UNIQUE (token);

CREATE INDEX passwordreset_id_user ON passwordreset (id_user);

CREATE TABLE IF NOT EXISTS emailverification (
    id SERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    id_user uuid NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_emailverification_id_user__id FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE emailverification ADD CONSTRAINT emailverification_token_unique UNIQUE (token);

CREATE INDEX emailverification_id_user ON emailverification (id_user);
