--
-- SQL Script to create the 'bids' table based on the JPA entity 'BiddingClass.java'
--
-- Prerequisites: The 'users' table and 'auction' table must already exist.
--

-- Drop the table if it already exists to allow for clean re-creation
DROP TABLE IF EXISTS bids;

CREATE TABLE bids (
    -- Primary Key: Corresponds to @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
    id BIGSERIAL PRIMARY KEY,

    -- bidAmount: Corresponds to @Column(nullable = false) and BigDecimal
    -- Using NUMERIC(19, 2) for standard currency precision
    bid_amount NUMERIC(19, 2) NOT NULL,

    -- bidTime: Corresponds to @Column(nullable = false) and LocalDateTime
    bid_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key: Corresponds to @ManyToOne(user) and @JoinColumn(name = "user_id", nullable = false)
    -- Links this bid to the User who placed it
    user_id BIGINT NOT NULL,

    -- Foreign Key: Corresponds to @ManyToOne(auction) and @JoinColumn(name = "auction_id", nullable = false)
    -- Links this bid to the Auction item
    auction_id BIGINT NOT NULL,

    -- Define Foreign Key Constraints (MUST be done after defining the columns)
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_auction
        FOREIGN KEY (auction_id)
        REFERENCES auction (id)
        ON DELETE CASCADE
);

-- Optional: Add comments to the table and columns for documentation
COMMENT ON TABLE bids IS 'Stores all bidding activity for auction items.';
COMMENT ON COLUMN bids.bid_amount IS 'The amount of the bid.';
COMMENT ON COLUMN bids.user_id IS 'Foreign key to the users table (the bidder).';
COMMENT ON COLUMN bids.auction_id IS 'Foreign key to the auction table (the item).';