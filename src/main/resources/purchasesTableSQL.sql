CREATE TABLE purchases (
    purchase_id UUID PRIMARY KEY,                          -- corresponds to @Id @Column(columnDefinition = "uuid")
    
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,  -- ManyToOne to User entity
    
    item VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL CHECK (amount > 0),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
    shipping_address VARCHAR(255) NOT NULL,
    
    card_tail INTEGER,                                      -- last 4 digits of the card
    purchased_at TIMESTAMP,                                 -- time of purchase
    
    user_name VARCHAR(255) NOT NULL,
    winner_name VARCHAR(255)
);

CREATE INDEX idx_purchases_user_id ON purchases(user_id);