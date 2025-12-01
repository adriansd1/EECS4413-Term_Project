package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.repository.BiddingRepository;
import org.eecs4413.eecs4413term_project.service.BiddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bids")
public class BiddingController {

    private final BiddingService biddingService;
    private final BiddingRepository biddingRepository; 

    public BiddingController(BiddingService biddingService, BiddingRepository biddingRepository) {
        this.biddingService = biddingService;
        this.biddingRepository = biddingRepository;
    }



    static class BidRequest {
        public Long auctionId;
        public Long userId;
        public BigDecimal bidAmount;
    }

    static class BidDTO {
        public Long id;
        public BigDecimal bidAmount;
        public LocalDateTime bidTime;
        public Long userId;
        public String username;
        public Long auctionId;

        public BidDTO(BiddingClass bid) {
            this.id = bid.getId();
            this.bidAmount = bid.getBidAmount();
            this.bidTime = bid.getBidTime();
            this.userId = bid.getUser().getId();
            this.username = bid.getUser().getUsername(); 
            this.auctionId = bid.getAuction().getId();
        }
    }


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
                return ResponseEntity.badRequest().body("Bid rejected.");
            }
        } catch (Exception e) {
            // Get the message
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/bids
     * Gets all bids.
     *
     * GET /api/bids?auctionId={id}
     * Gets all bids for a specific auction.
     * * GET /api/bids?userId={id}
     * Gets all bids for a specific user.
     */
    @GetMapping
    public List<BidDTO> getBids(
            @RequestParam(name = "auctionId", required = false) Long auctionId,
            @RequestParam(name = "userId", required = false) Long userId) { 
        
        List<BiddingClass> bids;
        
        if (auctionId != null) {
            // Priority 1: Check bids by auction
            bids = biddingRepository.findByAuctionIdOrderByBidTimeDesc(auctionId);
        } else if (userId != null) {
            // Priority 2: Check bids by user
            bids = biddingRepository.findByUserIdOrderByBidTimeDesc(userId);
        } else {
            // Default: Get all bids
            bids = biddingRepository.findAll();
        }
        
        // Convert entities to DTOs before returning
        return bids.stream().map(BidDTO::new).collect(Collectors.toList());
    }

    // --- getBidById method remains the same ---

    @GetMapping("/{id}")
    public ResponseEntity<BidDTO> getBidById(@PathVariable Long id) {
        return biddingRepository.findById(id)
                .map(bid -> ResponseEntity.ok(new BidDTO(bid)))
                .orElse(ResponseEntity.notFound().build());
    }
}