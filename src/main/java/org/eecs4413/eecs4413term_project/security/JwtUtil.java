// ============================================
// JWT UTILITY (Token Management)
// ============================================
package org.eecs4413.eecs4413term_project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT Utility Class
 * Handles creation and validation of JSON Web Tokens (JWT).
 * 
 * JWT Format: header.payload.signature
 * Example: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIn0.signature
 * 
 * Used for stateless authentication - no server-side session storage needed.
 */
@Component  // Spring manages this as a bean
public class JwtUtil {
    
    // Secret key for signing tokens (loaded from application.properties)
    // IMPORTANT: In production, use a strong 32+ character secret
    @Value("${jwt.secret:your-secret-key-min-32-characters-long}")
    private String secret;
    
    // Token expiration time in milliseconds (default: 24 hours)
    @Value("${jwt.expiration:86400000}")
    private long expiration;
    
    /**
     * Get the signing key from the secret string
     * Used to sign and verify tokens
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * Generate a JWT token for an authenticated user
     * Called after successful sign-in (UC1.2)
     * 
     * @param username - User's username (stored in token subject)
     * @param userId - User's ID (stored as custom claim)
     * @return JWT token string
     */
    @SuppressWarnings("deprecation")
    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey()) // <-- NEW (just the key)
                .compact();
    }
    
    /**
     * Extract username from a JWT token
     * Used to identify which user is making a request
     * 
     * @param token - JWT token string
     * @return Username stored in token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    /**
     * Validate a JWT token
     * Checks if token is properly signed and not expired
     * 
     * @param token - JWT token to validate
     * @return true if valid, false if expired or tampered with
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);  // Throws exception if invalid
            return true;
        } catch (Exception e) {
            // Token expired, invalid signature, or malformed
            return false;
        }
    }
}
