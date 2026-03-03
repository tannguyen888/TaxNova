CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS receipts (
    id          BIGSERIAL PRIMARY KEY,
    date        DATE             NOT NULL,
    amount      DOUBLE PRECISION NOT NULL,
    tax_amount  DOUBLE PRECISION NOT NULL,
    category    VARCHAR(100)     NOT NULL
);

INSERT INTO users (username, password_hash, role)
VALUES ('admin', 'admin123', 'ADMIN')
ON CONFLICT (username) DO NOTHING;