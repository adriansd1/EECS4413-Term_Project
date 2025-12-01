// ============================================
// UNIT TESTS (JUnit + Mockito)
// ============================================
package org.eecs4413.eecs4413term_project;

import org.eecs4413.eecs4413term_project.dto.SignUpRequest;
import org.eecs4413.eecs4413term_project.dto.SignInRequest;
import org.eecs4413.eecs4413term_project.dto.AuthResponse;
import org.eecs4413.eecs4413term_project.model.User;
import java.util.Optional;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.eecs4413.eecs4413term_project.service.ValidationService;
import org.eecs4413.eecs4413term_project.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Authentication Service Unit Tests
 * Tests business logic without touching the database.
 * 
 * Test Coverage:
 * - TC-01: Successful sign-up
 * - TC-02: Duplicate username rejection
 * - TC-03: Successful sign-in
 * - TC-04: Invalid password rejection
 * 
 * Uses Mockito to create fake (mock) dependencies.
 */
public class AuthenticationServiceTest {
    
    // Mock the repository - no real database calls
    @Mock
    private UserRepository userRepository;
    
    // Mock the validation service
    @Mock
    private ValidationService validationService;
    
    // Mock the JWT utility
    @Mock
    private org.eecs4413.eecs4413term_project.security.JwtUtil jwtUtil;
    
    // Inject mocks into the service we're testing
    @InjectMocks
    private AuthenticationService authenticationService;
    
