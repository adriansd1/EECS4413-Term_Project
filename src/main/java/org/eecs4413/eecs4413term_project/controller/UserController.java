package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * DTO (Data Transfer Object) for creating a user.
     * This securely encapsulates the request data.
     * * --- THIS IS THE FIX ---
     * Updated to match the new User constructor.
     */
    static class UserCreateRequest {
        public String username;
        public String password;
        public String firstName;
        public String lastName;
        public String shippingAddress;
        public String email;
    }

    /**
     * POST /api/users
     * Creates a new User.
     */
    @PostMapping
    public User createUser(@RequestBody UserCreateRequest request) {
        
        // --- THIS IS THE FIX ---
        // We now use the new constructor with all the required fields from the request.
        User newUser = new User(
            request.username,
            request.password, // Remember to HASH this password in a real app!
            request.firstName,
            request.lastName,
            request.shippingAddress,
            request.email
        );
        
        // newUser.setAuthenticated(true); // You might want to log them in
        return userRepository.save(newUser);
    }

    /**
     * GET /api/users
     * Gets all Users in the database.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}