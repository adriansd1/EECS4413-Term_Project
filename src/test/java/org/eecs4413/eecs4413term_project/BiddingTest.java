package org.eecs4413.eecs4413term_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BiddingAuthTest {

    private BiddingClass auction;
    private User authenticatedUser;
    private User unauthenticatedUser;

    @BeforeEach
    void setUp() {
        auction = new BiddingClass("Antique Vase", new BigDecimal("100.00"));
        authenticatedUser = new User("Alice", true);
        unauthenticatedUser = new User("Bob", false);
    }

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
        assertEquals(authenticatedUser.getName(), auction.getCurrentHighestBidder().getName());
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
        assertEquals(unauthenticatedUser.getName(), auction.getCurrentHighestBidder().getName());
        assertEquals(new BigDecimal("130.00"), auction.getCurrentHighestBid());
    }
    @Test
    void testValidBid() {
        boolean result = bidding.placeBid("Bob", 150.0);
        assertTrue(result, "Bid should be accepted when higher than current highest.");
        assertEquals(150.0, bidding.getCurrentBid());
        assertEquals("Bob", bidding.getHighestBidder());
    }

    @Test
    void testInvalidBid() {
        boolean result = bidding.placeBid("Charlie", 90.0);
        assertFalse(result, "Bid should be rejected when lower than current highest.");
        assertEquals(100.0, bidding.getCurrentBid());
        assertEquals("Alice", bidding.getHighestBidder());
    }
}

