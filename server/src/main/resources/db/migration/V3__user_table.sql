CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    username VARCHAR(100) NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enterprise_id uuid NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    CONSTRAINT fk_users_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT
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

CREATE TABLE IF NOT EXISTS user_organizations (
    user_id uuid,
    organization_id uuid,
    CONSTRAINT pk_user_organizations PRIMARY KEY (user_id,
    organization_id),
    CONSTRAINT fk_user_organizations_user_id__id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_user_organizations_organization_id__id FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE ON UPDATE RESTRICT
);
