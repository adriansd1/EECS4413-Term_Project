package org.eecs4413.eecs4413term_project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.model.Receipt;
import org.eecs4413.eecs4413term_project.model.Purchases;

public class ReceiptsTest {
    User winner;
    User owner;
    User unauthenticatedUser;
    Purchases purchase;
    
    @BeforeEach
    public void setUp() {
        // --- 1. FIX: Use the new User constructor ---
        // User(username, password, firstName, lastName, shippingAddress, email)
        winner = new User(
            "vic_winner",
            "pass123",
            "Victoria",
            "Winner",
            "123 Winner St, Win City, WC 12345",
            "victoria@winner.com"
        );
        winner.setAuthenticated(true); // Manually authenticate for the test

        owner = new User(
            "oliver_owner",
            "pass456",
            "Oliver",
            "Owner",
            "456 Owner Ave, Own Town, OT 67890",
            "oliver@owner.com"
        );
        owner.setAuthenticated(true); // Manually authenticate for the test

        unauthenticatedUser = new User(
            "unauth",
            "pass789",
            "Unauth",
            "User",
            "789 Nowhere Rd, No City, NC 00000",
            "unauth@example.com"
        );
        // This user is unauthenticated by default, which is correct.

        // This line now works because 'winner' is a valid User object
        purchase = new Purchases("Vintage Clock", 3, 150.00, winner, "1111222233334444", "10/26", "321");
    }

    @Test
    public void testReceiptCreation() {
        Receipt receipt = new Receipt(purchase, owner, 5);
        assertNotNull(receipt);
        assertEquals(purchase.getItem(), receipt.getAuctionItem());
        assertEquals(purchase.getPrice() * purchase.getAmount(), receipt.getFinalPrice());
        
        // --- 2. FIX: Test against the new convenience getter methods ---
        assertEquals("Victoria Winner", receipt.getWinnerName());
        assertEquals("Oliver Owner", receipt.getOwnerName());
    }

    @Test
    public void testReceiptCreationWithUnauthenticatedUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, unauthenticatedUser, 5);
        });
        assertEquals("Owner must be an authenticated user.", exception.getMessage());

        // --- 3. FIX: Removed non-existent method call ---
        // assertFalse(unauthenticatedUser.hasReceivedReceipt()); // This method does not exist
    }

    @Test
    public void testReceiptCreationWithNullFields() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(null, owner, 5);
        });
        
        // --- 4. FIX: Update exception message to match new constructor logic ---
        assertEquals("Purchase and its user (the winner) cannot be null.", exception.getMessage());
    }

    @Test
    public void testValidEntries() {
        Receipt receipt = new Receipt(purchase, owner, 5);
        assertTrue(receipt.validEntries());
    }

    @Test
    public void testInvalidEntries() {
        // This test checks for shippingDays < 0
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, owner, -1);
        });
        
        // --- 5. FIX: Update exception message to match new constructor logic ---
        // The check for shippingDays happens in validEntries(), which throws this specific message.
        assertEquals("All receipt fields must be valid and non-null.", exception.getMessage());
    }
}