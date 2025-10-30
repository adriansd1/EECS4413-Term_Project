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
        winner = new User("Victoria", "Victor123", "securePass!@#");
        winner.authenticate();
        winner.setAddress("123 Winner Ave, City, Country");
        owner = new User("Oliver", "Owner456", "ownerPass!@#");
        owner.authenticate();
        owner.setAddress("789 Owner St, City, Country");
        unauthenticatedUser = new User("UnauthUser", "Unauth123", "unauthPass!@#");
        unauthenticatedUser.setAddress("456 Other Rd, Town");
        purchase = new Purchases("Vintage Clock", 3, 150.00, winner, "1111222233334444", "10/26", "321");
    }

    @Test
    public void testReceiptCreation() {
        Receipt receipt = new Receipt(purchase, owner, 5);
        assertNotNull(receipt);
        assertEquals(purchase.getItem(), receipt.getAuctionItem());
        assertEquals(purchase.getPrice() * purchase.getAmount(), receipt.getFinalPrice());
        assertEquals(winner.getName(), receipt.getWinnerName());
    }

    @Test
    public void testReceiptCreationWithUnauthenticatedUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, unauthenticatedUser, 5);
        });
        assertEquals("Owner must be an authenticated user.", exception.getMessage());
        assertFalse(unauthenticatedUser.hasReceivedReceipt());
    }

    @Test
    public void testReceiptCreationWithNullFields() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(null, owner, 5);
        });
        assertEquals("All receipt fields must be valid and non-null.", exception.getMessage());
    }

    @Test
    public void testValidEntries() {
        Receipt receipt = new Receipt(purchase, owner, 5);
        assertTrue(receipt.validEntries());
    }

    @Test
    public void testInvalidEntries() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Receipt(purchase, owner, -1);
        });
        assertEquals("All receipt fields must be valid and non-null.", exception.getMessage());
    }

}
