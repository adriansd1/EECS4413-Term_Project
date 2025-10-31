
package org.eecs4413.eecs4413term_project.dto;

/**
 * Authentication Response DTO
 * Standardized response sent back to frontend for all auth operations.
 * 
 * Contains:
 * - success: Boolean indicating if operation succeeded
 * - message: Human-readable success/error message
 * - token: JWT token (only on successful sign-in)
 * - user: User details (only on successful sign-in)
 */
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;  // JWT token for authenticated requests
    private UserDTO user;  // User info (without sensitive data like password)
    
    /**
     * Constructor for responses without token (sign-up, errors)
     */
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Constructor for successful sign-in responses (includes token and user)
     */
    public AuthResponse(boolean success, String message, String token, UserDTO user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDTO getUser() { return user; }
}
