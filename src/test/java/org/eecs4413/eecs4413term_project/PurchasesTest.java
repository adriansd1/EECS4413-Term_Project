package org.eecs4413.eecs4413term_project;

// --- 1. IMPORT THE MODEL CLASSES ---
import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PurchasesTest {
    private User authenticatedUser;
    private User unauthenticatedUser;

    @BeforeEach
    public void setUp() {
        // --- 2. FIX: Use the new User constructor ---
        // User(String username, String password, String firstName, String lastName, String shippingAddress, String email)
        authenticatedUser = new User(
            "alice", 
            "pass123", 
            "Alice", 
            "Smith", 
            "123 Main St, City, Country", 
            "alice@example.com"
        );
        // Manually set as authenticated (constructor defaults to false)
        authenticatedUser.setAuthenticated(true); 

        unauthenticatedUser = new User(
            "bob",
            "pass456",
            "Bob",
            "Jones",
            "456 Elm St, City, Country",
            "bob@example.com"
        );
        // This user is unauthenticated by default, which is correct for the test.
    }

    @Test
    public void testValidPurchaseCreation() {
        Purchases purchase = new Purchases(
                "Laptop", 2, 999.99, authenticatedUser, "1234567812345678", "12/25", "123");
        
        assertNotNull(purchase);
        assertEquals("Laptop", purchase.getItem(), "Item name mismatch");
        assertEquals(2, purchase.getAmount(), "Amount mismatch");
        assertEquals(999.99, purchase.getPrice(), "Price mismatch");
        
        // --- 3. FIX: getUserName() now returns "firstName + lastName" ---
        assertEquals("Alice Smith", purchase.getUserName(), "User name mismatch");
        
        // --- 4. FIX: getShippingAddress() pulls from the User object ---
        assertEquals("123 Main St, City, Country", purchase.getShippingAddress(), "Shipping address mismatch");
        
        assertEquals("1234567812345678", purchase.getCardNumber(), "Card number mismatch");
        assertEquals("12/25", purchase.getCardExpiry(), "Card expiry mismatch");
        assertEquals("123", purchase.getCardCvv(), "Card CVV mismatch");
    }

    @Test
    public void testPurchaseCreationWithUnauthenticatedUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Purchases(
                    "Smartphone", 4, 499.99, unauthenticatedUser, "8765432187654321", "11/24", "456");
        });
        assertEquals("User must be authenticated to make a purchase.", exception.getMessage());
        
        // --- 5. FIX: The hasMadePurchase() method no longer exists ---
        // assertFalse(unauthenticatedUser.hasMadePurchase()); // This line was removed
    }

    @Test
    public void testPurchaseCreationWithInvalidCardDetails() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Purchases(
                    "Tablet", 1, 299.99, authenticatedUser,"invalid_card", "13/25", "789");
        });
        assertEquals("Invalid card details provided.", exception.getMessage());
    }

    @Test
    public void testPurchaseCreationWithNullFields() {
        // This test checks the validEntries() method in your Purchases class
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Purchases(
                    null, -1, 199.99, authenticatedUser, "1234567812345678", "12/25", "123");
        });
        assertEquals("All purchase fields must be valid and non",null, exception.getMessage());
        }
    }