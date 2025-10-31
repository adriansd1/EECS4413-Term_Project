// ============================================
// VALIDATION SERVICE
// ============================================
package com.auction404.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * Validation Service
 * Centralized validation logic for user input.
 * Ensures data integrity before database operations.
 * 
 * Implements validation requirements from TC-01 (Sign-Up test cases)
 */
@Service
public class ValidationService {
    
    // Regex pattern for email validation
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Validate username format
     * Rules: 3-20 characters, alphanumeric and underscore only
     * 
     * Valid examples: john_doe, user123, JohnDoe
     * Invalid examples: ab (too short), john@doe (contains @)
     * 
     * @param username - Username to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        // ^ = start, $ = end, [a-zA-Z0-9_] = allowed chars, {3,20} = length range
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Validate password strength
     * Rules: Minimum 8 characters, at least one uppercase, one lowercase, one digit
     * 
     * Valid examples: Password123, MyPass1
     * Invalid examples: password (no uppercase/digit), Pass1 (too short)
     * 
     * @param password - Password to validate
     * @return true if meets security requirements, false otherwise
     */
    public boolean isValidPassword(String password) {
        if (password == null) return false;
        // Check all requirements
        return password.length() >= 8 &&  // Minimum 8 characters
               password.matches(".*[A-Z].*") &&  // At least one uppercase letter
               password.matches(".*[a-z].*") &&  // At least one lowercase letter
               password.matches(".*\\d.*");  // At least one digit
    }
    
    /**
     * Validate email format
     * Uses regex pattern to check email structure
     * 
     * @param email - Email address to validate
     * @return true if valid email format, false otherwise
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate name (first name or last name)
     * Rules: Minimum 2 characters, letters, spaces, hyphens, and apostrophes only
     * 
     * Valid examples: John, Mary-Jane, O'Connor
     * Invalid examples: J (too short), John123 (contains digits)
     * 
     * @param name - Name to validate
     * @return true if valid name format, false otherwise
     */
    public boolean isValidName(String name) {
        return name != null && name.length() >= 2 && 
               name.matches("^[a-zA-Z\\s'-]+$");
    }
    
    /**
     * Validate shipping address
     * Rules: Minimum 5 characters (simple validation)
     * 
     * @param address - Shipping address to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidAddress(String address) {
        return address != null && address.length() >= 5;
    }
}
