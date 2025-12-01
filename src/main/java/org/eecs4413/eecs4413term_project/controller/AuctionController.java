package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.dto.UploadCatalogueRequest;
import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    //  Inject the service
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * POST /api/auctions/create
     * Starts an auction AND adds it to the catalogue.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAuction(@RequestBody UploadCatalogueRequest req) {
        try {
            // Call the service to save to BOTH tables (Catalogue & Auction)
            AuctionClass newAuction = auctionService.startAuction(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAuction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to start auction: " + e.getMessage());
        }
    }
}