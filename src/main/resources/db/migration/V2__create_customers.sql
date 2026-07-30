CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone_number VARCHAR(20),
    company_name VARCHAR(120),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_customers_email UNIQUE (email)
);

CREATE INDEX idx_customers_company_name
    ON customers (company_name);

CREATE INDEX idx_customers_is_active
    ON customers (is_active);