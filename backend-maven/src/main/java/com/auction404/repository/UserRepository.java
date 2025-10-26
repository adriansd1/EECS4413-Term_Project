package com.auction404.repository;

import com.auction404.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User Repository Interface
 * Handles all database operations for User entities.
 * Spring Data JPA automatically implements these methods at runtime.
 * 
 * JpaRepository provides built-in CRUD operations:
 * - save() - Insert or update user
 * - findById() - Find user by ID
 * - findAll() - Get all users
 * - delete() - Delete user
 */
@Repository  // Marks this as a Spring Data repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by their username
     * Spring auto-generates: SELECT * FROM users WHERE username = ?
     * @param username - The username to search for
     * @return User object if found, null otherwise
     */
    User findByUsername(String username);
    
    /**
     * Find a user by their email address
     * Auto-generates: SELECT * FROM users WHERE email = ?
     * @param email - The email to search for
     * @return User object if found, null otherwise
     */
    User findByEmail(String email);
    
    /**
     * Check if a username already exists in the database
     * Auto-generates: SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)
     * Used in UC1.1 to prevent duplicate usernames (TC-02)
     * @param username - Username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists in the database
     * Auto-generates: SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
     * @param email - Email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}