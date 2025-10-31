-- ============================================
-- Auction404 Database Schema
-- PostgreSQL Database Setup Script
-- ============================================

-- Drop table if exists (for clean reinstall)
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    -- Primary Key: Auto-incrementing unique identifier
    id BIGSERIAL PRIMARY KEY,
    
    -- User Credentials
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash (60 chars, but allowing buffer)
    
    -- Personal Information
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    
    -- Shipping Information
    shipping_address VARCHAR(255) NOT NULL,
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT username_length CHECK (LENGTH(username) >= 3),
    CONSTRAINT email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for better query performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Stores user account information for the Auction404 system';
COMMENT ON COLUMN users.id IS 'Unique identifier for each user';
COMMENT ON COLUMN users.username IS 'Unique username (3-20 characters)';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.first_name IS 'User first name';
COMMENT ON COLUMN users.last_name IS 'User last name';
COMMENT ON COLUMN users.email IS 'Unique email address for account recovery';
COMMENT ON COLUMN users.shipping_address IS 'Default shipping address for auction deliveries';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the account was created';

-- ============================================
-- Sample Data (Optional - for testing)
-- ============================================

-- Insert test user (password is 'password123' hashed with BCrypt)
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (username, password, first_name, last_name, email, shipping_address) 
VALUES 
    ('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
     'John', 'Doe', 'john.doe@example.com', '123 Main St, Toronto, ON M1A 1A1');

-- ============================================
-- Verification Queries
-- ============================================

-- Check table structure
-- SELECT column_name, data_type, character_maximum_length, is_nullable, column_default
-- FROM information_schema.columns
-- WHERE table_name = 'users'
-- ORDER BY ordinal_position;

-- Count users
-- SELECT COUNT(*) as total_users FROM users;

-- View all users (excluding password)
-- SELECT id, username, first_name, last_name, email, shipping_address, created_at
-- FROM users;
