CREATE TABLE receipts (
    receipt_id UUID PRIMARY KEY,                                   -- @Id

    purchase_id UUID UNIQUE REFERENCES purchases(purchase_id)      -- @OneToOne to Purchases
        ON DELETE CASCADE ON UPDATE CASCADE,

    winner_id BIGINT REFERENCES users(id)                          -- @ManyToOne to User (winner)
        ON DELETE SET NULL ON UPDATE CASCADE,

    owner_id BIGINT REFERENCES users(id)                           -- @ManyToOne to User (owner)
        ON DELETE SET NULL ON UPDATE CASCADE,

    winner_name VARCHAR(255),
    owner_name VARCHAR(255),

    winner_address VARCHAR(255),
    owner_address VARCHAR(255),

    auction_item VARCHAR(255),
    amount INTEGER CHECK (amount >= 0),
    price DOUBLE PRECISION CHECK (price >= 0),
    final_price DOUBLE PRECISION CHECK (final_price >= 0),
    shipping_days INTEGER CHECK (shipping_days >= 0)
);

CREATE INDEX idx_receipts_winner_id ON receipts(winner_id);
CREATE INDEX idx_receipts_owner_id ON receipts(owner_id);
CREATE INDEX idx_receipts_purchase_id ON receipts(purchase_id);