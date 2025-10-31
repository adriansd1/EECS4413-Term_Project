package com.auction404.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
/**
 * User Entity Class
 * Represents a user in the Auction404 system.
 * This class is mapped to the "users" table in PostgreSQL database.
 * 
 *
 */
@Entity  // Marks this class as a JPA entity (database table)
@Table(name = "users")  // Specifies the table name in the database
public class User {
    
    // Primary key field - uniquely identifies each user
    @Id  // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID (1, 2, 3...)
    private Long id;
    
    // Username - must be unique across all users
    @Column(unique = true, nullable = false)  // Database constraint: unique and required
    private String username;
    
    // Password - stored as BCrypt hash, never plain text
    @Column(nullable = false)  // Required field
    private String password;
    
    // User's first name
    @Column(nullable = false)
    private String firstName;
    
    // User's last name
    @Column(nullable = false)
    private String lastName;
    
    // Shipping address for auction item delivery
    @Column(nullable = false)
    private String shippingAddress;
    
    // Email address - must be unique for account recovery
    @Column(nullable = false, unique = true)
    private String email;
    
    // Timestamp when the user account was created
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Default constructor required by JPA
     */
    public User() {}
    
    /**
     * Constructor for creating a new user with all required fields
     * @param username - User's chosen username (3-20 characters)
     * @param password - BCrypt hashed password
     * @param firstName - User's first name
     * @param lastName - User's last name
     * @param shippingAddress - Delivery address for won auction items
     * @param email - User's email address
     */
    public User(String username, String password, String firstName, 
                String lastName, String shippingAddress, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.shippingAddress = shippingAddress;
        this.email = email;
        this.createdAt = LocalDateTime.now();  // Set creation timestamp
    }
    
    // Getters and Setters
    // These methods allow controlled access to private fields
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { 
        this.shippingAddress = shippingAddress; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}