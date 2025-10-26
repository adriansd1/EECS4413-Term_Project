package org.eecs4413.eecs4413term_project;

import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BiddingAuthTest {

    private BiddingClass auction;
    private User authenticatedUser;
    private User unauthenticatedUser;
    private User initialBidder; // Added for the new tests

    @BeforeEach
    void setUp() {
        // Initialize the auction with an item and starting price
        auction = new BiddingClass("Antique Vase", new BigDecimal("100.00"));

        // Initialize users
        authenticatedUser = new User("Alice", true);
        unauthenticatedUser = new User("Bob", false);
        
        // Use a third user for the initial high bid in the last two tests
        initialBidder = new User("Charlie", true);
        
        // Place an initial bid to establish a highest bidder/bid (for invalid bid test)
        // Note: The @BeforeEach runs before EVERY test, so we place this here for
        // consistent setup for the final two tests, but ensure the first two tests 
        // don't rely on it (which they don't, as they test initial state).
    }

    // --- Authentication Tests (Your original, correct tests) ---

    @Test
    void testUnauthenticatedUserCannotBid() {
        boolean result = auction.placeBid(unauthenticatedUser, new BigDecimal("120.00"));
        assertFalse(result, "Unauthenticated users should not be allowed to bid.");
        assertNull(auction.getCurrentHighestBidder(), "No bidder should be recorded for unauthenticated bid.");
        assertEquals(new BigDecimal("100.00"), auction.getCurrentHighestBid(), "Highest bid should remain unchanged.");
    }

    @Test
    void testAuthenticatedUserCanBid() {
        boolean result = auction.placeBid(authenticatedUser, new BigDecimal("150.00"));
        assertTrue(result, "Authenticated users should be allowed to place a valid bid.");
        assertEquals(authenticatedUser, auction.getCurrentHighestBidder()); // Compare User objects directly
        assertEquals(new BigDecimal("150.00"), auction.getCurrentHighestBid());
    }

    @Test
    void testUserCanBidAfterAuthentication() {
        // Initially unauthenticated → should fail
        boolean firstAttempt = auction.placeBid(unauthenticatedUser, new BigDecimal("120.00"));
        assertFalse(firstAttempt);

        // Now authenticate the user
        unauthenticatedUser.authenticate();

        // Try again → should succeed
        boolean secondAttempt = auction.placeBid(unauthenticatedUser, new BigDecimal("130.00"));
        assertTrue(secondAttempt);
        assertEquals(unauthenticatedUser, auction.getCurrentHighestBidder());
        assertEquals(new BigDecimal("130.00"), auction.getCurrentHighestBid());
    }

    // --- Corrected Bid Logic Tests ---

    @Test
    void testValidBidAccepted() {
        // Arrange: Place an initial bid
        auction.placeBid(initialBidder, new BigDecimal("150.00"));
        
        // Act: A higher bid from a new authenticated user (Alice)
        boolean result = auction.placeBid(authenticatedUser, new BigDecimal("175.50"));
        
        // Assert
        assertTrue(result, "Bid should be accepted when higher than current highest.");
        // Use BigDecimal.compareTo() for robust comparison or compare string value
        assertEquals(new BigDecimal("175.50"), auction.getCurrentHighestBid());
        assertEquals(authenticatedUser, auction.getCurrentHighestBidder());
    }

    @Test
    void testInvalidBidRejected() {
        // Arrange: Place an initial bid
        auction.placeBid(initialBidder, new BigDecimal("150.00"));

        // Act: A lower bid from an authenticated user (Alice)
        boolean result = auction.placeBid(authenticatedUser, new BigDecimal("120.00"));
        
        // Assert
        assertFalse(result, "Bid should be rejected when lower than current highest.");
        // Highest bid should be the initial bid
        assertEquals(new BigDecimal("150.00"), auction.getCurrentHighestBid());
        // Highest bidder should be the initial bidder
        assertEquals(initialBidder, auction.getCurrentHighestBidder());
    }
}