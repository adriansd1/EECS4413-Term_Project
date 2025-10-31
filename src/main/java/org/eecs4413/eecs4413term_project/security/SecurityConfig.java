package org.eecs4413.eecs4413term_project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <-- 1. ADD THIS IMPORT
import org.springframework.security.crypto.password.PasswordEncoder; // <-- 2. ADD THIS IMPORT

@Configuration
public class SecurityConfig {

    // --- 3. ADD THIS BEAN ---
    /**
     * Creates the password encoder bean.
     * This tells Spring to use BCrypt for password hashing and validation.
     * Now you can @Autowire PasswordEncoder in any other class (like your AuthService).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (useful for API-only backends and tests)
                .csrf(csrf -> csrf.disable())

                // Allow all requests (no authentication required)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}