--
-- SQL Script to create the 'auctions' table based on the JPA entity 'AuctionClass.java'
--
-- Prerequisite: The 'users' table must already exist.
--

-- Drop the table if it already exists to allow for clean re-creation
DROP TABLE IF EXISTS auctions;

CREATE TABLE auctions (
    -- Primary Key: Corresponds to @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
    id BIGSERIAL PRIMARY KEY,

    -- itemName: Corresponds to @Column(nullable = false)
    item_name VARCHAR(255) NOT NULL,

    -- startingPrice: Corresponds to @Column(nullable = false) and BigDecimal
    -- Using NUMERIC(19, 2) for standard currency precision
    starting_price NUMERIC(19, 2) NOT NULL,

    -- currentHighestBid: BigDecimal, nullable (will default to startingPrice in Java)
    current_highest_bid NUMERIC(19, 2),

    -- endTime: Corresponds to @Column(nullable = false) and LocalDateTime
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- isClosed: Corresponds to boolean isClosed. Defaults to FALSE in Java constructor.
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,

    -- Foreign Key: Corresponds to @ManyToOne(currentHighestBidder) and @JoinColumn(name = "current_highest_bidder_id")
    -- Links to the User currently winning the auction. This field is nullable.
    current_highest_bidder_id BIGINT,

    -- Define Foreign Key Constraints (MUST be done after defining the columns)
    CONSTRAINT fk_current_highest_bidder
        FOREIGN KEY (current_highest_bidder_id)
        REFERENCES users (id)
        ON DELETE SET NULL -- Set to NULL if the user account is deleted
);

-- Optional: Add comments to the table and columns for documentation
COMMENT ON TABLE auctions IS 'Stores all active and closed auction listings.';