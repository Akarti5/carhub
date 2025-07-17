-- Create database (if not already created)
-- CREATE DATABASE carhub_db;

-- Connect to the database
\c carhub_db;

-- Create tables (Spring Boot will handle this with JPA, but here's the manual schema)

-- Admins table
CREATE TABLE IF NOT EXISTS admins (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Cars table
CREATE TABLE IF NOT EXISTS cars (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    color VARCHAR(100) NOT NULL,
    mileage INTEGER NOT NULL,
    fuel_type VARCHAR(20),
    transmission VARCHAR(20),
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    description TEXT,
    vin VARCHAR(100),
    doors INTEGER DEFAULT 4,
    seats INTEGER DEFAULT 5,
    engine_size VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    zip_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales table
CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL REFERENCES cars(id),
    client_id BIGINT NOT NULL REFERENCES clients(id),
    admin_id BIGINT NOT NULL REFERENCES admins(id),
    sale_price DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20),
    invoice_number VARCHAR(100) UNIQUE,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'COMPLETED'
);

-- Car Images table
CREATE TABLE IF NOT EXISTS car_images (
    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL REFERENCES cars(id),
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT false
);

-- Note: Default admin user will be created automatically by AdminInitService
-- Username: admin, Password: admin123

-- Insert sample cars
INSERT INTO cars (brand, model, year, price, color, mileage, fuel_type, transmission, description) VALUES
('Toyota', 'Camry', 2022, 28500.00, 'Silver', 15000, 'GASOLINE', 'AUTOMATIC', 'Excellent condition, well maintained'),
('Honda', 'Civic', 2021, 24000.00, 'Blue', 22000, 'GASOLINE', 'MANUAL', 'Sporty and fuel efficient'),
('Ford', 'Mustang', 2023, 45000.00, 'Red', 5000, 'GASOLINE', 'AUTOMATIC', 'Performance vehicle, like new'),
('BMW', '3 Series', 2022, 42000.00, 'Black', 18000, 'GASOLINE', 'AUTOMATIC', 'Luxury sedan with premium features'),
('Mercedes-Benz', 'C-Class', 2021, 38000.00, 'White', 25000, 'GASOLINE', 'AUTOMATIC', 'Elegant and comfortable'),
('Audi', 'A4', 2022, 40000.00, 'Gray', 12000, 'GASOLINE', 'AUTOMATIC', 'German engineering at its finest'),
('Tesla', 'Model 3', 2023, 48000.00, 'Blue', 8000, 'ELECTRIC', 'AUTOMATIC', 'Electric vehicle with autopilot'),
('Volkswagen', 'Golf', 2021, 22000.00, 'Green', 30000, 'GASOLINE', 'MANUAL', 'Compact and reliable'),
('Nissan', 'Altima', 2022, 26000.00, 'Silver', 20000, 'GASOLINE', 'AUTOMATIC', 'Comfortable family sedan'),
('Hyundai', 'Elantra', 2023, 23500.00, 'White', 10000, 'GASOLINE', 'AUTOMATIC', 'Modern design with great warranty')
ON CONFLICT DO NOTHING;

-- Insert sample clients
INSERT INTO clients (first_name, last_name, email, phone, address, city, zip_code) VALUES
('John', 'Doe', 'john.doe@email.com', '+1-555-0101', '123 Main St', 'New York', '10001'),
('Jane', 'Smith', 'jane.smith@email.com', '+1-555-0102', '456 Oak Ave', 'Los Angeles', '90001'),
('Mike', 'Johnson', 'mike.johnson@email.com', '+1-555-0103', '789 Pine Rd', 'Chicago', '60601'),
('Sarah', 'Williams', 'sarah.williams@email.com', '+1-555-0104', '321 Elm St', 'Houston', '77001'),
('David', 'Brown', 'david.brown@email.com', '+1-555-0105', '654 Maple Dr', 'Phoenix', '85001')
ON CONFLICT (email) DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_cars_status ON cars(status);
CREATE INDEX IF NOT EXISTS idx_cars_brand ON cars(brand);
CREATE INDEX IF NOT EXISTS idx_cars_year ON cars(year);
CREATE INDEX IF NOT EXISTS idx_cars_price ON cars(price);
CREATE INDEX IF NOT EXISTS idx_sales_date ON sales(sale_date);
CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);
