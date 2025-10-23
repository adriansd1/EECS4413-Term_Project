package org.java.main.org.eecs4413.eecs4413term_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {

    private User alice;
    private User bob;

    @BeforeEach
    void setup() {
        alice = new User("Alice", true);
        bob = new User("Bob", true);
    }

    @Test
    void testAuctionAcceptsValidBids() {
        Auction auction = new Auction("Painting", new BigDecimal("100.00"), LocalDateTime.now().plusSeconds(3));

        boolean result1 = auction.placeBid(alice, new BigDecimal("120.00"));
        boolean result2 = auction.placeBid(bob, new BigDecimal("150.00"));

        assertTrue(result1, "First valid bid should be accepted.");
        assertTrue(result2, "Higher bid should be accepted.");
        assertEquals(bob.getName(), auction.getCurrentHighestBidder().getName());
        assertEquals(new BigDecimal("150.00"), auction.getCurrentHighestBid());
    }

    @Test
    void testAuctionRejectsUnauthenticatedUser() {
        User unauthenticated = new User("Eve", false);
        Auction auction = new Auction("Laptop", new BigDecimal("200.00"), LocalDateTime.now().plusSeconds(2));

        boolean result = auction.placeBid(unauthenticated, new BigDecimal("250.00"));

        assertFalse(result, "Unauthenticated user should not be allowed to bid.");
        assertNull(auction.getCurrentHighestBidder(), "No highest bidder should be set.");
        assertEquals(new BigDecimal("200.00"), auction.getCurrentHighestBid(), "Highest bid remains unchanged.");
    }

    @Test
    void testAuctionClosesAutomatically() throws InterruptedException {
        Auction auction = new Auction("Watch", new BigDecimal("300.00"), LocalDateTime.now().plusSeconds(2));

        auction.placeBid(alice, new BigDecimal("350.00"));
        auction.placeBid(bob, new BigDecimal("400.00"));

        // Wait for the timer to close the auction
        Thread.sleep(2500);

        assertTrue(auction.isClosed(), "Auction should automatically close after its end time.");
        assertEquals(bob.getName(), auction.getCurrentHighestBidder().getName(), "Highest bidder should be the winner.");
    }

    @Test
    void testAuctionRejectsBidAfterClosing() throws InterruptedException {
        Auction auction = new Auction("Camera", new BigDecimal("500.00"), LocalDateTime.now().plusSeconds(1));
        auction.placeBid(alice, new BigDecimal("600.00"));

        // Wait until the auction closes
        Thread.sleep(1500);

        boolean result = auction.placeBid(bob, new BigDecimal("700.00"));
        assertFalse(result, "Bids after auction close should be rejected.");
    }
}
