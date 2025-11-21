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
