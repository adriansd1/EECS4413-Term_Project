package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController // Marks this class as a request handler
@RequestMapping("/api/auction") // Base path for all endpoints in this controller
public class BiddingController {

    // 💡 Simple in-memory storage for testing:
    private final BiddingClass antiqueVaseAuction;
    private final Map<String, User> userDatabase = new HashMap<>();

    public BiddingController() {
        // Initialize the auction when the controller is created
        this.antiqueVaseAuction = new BiddingClass("Antique Vase", new BigDecimal("100.00"));

        // Initialize a few users in our "database"
        userDatabase.put("alice", new User("Alice", true));     // Authenticated
        userDatabase.put("bob", new User("Bob", false));       // Unauthenticated
        userDatabase.put("charlie", new User("Charlie", true)); // Authenticated
    }

    // --- DTO (Data Transfer Object) for incoming bid data ---
    private static class BidRequest {
        public String username;
        public double bidAmount; // Use double for simple JSON parsing, then convert to BigDecimal
    }

    /**
     * Endpoint to place a new bid.
     * Maps to: POST /api/auction/bid
     */
    @PostMapping("/bid")
    public ResponseEntity<String> placeBid(@RequestBody BidRequest request) {
        // 1. Find User
        User bidder = userDatabase.get(request.username.toLowerCase());
        if (bidder == null) {
            return new ResponseEntity<>("User '" + request.username + "' not found.", HttpStatus.NOT_FOUND);
        }

        // 2. Convert double to BigDecimal
        BigDecimal bidAmount = new BigDecimal(String.valueOf(request.bidAmount));

        // 3. Execute core logic
        boolean success = antiqueVaseAuction.placeBid(bidder, bidAmount);

        // 4. Return appropriate response
        if (success) {
            return new ResponseEntity<>(
                "Bid accepted! New high bid: $" + antiqueVaseAuction.getCurrentHighestBid(), 
                HttpStatus.OK
            );
        } else {
            String message = bidder.isAuthenticated() 
                ? "Bid rejected. Too low. Current high: $" + antiqueVaseAuction.getCurrentHighestBid()
                : bidder.getName() + " is not authenticated.";
                
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to check the current status.
     * Maps to: GET /api/auction/status
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("item", antiqueVaseAuction.getItemName());
        status.put("currentHighestBid", antiqueVaseAuction.getCurrentHighestBid());
        status.put("highestBidder", 
            antiqueVaseAuction.getCurrentHighestBidder() != null ? 
            antiqueVaseAuction.getCurrentHighestBidder().getName() : "None");
        return status;
    }
}