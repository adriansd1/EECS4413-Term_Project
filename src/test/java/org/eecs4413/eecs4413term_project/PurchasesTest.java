package org.eecs4413.eecs4413term_project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PurchasesTest {
    private org.eecs4413.eecs4413term_project.model.User authenticatedUser;
    private org.eecs4413.eecs4413term_project.model.User unauthenticatedUser;

    @BeforeEach
    public void setUp() {
        authenticatedUser = new org.eecs4413.eecs4413term_project.model.User("Alice", true, "123 Main St, City, Country");
        unauthenticatedUser = new org.eecs4413.eecs4413term_project.model.User("Bob", false, "456 Elm St, City, Country");
    }

    @Test
    public void testValidPurchaseCreation() {
        org.eecs4413.eecs4413term_project.model.Purchases purchase = new org.eecs4413.eecs4413term_project.model.Purchases(
                "Laptop", 999.99, authenticatedUser, "1234567812345678", "12/25", "123");
        assertNotNull(purchase);
        assertEquals("Laptop", purchase.getItem());
        assertEquals(999.99, purchase.getPrice());
        assertEquals("Alice", purchase.getUserName());
        assertEquals("123 Main St, City, Country", purchase.getShippingAddress());
        assertEquals("1234567812345678", purchase.getCardNumber());
        assertEquals("12/25", purchase.getCardExpiry());
        assertEquals("123", purchase.getCardCvv());
    }

    @Test
    public void testPurchaseCreationWithUnauthenticatedUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new org.eecs4413.eecs4413term_project.model.Purchases(
                    "Smartphone", 499.99, unauthenticatedUser, "8765432187654321", "11/24", "456");
        });
        assertEquals("User must be authenticated to make a purchase.", exception.getMessage());
        assertFalse(unauthenticatedUser.hasMadePurchase());
    }

    @Test
    public void testPurchaseCreationWithInvalidCardDetails() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new org.eecs4413.eecs4413term_project.model.Purchases(
                    "Tablet", 299.99, authenticatedUser,"invalid_card", "13/25", "789");
        });
        assertEquals("Invalid card details provided.", exception.getMessage());
    }

    @Test
    public void testPurchaseCreationWithNullFields() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new org.eecs4413.eecs4413term_project.model.Purchases(
                    null, 199.99, authenticatedUser, "1234567812345678", "12/25", "123");
        });
        assertEquals("All purchase fields must be valid and non-null.", exception.getMessage());
    }
}
