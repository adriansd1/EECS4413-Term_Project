package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;

    public AuctionController(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    /**
     * DTO for creating an auction.
     */
    static class AuctionCreateRequest {
        public String itemName;
        public BigDecimal startingPrice;
        public int durationInMinutes; // Simple way to set end time
    }

    /**
     * POST /api/auctions
     * Creates a new Auction.
     */
    @PostMapping
    public AuctionClass createAuction(@RequestBody AuctionCreateRequest request) {
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(request.durationInMinutes);
        AuctionClass newAuction = new AuctionClass(request.itemName, request.startingPrice, endTime);
        return auctionRepository.save(newAuction);
    }

    /**
     * GET /api/auctions
     * Gets all auctions.
     */
    @GetMapping
    public List<AuctionClass> getAllAuctions() {
        return auctionRepository.findAll();
    }

    /**
     * GET /api/auctions/{id}
     * Gets a single auction by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuctionClass> getAuctionById(@PathVariable Long id) {
        return auctionRepository.findById(id)
                .map(ResponseEntity::ok) // If found, return 200 OK
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404
    }
}
