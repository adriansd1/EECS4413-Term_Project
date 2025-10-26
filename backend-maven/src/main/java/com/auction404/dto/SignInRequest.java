package com.auction404.dto;

/**
 * Sign-In Request DTO
 * Represents the credentials sent from frontend when a user signs in (UC1.2).
 */
public class SignInRequest {
    private String username;
    private String password;
    
    public SignInRequest() {}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}