--
-- SQL Script to create the 'users' table based on the JPA entity 'User.java'
-- Designed for PostgreSQL, ensuring all constraints from the Java code are met.
--

-- Drop the table if it already exists to allow for clean re-creation
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    -- Primary Key: Corresponds to @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
    id BIGSERIAL PRIMARY KEY,

    -- Username: Corresponds to @Column(unique = true, nullable = false)
    username VARCHAR(255) NOT NULL UNIQUE,

    -- Password: Corresponds to @Column(nullable = false). Stored as BCrypt hash.
    password VARCHAR(255) NOT NULL,

    -- First Name: Corresponds to @Column(nullable = false)
    first_name VARCHAR(255) NOT NULL,

    -- Last Name: Corresponds to @Column(nullable = false)
    last_name VARCHAR(255) NOT NULL,

    -- Shipping Address: Corresponds to @Column(nullable = false)
    shipping_address VARCHAR(255) NOT NULL,

    -- Email Address: Corresponds to @Column(nullable = false, unique = true)
    email VARCHAR(255) NOT NULL UNIQUE,

    -- Is Authenticated: Corresponds to boolean isAuthenticated. Default to false.
    -- BOOLEAN is the standard type for boolean in PostgreSQL.
    is_authenticated BOOLEAN NOT NULL DEFAULT FALSE,

    -- Created At: Corresponds to @Column(name = "created_at") and LocalDateTime
    -- TIMESTAMP WITHOUT TIME ZONE is suitable for Java's LocalDateTime
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Add comments to the table and columns for better documentation
COMMENT ON TABLE users IS 'Stores user accounts for the Auction404 system.';
COMMENT ON COLUMN users.is_authenticated IS 'Indicates if the current session is authenticated.';