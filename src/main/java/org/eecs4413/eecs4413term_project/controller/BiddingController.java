package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.service.BiddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bids")
public class BiddingController {

    private final BiddingService biddingService;

    public BiddingController(BiddingService biddingService) {
        this.biddingService = biddingService;
    }

    /**
     * DTO for placing a bid.
     */
    static class BidRequest {
        public Long auctionId;
        public Long userId;
        public BigDecimal bidAmount;
    }

    /**
     * POST /api/bids/place
     * Attempts to place a bid using the BiddingService.
     */
    @PostMapping("/place")
    public ResponseEntity<String> placeBid(@RequestBody BidRequest request) {
        try {
            boolean success = biddingService.placeBid(
                    request.auctionId,
                    request.userId,
                    request.bidAmount
            );

            if (success) {
                return ResponseEntity.ok("Bid placed successfully!");
            } else {
                // Bid was too low or auction ended
                return ResponseEntity.badRequest().body("Bid was not high enough or auction is closed.");
            }
        } catch (RuntimeException e) {
            // Handle cases where auctionId or userId are not found
            return ResponseEntity.notFound().build();
        }
    }
}
