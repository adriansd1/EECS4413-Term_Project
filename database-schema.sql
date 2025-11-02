-- ============================================
-- EECS4413 Auction404 - Complete Database Schema
-- PostgreSQL Database Setup Script
-- Team Project - Deliverable 2
-- ============================================

-- This script creates all tables needed for the Auction404 application
-- Run this script on a fresh PostgreSQL database to set up the complete schema

-- ============================================
-- DROP ALL TABLES (for clean installation)
-- ============================================

DROP TABLE IF EXISTS receipts CASCADE;
DROP TABLE IF EXISTS purchases CASCADE;
DROP TABLE IF EXISTS bidding CASCADE;
DROP TABLE IF EXISTS auctions CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================
-- TABLE 1: USERS
-- Stores user account information (UC1.1 - Sign Up, UC1.2 - Sign In)
-- ============================================

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

-- Indexes for users table
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Comments
COMMENT ON TABLE users IS 'Stores user account information for the Auction404 system';
COMMENT ON COLUMN users.id IS 'Unique identifier for each user';
COMMENT ON COLUMN users.username IS 'Unique username (3-20 characters)';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';

-- ============================================
-- TABLE 2: AUCTIONS
-- Stores auction listings (UC3 - Create Auction)
-- ============================================

CREATE TABLE auctions (
    auction_id BIGSERIAL PRIMARY KEY,
    
    -- Owner/Seller reference
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Auction details
    item_name VARCHAR(255) NOT NULL,
    description TEXT,
    starting_price DOUBLE PRECISION NOT NULL CHECK (starting_price >= 0),
    current_price DOUBLE PRECISION NOT NULL CHECK (current_price >= 0),
    
    -- Timing
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NOT NULL,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Winner (nullable until auction ends)
    winner_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT end_time_after_start CHECK (end_time > start_time)
);

-- Indexes for auctions table
CREATE INDEX idx_auctions_owner_id ON auctions(owner_id);
CREATE INDEX idx_auctions_winner_id ON auctions(winner_id);
CREATE INDEX idx_auctions_is_active ON auctions(is_active);
CREATE INDEX idx_auctions_end_time ON auctions(end_time);

COMMENT ON TABLE auctions IS 'Stores auction listings and their current state';

-- ============================================
-- TABLE 3: BIDDING
-- Stores bid history (UC4 - Place Bid)
-- ============================================

CREATE TABLE bidding (
    bid_id BIGSERIAL PRIMARY KEY,
    
    -- References
    auction_id BIGINT NOT NULL REFERENCES auctions(auction_id) ON DELETE CASCADE,
    bidder_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Bid details
    bid_amount DOUBLE PRECISION NOT NULL CHECK (bid_amount > 0),
    bid_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Status
    is_winning BOOLEAN DEFAULT FALSE
);

-- Indexes for bidding table
CREATE INDEX idx_bidding_auction_id ON bidding(auction_id);
CREATE INDEX idx_bidding_bidder_id ON bidding(bidder_id);
CREATE INDEX idx_bidding_bid_time ON bidding(bid_time);

COMMENT ON TABLE bidding IS 'Stores all bids placed on auctions';

-- ============================================
-- TABLE 4: PURCHASES
-- Stores completed purchases (UC5 - Process Payment)
-- ============================================

CREATE TABLE purchases (
    purchase_id UUID PRIMARY KEY,
    
    -- User reference
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,

    -- Purchase details
    item VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL CHECK (amount > 0),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
    shipping_address VARCHAR(255) NOT NULL,

    -- Payment info
    card_tail INTEGER,  -- last 4 digits of the card
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- User names (denormalized for receipts)
    user_name VARCHAR(255) NOT NULL,
    winner_name VARCHAR(255)
);

-- Index for purchases table
CREATE INDEX idx_purchases_user_id ON purchases(user_id);

COMMENT ON TABLE purchases IS 'Stores completed purchase transactions';

-- ============================================
-- TABLE 5: RECEIPTS
-- Stores receipt information (UC6 - View Receipt/Shipment)
-- ============================================

CREATE TABLE receipts (
    receipt_id UUID PRIMARY KEY,

    -- Purchase reference
    purchase_id UUID UNIQUE REFERENCES purchases(purchase_id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    -- User references
    winner_id BIGINT REFERENCES users(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    owner_id BIGINT REFERENCES users(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    -- User names
    winner_name VARCHAR(255),
    owner_name VARCHAR(255),

    -- Addresses
    winner_address VARCHAR(255),
    owner_address VARCHAR(255),

    -- Receipt details
    auction_item VARCHAR(255),
    amount INTEGER CHECK (amount >= 0),
    price DOUBLE PRECISION CHECK (price >= 0),
    final_price DOUBLE PRECISION CHECK (final_price >= 0),
    shipping_days INTEGER CHECK (shipping_days >= 0),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for receipts table
CREATE INDEX idx_receipts_winner_id ON receipts(winner_id);
CREATE INDEX idx_receipts_owner_id ON receipts(owner_id);
CREATE INDEX idx_receipts_purchase_id ON receipts(purchase_id);

COMMENT ON TABLE receipts IS 'Stores receipt and shipment information';

-- ============================================
-- SAMPLE TEST DATA
-- ============================================

-- Insert test users
-- Password for all test users is 'password123'
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (username, password, first_name, last_name, email, shipping_address)
VALUES
    ('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'John', 'Doe', 'john.doe@example.com', '123 Main St, Toronto, ON M1A 1A1'),
    ('seller1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Jane', 'Smith', 'jane.smith@example.com', '456 Oak Ave, Toronto, ON M2B 2B2'),
    ('bidder1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Bob', 'Johnson', 'bob.j@example.com', '789 Elm St, Toronto, ON M3C 3C3');

-- Insert sample auctions
INSERT INTO auctions (owner_id, item_name, description, starting_price, current_price, start_time, end_time, is_active)
VALUES
    (2, 'Vintage Camera', 'Classic 35mm film camera in excellent condition', 50.00, 75.00, 
     NOW(), NOW() + INTERVAL '7 days', TRUE),
    (2, 'Antique Watch', 'Rare pocket watch from 1920s', 200.00, 200.00,
     NOW(), NOW() + INTERVAL '5 days', TRUE);

-- Insert sample bids
INSERT INTO bidding (auction_id, bidder_id, bid_amount, is_winning)
VALUES
    (1, 3, 75.00, TRUE),
    (1, 1, 60.00, FALSE);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Uncomment to verify the installation:

-- SELECT 'Users:' as table_name, COUNT(*) as record_count FROM users
-- UNION ALL
-- SELECT 'Auctions:', COUNT(*) FROM auctions
-- UNION ALL
-- SELECT 'Bids:', COUNT(*) FROM bidding
-- UNION ALL
-- SELECT 'Purchases:', COUNT(*) FROM purchases
-- UNION ALL
-- SELECT 'Receipts:', COUNT(*) FROM receipts;

-- ============================================
-- END OF SCHEMA
-- ============================================
