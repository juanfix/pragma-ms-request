 -- Tabla status
CREATE TABLE IF NOT EXISTS status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Tabla loan_type
CREATE TABLE IF NOT EXISTS loan_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    min_amount BIGINT NULL,
    max_amount BIGINT NULL,
    interest_rate DECIMAL NOT NULL,
    automatic_validation BOOLEAN DEFAULT TRUE
);

-- Tabla requests
CREATE TABLE IF NOT EXISTS requests (
    id SERIAL PRIMARY KEY,
    amount BIGINT NOT NULL,
    term SMALLINT NOT NULL,
    email VARCHAR(255) NULL,
    identity_number VARCHAR(255) NULL,
    status_id INTEGER NOT NULL,
    loan_type_id INTEGER NOT NULL,
    CONSTRAINT fk_requests_status FOREIGN KEY (status_id) REFERENCES status(id),
    CONSTRAINT fk_requests_loan_type FOREIGN KEY (loan_type_id) REFERENCES loan_type(id)
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_requests_email ON requests(email);
CREATE INDEX IF NOT EXISTS idx_requests_identity_number ON requests(identity_number);