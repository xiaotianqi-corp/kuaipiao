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

CREATE TABLE IF NOT EXISTS enterprise_migrations (
    id uuid PRIMARY KEY,
    enterprise_id uuid NOT NULL,
    "version" VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    script TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    applied_at TIMESTAMP NOT NULL,
    execution_time BIGINT NOT NULL,
    error_message TEXT NULL,
    checksum VARCHAR(64) NULL,
    CONSTRAINT fk_enterprise_migrations_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS enterprise_backups (
    id uuid PRIMARY KEY,
    enterprise_id uuid NOT NULL,
    description TEXT NULL,
    backup_path VARCHAR(255) NOT NULL,
    "size" BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    include_data BOOLEAN DEFAULT TRUE NOT NULL,
    include_schema BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NULL,
    error TEXT NULL,
    CONSTRAINT fk_enterprise_backups_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS enterprise_audit_logs (
    id uuid PRIMARY KEY,
    enterprise_id uuid NOT NULL,
    "action" VARCHAR(50) NOT NULL,
    actor_id VARCHAR(255) NOT NULL,
    actor_type VARCHAR(50) NOT NULL,
    details TEXT NOT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_enterprise_audit_logs_enterprise_id__id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
