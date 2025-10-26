// ============================================
// DTOs (Data Transfer Objects)
// ============================================
package com.auction404.dto;

/**
 * Sign-Up Request DTO
 * Represents the data sent from frontend when a user signs up (UC1.1).
 * Separates API contract from internal database model.
 * 
 * Frontend sends JSON → Spring converts to this object → Service processes it
 */
public class SignUpRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String shippingAddress;
    private String email;
    
    // Default constructor required for JSON deserialization
    public SignUpRequest() {}
    
    // Getters and setters for all fields
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
}