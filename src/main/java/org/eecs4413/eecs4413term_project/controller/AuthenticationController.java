// ============================================
// REST CONTROLLER (API Endpoints)
// ============================================
package com.auction404.controller;

import com.auction404.dto.SignUpRequest;
import com.auction404.dto.SignInRequest;
import com.auction404.dto.AuthResponse;
import com.auction404.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller
 * Exposes HTTP endpoints for user authentication operations.
 * 
 * Base URL: /api/auth
 * Endpoints:
 * - POST /api/auth/signup - Create new user account (UC1.1)
 * - POST /api/auth/signin - Authenticate user (UC1.2)
 * 
 * Returns JSON responses to frontend React application.
 */
@RestController  // Marks this as a REST API controller (returns JSON, not views)
@RequestMapping("/api/auth")  // Base path for all endpoints in this controller
@CrossOrigin(origins = "*")  // Allow requests from any origin (for development)
// TODO: In production, restrict to specific frontend domain: @CrossOrigin(origins = "https://auction404.com")
public class AuthenticationController {
    
    // Inject authentication service (contains business logic)
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * Sign-Up Endpoint (UC1.1)
     * POST /api/auth/signup
     * 
     * Accepts JSON request body with user registration data.
     * Returns HTTP 201 (CREATED) on success, HTTP 400 (BAD REQUEST) on failure.
     * 
     * Example Request:
     * POST /api/auth/signup
     * {
     *   "username": "john_doe",
     *   "password": "Password123",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john@example.com",
     *   "shippingAddress": "123 Main St"
     * }
     * 
     * Example Success Response (201):
     * {
     *   "success": true,
     *   "message": "Account created successfully"
     * }
     * 
     * Example Error Response (400):
     * {
     *   "success": false,
     *   "message": "Username already exists"
     * }
     * 
     * @param request - SignUpRequest DTO (automatically parsed from JSON)
     * @return ResponseEntity with AuthResponse and appropriate HTTP status
     */
    @PostMapping("/signup")  // Maps POST requests to /api/auth/signup
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
        // @RequestBody automatically converts JSON to SignUpRequest object
        
        // Call service layer to process sign-up
        AuthResponse response = authenticationService.signUp(request);
        
        // Return appropriate HTTP status based on success/failure
        return response.isSuccess() ? 
            ResponseEntity.status(HttpStatus.CREATED).body(response) :  // 201 Created
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // 400 Bad Request
    }
    
    /**
     * Sign-In Endpoint (UC1.2)
     * POST /api/auth/signin
     * 
     * Accepts JSON request body with username and password.
     * Returns HTTP 200 (OK) with JWT token on success.
     * Returns HTTP 401 (UNAUTHORIZED) on invalid credentials.
     * 
     * Example Request:
     * POST /api/auth/signin
     * {
     *   "username": "john_doe",
     *   "password": "Password123"
     * }
     * 
     * Example Success Response (200):
     * {
     *   "success": true,
     *   "message": "Login successful",
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "user": {
     *     "id": 1,
     *     "username": "john_doe",
     *     "firstName": "John",
     *     "lastName": "Doe"
     *   }
     * }
     * 
     * Example Error Response (401):
     * {
     *   "success": false,
     *   "message": "Invalid username or password"
     * }
     * 
     * @param request - SignInRequest DTO (automatically parsed from JSON)
     * @return ResponseEntity with AuthResponse and appropriate HTTP status
     */
    @PostMapping("/signin")  // Maps POST requests to /api/auth/signin
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest request) {
        // Call service layer to process sign-in
        AuthResponse response = authenticationService.signIn(request);
        
        // Return appropriate HTTP status based on success/failure
        return response.isSuccess() ? 
            ResponseEntity.ok(response) :  // 200 OK
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // 401 Unauthorized
    }
}
