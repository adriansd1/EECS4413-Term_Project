-- =======================================================
-- CLEAN START
-- =======================================================
DROP TABLE IF EXISTS catalogue CASCADE;

-- =======================================================
-- CATALOGUE TABLE (UC2.1–UC2.3 + UC7 with images)
-- =======================================================
CREATE TABLE catalogue (
                           id SERIAL PRIMARY KEY,                                     -- Unique auction item ID
                           title VARCHAR(255) NOT NULL,                               -- Item name
                           description TEXT NOT NULL,                                 -- Item details
                           type VARCHAR(100) NOT NULL,                                -- Category/type (e.g., Electronics, Art)
                           starting_price NUMERIC(10, 2) NOT NULL CHECK (starting_price >= 0),
                           current_bid NUMERIC(10, 2) DEFAULT 0 CHECK (current_bid >= 0),
                           end_time TIMESTAMP NOT NULL,                               -- Auction end timestamp
                           seller VARCHAR(255),                                       -- Optional seller name or ID
                           image_url TEXT,                                            -- Optional URL/path to image (for front-end display)
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP            -- Time item was uploaded
);

-- =======================================================
-- INDEXES
-- =======================================================

-- GIN index for keyword search (UC2.1)
CREATE INDEX idx_catalogue_search
    ON catalogue
        USING gin (to_tsvector('english', title || ' ' || description));

-- Index for active auctions (UC2.2)
CREATE INDEX idx_catalogue_end_time
    ON catalogue (end_time);

-- =======================================================
-- SEED DATA (For testing UC2.1–UC7)
-- =======================================================
INSERT INTO catalogue (title, description, type, starting_price, current_bid, end_time, seller, image_url)
VALUES
    ('Vintage Clock', 'Antique clock from early 1900s', 'Antique', 100.00, 120.00, NOW() + INTERVAL '3 days', 'AntiqueSeller', '/images/clock.jpg'),
    ('iPhone 13', 'Brand new sealed iPhone 13, 128GB', 'Electronics', 800.00, 850.00, NOW() + INTERVAL '2 days', 'TechStore', '/images/iphone13.jpg'),
    ('Gaming Chair', 'Ergonomic chair with lumbar support and headrest', 'Furniture', 150.00, 180.00, NOW() + INTERVAL '5 days', 'ChairWorld', '/images/chair.jpg'),
    ('Oil Painting', 'Beautiful oil painting in a wooden frame', 'Art', 200.00, 0.00, NOW() + INTERVAL '1 day', 'ArtGallery', '/images/painting.jpg'),
    ('Laptop', 'Lightweight 14-inch laptop, 16GB RAM', 'Electronics', 900.00, 950.00, NOW() + INTERVAL '4 days', 'TechStore', '/images/laptop.jpg');
