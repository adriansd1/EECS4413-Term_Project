// ============================================
// AUTHENTICATION SERVICE (Business Logic)
// ============================================
package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.dto.SignUpRequest;
import org.eecs4413.eecs4413term_project.dto.SignInRequest;
import org.eecs4413.eecs4413term_project.dto.AuthResponse;
import org.eecs4413.eecs4413term_project.dto.UserDTO;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.eecs4413.eecs4413term_project.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * Core business logic for user sign-up (UC1.1) and sign-in (UC1.2).
 * 
 * Responsibilities:
 * 1. Validate user input
 * 2. Hash passwords securely
 * 3. Check for duplicate users
 * 4. Generate JWT tokens
 * 5. Return appropriate responses
 */
@Service  // Marks this as a Spring service layer component
public class AuthenticationService {
    
    // Dependencies injected by Spring
    @Autowired
    private UserRepository userRepository;  // Database access
    
    @Autowired
    private ValidationService validationService;  // Input validation
    
    @Autowired
    private JwtUtil jwtUtil;  // JWT token management
    
    // BCrypt password encoder - industry standard for password hashing
    // Uses adaptive hashing (slow by design to prevent brute-force attacks)
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Sign-Up Operation (UC1.1)
     * Creates a new user account with validated information.
     * 
     * Test Coverage: TC-01 (success), TC-02 (duplicate username)
     * 
     * Process:
     * 1. Validate all input fields
     * 2. Check for existing username/email
     * 3. Hash password with BCrypt
     * 4. Save user to database
     * 5. Return success/failure response
     * 
     * @param request - SignUpRequest containing user registration data
     * @return AuthResponse with success status and message
     */
    public AuthResponse signUp(SignUpRequest request) {
        // Step 1: Validate username format
        if (!validationService.isValidUsername(request.getUsername())) {
            return new AuthResponse(false, 
                "Username must be 3-20 characters, alphanumeric and underscore only");
        }
        
        // Step 2: Validate password strength
        if (!validationService.isValidPassword(request.getPassword())) {
            return new AuthResponse(false, 
                "Password must be at least 8 characters with uppercase, lowercase, and digit");
        }
        
        // Step 3: Validate email format
        if (!validationService.isValidEmail(request.getEmail())) {
            return new AuthResponse(false, "Invalid email format");
        }
        
        // Step 4: Validate first name
        if (!validationService.isValidName(request.getFirstName())) {
            return new AuthResponse(false, "Invalid first name");
        }
        
        // Step 5: Validate last name
        if (!validationService.isValidName(request.getLastName())) {
            return new AuthResponse(false, "Invalid last name");
        }
        
        // Step 6: Validate shipping address
        if (!validationService.isValidAddress(request.getShippingAddress())) {
            return new AuthResponse(false, 
                "Shipping address must be at least 5 characters");
        }
        
        // Step 7: Check if username already exists (TC-02)
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(false, "Username already exists");
        }
        
        // Step 8: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email already registered");
        }
        
        // Step 9: Create new user with hashed password
        // CRITICAL: Never store plain-text passwords!
        // BCrypt creates a salted hash like: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),  // Hash password
                request.getFirstName(),
                request.getLastName(),
                request.getShippingAddress(),
                request.getEmail()
        );
        
        // Step 10: Save to database
        try {
            userRepository.save(user);  // Spring converts to INSERT SQL
            return new AuthResponse(true, "Account created successfully");
        } catch (Exception e) {
            // Catch any database errors (network issues, constraint violations, etc.)
            return new AuthResponse(false, "Error creating account: " + e.getMessage());
        }
    }
    
    /**
     * Sign-In Operation (UC1.2)
     * Authenticates a user and returns a JWT token for session management.
     * 
     * Test Coverage: TC-03 (success), TC-04 (invalid password)
     * 
     * Process:
     * 1. Validate input exists
     * 2. Find user in database
     * 3. Compare hashed passwords
     * 4. Generate JWT token
     * 5. Return token and user info
     * 
     * @param request - SignInRequest containing username and password
     * @return AuthResponse with JWT token on success, error message on failure
     */
    public AuthResponse signIn(SignInRequest request) {
        // Step 1: Validate input exists
        if (request.getUsername() == null || request.getPassword() == null) {
            return new AuthResponse(false, "Username and password required");
        }
        
        // Step 2: Find user by username
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        
        // Step 3: Check if user exists
        if (user == null) {
            // Generic error message - don't reveal if username exists (security best practice)
            return new AuthResponse(false, "Invalid username or password");
        }
        
        // Step 4: Verify password
        // passwordEncoder.matches() hashes the input and compares with stored hash
        // Input: "Password123" → Hash → Compare with DB hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // TC-04: Invalid password test case
            return new AuthResponse(false, "Invalid username or password");
        }
        
        // Step 5: Generate JWT token (valid for 24 hours by default)
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        
        // Step 6: Create UserDTO (excludes password and other sensitive data)
        UserDTO userDTO = new UserDTO(
            user.getId(), 
            user.getUsername(), 
            user.getFirstName(), 
            user.getLastName()
        );
        
        // Step 7: Return success response with token and user info
        // TC-03: Successful sign-in test case
        return new AuthResponse(true, "Login successful", token, userDTO);
    }
}
