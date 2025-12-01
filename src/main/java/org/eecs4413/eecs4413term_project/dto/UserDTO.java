package org.eecs4413.eecs4413term_project.dto;

/**
 * User DTO
 * Represents user data sent to frontend (excludes sensitive info like password).
 * Used in AuthResponse after successful sign-in.
 */
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String shippingAddress;
    
    public UserDTO(Long id, String username, String firstName, String lastName, String email, String shippingAddress) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.shippingAddress = shippingAddress;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getShippingAddress() { return shippingAddress; }
}
