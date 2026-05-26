--liquibase formatted sql

--changeset author:1
--comment: Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset author:2
--comment: Create categories table
CREATE TABLE categories (
                            category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            parent_category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:3
--comment: Create suppliers table
CREATE TABLE suppliers (
                           supplier_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(255) NOT NULL,
                           contact_name VARCHAR(255),
                           phone VARCHAR(50),
                           email VARCHAR(255),
                           address TEXT,
                           inn VARCHAR(12),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:4
--comment: Create products table
CREATE TABLE products (
                          product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (price >= 0),
                          cost_price DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (cost_price >= 0),
                          stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
                          vat_rate INTEGER DEFAULT 22,
                          manufacturer VARCHAR(255),
                          warranty_period INTEGER DEFAULT 0,
                          image_path VARCHAR(500),
                          category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
                          supplier_id UUID REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:5
--comment: Create customers table
CREATE TABLE customers (
                           customer_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           phone VARCHAR(50),
                           address TEXT,
                           image_path VARCHAR(500),
                           discount INTEGER DEFAULT 0,
                           money_spent DECIMAL(15,2) DEFAULT 0 CHECK ( money_spent >= 0 ),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:6
--comment: Create orders table
CREATE TABLE orders (
                        order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        order_number VARCHAR(50) UNIQUE,
                        customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
                        total_price DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK ( total_price >= 0 ),
                        status VARCHAR(50) DEFAULT 'IN_PROGRESS',
                        shipping_address TEXT,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:7
--comment: Create order_items table
CREATE TABLE order_items (
                             order_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                             product_id UUID REFERENCES products(product_id) ON DELETE SET NULL,
                             quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
                             price DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK ( price >= 0 ),
                             cost_price DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK ( cost_price >= 0 ),
                             vat_rate INTEGER,
                             vat_amount DECIMAL(12,2) DEFAULT 0,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:8
--comment: Create indexes for better performance
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_supplier_id ON products(supplier_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_customers_email ON customers(email);

--changeset author:9
--comment: Add check constraints
ALTER TABLE products ADD CONSTRAINT check_price_positive CHECK (price >= 0);
ALTER TABLE products ADD CONSTRAINT check_stock_quantity_non_negative CHECK (stock_quantity >= 0);
ALTER TABLE order_items ADD CONSTRAINT check_quantity_positive CHECK (quantity > 0);
ALTER TABLE order_items ADD CONSTRAINT check_price_positive CHECK (price >= 0);

--changeset author:10
--comment: Create supplies table
CREATE TABLE supplies (
                          supply_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          supplier_id UUID NOT NULL REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
                          supply_number VARCHAR(50) UNIQUE NOT NULL,
                          supply_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(50) DEFAULT 'PENDING',
                          notes TEXT,
                          logistic_cost DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK ( logistic_cost >= 0 ),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:11
--comment: Create supply_items table
CREATE TABLE supply_items (
                              supply_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              supply_id UUID NOT NULL REFERENCES supplies(supply_id) ON DELETE CASCADE,
                              product_id UUID NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                              quantity INTEGER NOT NULL CHECK (quantity > 0),
                              price_per_unit DECIMAL(12,2) NOT NULL CHECK (price_per_unit >= 0),
                              total_price DECIMAL(15,2) GENERATED ALWAYS AS (quantity * price_per_unit) STORED,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:12
--comment: Create indexes for supplies tables
CREATE INDEX idx_supplies_supplier_id ON supplies(supplier_id);
CREATE INDEX idx_supplies_supply_date ON supplies(supply_date);
CREATE INDEX idx_supplies_status ON supplies(status);
CREATE INDEX idx_supply_items_supply_id ON supply_items(supply_id);
CREATE INDEX idx_supply_items_product_id ON supply_items(product_id);

--changeset author:13
--comment: Add check constraint for supply date
ALTER TABLE supplies ADD CONSTRAINT check_supply_date_not_future
    CHECK (supply_date <= CURRENT_TIMESTAMP + interval '1 day');

CREATE TABLE expenses (
                          expense_id UUID PRIMARY KEY,
                          amount DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK ( amount >= 0 ),
                          description VARCHAR(500),
                          type VARCHAR(50) NOT NULL,
                          expense_date DATE NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE analytics_daily (
                                               date DATE NOT NULL PRIMARY KEY,
                                               total_revenue DECIMAL(15,2) NOT NULL DEFAULT 0,
                                               total_cost DECIMAL(15,2) NOT NULL DEFAULT 0,
                                               order_count INT NOT NULL DEFAULT 0,
                                               new_customers INT NOT NULL DEFAULT 0
);

CREATE TABLE users (
                                     user_id UUID PRIMARY KEY,
                                     username VARCHAR(100) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(20) NOT NULL,
                                     full_name VARCHAR(150),
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE settings (
                               id UUID PRIMARY KEY,
                               shop_name VARCHAR(255) NOT NULL,
                               address VARCHAR(500),
                               phone VARCHAR(50),
                               email VARCHAR(255)
);

CREATE TABLE audit_log (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           username VARCHAR(100) NOT NULL,
                           action VARCHAR(50) NOT NULL,          -- CREATE, UPDATE, DELETE, BLOCK, UNBLOCK, etc.
                           entity_type VARCHAR(100) NOT NULL,    -- Product, Order, User, Supply, ...
                           entity_id VARCHAR(255),               -- UUID сущности (может быть null для массовых операций)
                           details TEXT                           -- JSON с дополнительной информацией
);

CREATE INDEX idx_audit_log_timestamp ON audit_log(created_at);
CREATE INDEX idx_audit_log_username ON audit_log(username);
CREATE INDEX idx_audit_log_entity_type ON audit_log(entity_type);
