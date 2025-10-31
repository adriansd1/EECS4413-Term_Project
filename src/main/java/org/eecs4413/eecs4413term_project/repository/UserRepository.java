package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- 3. IMPORT OPTIONAL

/**
 * User Repository Interface
 * Handles all database operations for User entities.
 * Spring Data JPA automatically implements these methods at runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username
     * @param username - The username to search for
     * @return An Optional containing the User if found, or an empty Optional if not
     */
    Optional<User> findByUsername(String username); // <-- 4. FIXED RETURN TYPE

    /**
     * Find a user by their email address
     * @param email - The email to search for
     * @return An Optional containing the User if found, or an empty Optional if not
     */
    Optional<User> findByEmail(String email); // <-- 5. FIXED RETURN TYPE

    /**
     * Check if a username already exists in the database
     * @param username - Username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists in the database
     * @param email - Email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}