    /**
     * Setup method - runs before each test
     * Initializes mocks and sets up test environment
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * Test Case TC-01: Sign-Up Success
     * Tests that a valid user can successfully create an account.
     * 
     * Steps:
     * 1. Create valid sign-up request
     * 2. Mock validation to return true for all fields
     * 3. Mock repository to indicate username/email don't exist
     * 4. Call sign-up service
     * 5. Assert success response
     * 6. Verify user was saved to database
     */
    @Test
    public void testSignUpSuccess() {
        // Arrange: Create test data
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john_doe");
        request.setPassword("Password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setShippingAddress("123 Main St");
        
        // Mock validation service to return true (all fields valid)
        when(validationService.isValidUsername("john_doe")).thenReturn(true);
        when(validationService.isValidPassword("Password123")).thenReturn(true);
        when(validationService.isValidEmail("john@example.com")).thenReturn(true);
        when(validationService.isValidName("John")).thenReturn(true);
        when(validationService.isValidName("Doe")).thenReturn(true);
        when(validationService.isValidAddress("123 Main St")).thenReturn(true);
        
        // Mock repository to indicate username and email don't exist (can register)
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signUp(request);
        
        // Assert: Verify the response is successful
        assertTrue(response.isSuccess());
        assertEquals("Account created successfully", response.getMessage());
        
        // Verify that save() was called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    /**
     * Test Case TC-02: Sign-Up with Existing Username
     * Tests that duplicate usernames are rejected.
     * 
     * Expected behavior: Return error "Username already exists"
     * Database should NOT be modified
     */
    @Test
    public void testSignUpUsernameExists() {
        // Arrange: Create sign-up request
        SignUpRequest request = new SignUpRequest();
        request.setUsername("existing_user");
        request.setPassword("Password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane@example.com");
        request.setShippingAddress("456 Oak Ave");
        
        // Mock all validations to pass
        when(validationService.isValidUsername("existing_user")).thenReturn(true);
        when(validationService.isValidPassword("Password123")).thenReturn(true);
        when(validationService.isValidEmail("jane@example.com")).thenReturn(true);
        when(validationService.isValidName("Jane")).thenReturn(true);
        when(validationService.isValidName("Smith")).thenReturn(true);
        when(validationService.isValidAddress("456 Oak Ave")).thenReturn(true);
        
        // Mock repository to indicate username ALREADY exists
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signUp(request);
        
        // Assert: Verify error response
        assertFalse(response.isSuccess());
        assertEquals("Username already exists", response.getMessage());
        
        // Verify that save() was NEVER called (no database modification)
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * Test Case TC-03: Sign-In Success
     * Tests that a valid user can successfully sign in.
     * 
     * Expected behavior:
     * - Return success response
     * - Include JWT token
     * - Include user information
     */
    @Test
    public void testSignInSuccess() {
        // Arrange: Create a mock user in the database
        // Note: Password must be BCrypt hashed
        User mockUser = new User(
            "john_doe", 
            new BCryptPasswordEncoder().encode("Password123"),  // Hash the password
            "John", 
            "Doe", 
            "123 Main St", 
            "john@example.com"
        );
        mockUser.setId(1L);  // Simulate database-generated ID
        
        // Mock repository to return this user when searched
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        
        // Mock JWT token generation
        when(jwtUtil.generateToken("john_doe", 1L)).thenReturn("mock-jwt-token");
        
        // Create sign-in request
        org.eecs4413.eecs4413term_project.dto.SignInRequest request = new org.eecs4413.eecs4413term_project.dto.SignInRequest();
        request.setUsername("john_doe");
        request.setPassword("Password123");  // Plain text password (user input)
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signIn(request);
        
        // Assert: Verify successful response
        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getToken());  // JWT token should be present
        assertNotNull(response.getUser());  // User info should be present
        assertEquals("john_doe", response.getUser().getUsername());
    }
    
    /**
     * Test Case TC-04: Sign-In with Invalid Password
     * Tests that incorrect passwords are rejected.
     * 
     * Expected behavior: Return error "Invalid username or password"
     * No token should be generated
     */
    @Test
    public void testSignInInvalidPassword() {
        // Arrange: Create a mock user with known password
        User mockUser = new User(
            "john_doe", 
            new BCryptPasswordEncoder().encode("Password123"),  // Correct password
            "John", 
            "Doe", 
            "123 Main St", 
            "john@example.com"
        );
        
        // Mock repository to return user
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        
        // Create sign-in request with WRONG password
        org.eecs4413.eecs4413term_project.dto.SignInRequest request = new org.eecs4413.eecs4413term_project.dto.SignInRequest();
        request.setUsername("john_doe");
        request.setPassword("WrongPassword");  // Incorrect password
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signIn(request);
        
        // Assert: Verify error response
        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password", response.getMessage());
        assertNull(response.getToken());  // No token should be generated
    }
    
    /**
     * Test Case: Sign-In with Non-Existent User
     * Tests that non-existent usernames are rejected.
     * 
     * Expected behavior: Return generic error message
     */
    @Test
    public void testSignInUserNotFound() {
        // Arrange: Mock repository to return null (user not found)
        when(userRepository.findByUsername("nonexistent_user")).thenReturn(null);
        
        // Create sign-in request
        org.eecs4413.eecs4413term_project.dto.SignInRequest request = new org.eecs4413.eecs4413term_project.dto.SignInRequest();
        request.setUsername("nonexistent_user");
        request.setPassword("Password123");
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signIn(request);
        
        // Assert: Verify error response
        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password", response.getMessage());
        
        // Security Note: We don't reveal if username exists or not
        // Generic message prevents username enumeration attacks
    }
    
    /**
     * Test Case: Sign-Up with Invalid Email
     * Tests that invalid email formats are rejected.
     */
    @Test
    public void testSignUpInvalidEmail() {
        // Arrange: Create request with invalid email
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john_doe");
        request.setPassword("Password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email");  // Invalid format
        request.setShippingAddress("123 Main St");
        
        // Mock username and password validation to pass
        when(validationService.isValidUsername("john_doe")).thenReturn(true);
        when(validationService.isValidPassword("Password123")).thenReturn(true);
        
        // Mock email validation to FAIL
        when(validationService.isValidEmail("invalid-email")).thenReturn(false);
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signUp(request);
        
        // Assert: Verify error response
        assertFalse(response.isSuccess());
        assertEquals("Invalid email format", response.getMessage());
        
        // Verify save was never called
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * Test Case: Sign-Up with Weak Password
     * Tests that weak passwords are rejected.
     */
    @Test
    public void testSignUpWeakPassword() {
        // Arrange: Create request with weak password
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john_doe");
        request.setPassword("weak");  // Too short, no uppercase, no digits
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setShippingAddress("123 Main St");
        
        // Mock username validation to pass
        when(validationService.isValidUsername("john_doe")).thenReturn(true);
        
        // Mock password validation to FAIL
        when(validationService.isValidPassword("weak")).thenReturn(false);
        
        // Act: Call the service method
        AuthResponse response = authenticationService.signUp(request);
        
        // Assert: Verify error response
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Password must be"));
        
        // Verify save was never called
        verify(userRepository, never()).save(any(User.class));
    }
}
