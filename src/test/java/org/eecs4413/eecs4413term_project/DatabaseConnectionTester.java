package org.eecs4413.eecs4413term_project; // Use your main application package

import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DatabaseConnectionTester implements CommandLineRunner {

    private final UserRepository userRepository;

    // Inject the UserRepository (Dependency Injection)
    public DatabaseConnectionTester(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- 🚀 Testing PostgreSQL Connection ---");
        
        // 1. CREATE: Create a new User object using the correct constructor
        // We use the new User(name, userName, password) constructor
        User newUser = new User("TestUser1", "testuser1_username", "apassword123");

        // (Optional) Set the optional address if you want
        newUser.setAddress("123 Main St");

        // 2. SAVE: Persist the user to the database (INSERT operation)
        User savedUser = userRepository.save(newUser);
        System.out.println("✅ Successfully saved user with ID: " + savedUser.getId());
        
        // 3. READ: Retrieve all users from the database (SELECT operation)
        List<User> users = userRepository.findAll();
        System.out.println("🔍 Total users found in DB: " + users.size());

        // 4. VERIFY: Check that the user's *name* was saved
        if (users.stream().anyMatch(u -> u.getName().equals("TestUser1"))) {
            System.out.println("✅ Connection Test SUCCESSFUL! Data was saved and retrieved.");
        } else {
             System.out.println("❌ Connection Test FAILED. Check database logs.");
        }
        System.out.println("-------------------------------------------\n");
    }
}