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
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:4
--comment: Create products table
CREATE TABLE products (
                          product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL DEFAULT 0,
                          stock_quantity INTEGER NOT NULL DEFAULT 0,
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
                           money_spent DECIMAL(10,2) DEFAULT 0,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:6
--comment: Create orders table
CREATE TABLE orders (
                        order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
                        total_price DECIMAL(10,2) NOT NULL DEFAULT 0,
                        status VARCHAR(50) DEFAULT 'IN_PROGRESS',
                        shipping_address TEXT,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset author:7
--comment: Create order_items table
CREATE TABLE order_items (
                             order_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                             product_id UUID REFERENCES products(product_id) ON DELETE SET NULL,
                             quantity INTEGER NOT NULL DEFAULT 0,
                             price DECIMAL(10,2) NOT NULL DEFAULT 0,
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