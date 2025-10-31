package com.auction404.dto;

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
    
    public UserDTO(Long id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
