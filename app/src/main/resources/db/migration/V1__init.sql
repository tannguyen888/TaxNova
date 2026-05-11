-- Users table
CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER'
);

-- Receipts table (for revenue tracking)
CREATE TABLE IF NOT EXISTS receipts (
    id          BIGSERIAL PRIMARY KEY,
    date        DATE             NOT NULL,
    amount      DOUBLE PRECISION NOT NULL,
    tax_amount  DOUBLE PRECISION NOT NULL,
    category    VARCHAR(100)     NOT NULL
);

-- Products table (for inventory management)
CREATE TABLE IF NOT EXISTS products (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    price         DOUBLE PRECISION NOT NULL,
    quantity      BIGINT NOT NULL DEFAULT 0,
    category      VARCHAR(100),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Carts table (for shopping cart)
CREATE TABLE IF NOT EXISTS carts (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(50) DEFAULT 'ACTIVE'
);

-- Cart items table
CREATE TABLE IF NOT EXISTS cart_items (
    id            BIGSERIAL PRIMARY KEY,
    cart_id       BIGINT NOT NULL REFERENCES carts(id),
    product_id    BIGINT NOT NULL REFERENCES products(id),
    product_code  VARCHAR(50),
    product_name  VARCHAR(255),
    unit_price    DOUBLE PRECISION NOT NULL,
    quantity      BIGINT NOT NULL
);

-- Invoices table (for invoice generation)
CREATE TABLE IF NOT EXISTS invoices (
    id            BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE,
    user_id       BIGINT NOT NULL REFERENCES users(id),
    cart_id       BIGINT REFERENCES carts(id),
    total_amount  DOUBLE PRECISION NOT NULL,
    tax_amount    DOUBLE PRECISION DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(50) DEFAULT 'COMPLETED'
);

-- Invoice details table
CREATE TABLE IF NOT EXISTS invoice_details (
    id            BIGSERIAL PRIMARY KEY,
    invoice_id    BIGINT NOT NULL REFERENCES invoices(id),
    product_code  VARCHAR(50),
    product_name  VARCHAR(255),
    quantity      BIGINT NOT NULL,
    unit_price    DOUBLE PRECISION NOT NULL,
    total_price   DOUBLE PRECISION NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_products_code ON products(code);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_carts_user_id ON carts(user_id);
CREATE INDEX IF NOT EXISTS idx_carts_status ON carts(status);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_invoices_user_id ON invoices(user_id);
CREATE INDEX IF NOT EXISTS idx_invoices_created_at ON invoices(created_at);

-- Insert default users
-- Password 'admin123' hashed with SHA-256 + Base64 = JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=
INSERT INTO users (username, password_hash, role)
VALUES ('admin', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Insert sample products
INSERT INTO products (code, name, description, price, quantity, category, created_at, updated_at)
VALUES 
    ('P001', 'Sản phẩm A', 'Mô tả sản phẩm A', 100000.00, 50, 'Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('P002', 'Sản phẩm B', 'Mô tả sản phẩm B', 200000.00, 30, 'Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('P003', 'Sản phẩm C', 'Mô tả sản phẩm C', 150000.00, 20, 'Accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;