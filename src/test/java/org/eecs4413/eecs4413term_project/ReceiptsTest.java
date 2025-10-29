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
        winner = new User("Victoria", true, "123 Winner St, Win City, WC 12345");
        owner = new User("Oliver", true, "456 Owner Ave, Own Town, OT 67890");
        unauthenticatedUser = new User("UnauthUser", false, "789 Nowhere Rd, No City, NC 00000");
        purchase = new Purchases("Vintage Clock", 150.00, winner, "1111222233334444", "10/26", "321");
    }

    @Test
    public void testReceiptCreation() {
        Receipt receipt = new Receipt(purchase, winner, owner, "Vintage Clock", 4, 150.00, 5);
        assertNotNull(receipt);
        assertEquals(purchase.getItem(), receipt.getAuctionItem());
        assertEquals(purchase.getPrice(), receipt.getFinalPrice());
        assertEquals(winner.getName(), receipt.getWinnerName());
    }

    @Test
    public void testReceiptCreationWithUnauthenticatedUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, unauthenticatedUser, owner, "Vintage Clock", 4, 150.00, 5);
        });
        assertEquals("Both winner and owner must be authenticated users.", exception.getMessage());
        assertFalse(unauthenticatedUser.hasReceivedReceipt());
    }

    @Test
    public void testReceiptCreationWithNullFields() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(null, winner, owner, "Vintage Clock", 4, 150.00, 5);
        });
        assertEquals("All receipt fields must be valid and non-null.", exception.getMessage());
    }

    @Test
    public void testValidEntries() {
        Receipt receipt = new Receipt(purchase, winner, owner, "Vintage Clock", 4, 150.00, 5);
        assertTrue(receipt.validEntries());
    }

    @Test
    public void testInvalidEntries() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, winner, owner, "", 1,-10.00, -1);
        });
        assertEquals("All receipt fields must be valid and non-null.", exception.getMessage());
    }

}
