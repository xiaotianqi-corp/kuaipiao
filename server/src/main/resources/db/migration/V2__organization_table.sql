CREATE TABLE IF NOT EXISTS organizations (
    id uuid PRIMARY KEY,
    "name" VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    address VARCHAR(250) NOT NULL,
    phone VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    country VARCHAR(150) NOT NULL,
    city VARCHAR(150) NOT NULL,
    status VARCHAR(50) NOT NULL,
    metadata TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL
);
