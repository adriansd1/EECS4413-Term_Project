package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.eecs4413.eecs4413term_project.repository.BiddingRepository;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BiddingService {

    private final AuctionRepository auctionRepository;
    private final BiddingRepository bidRepository;
    private final UserRepository userRepository;
    private final CatalogueRepository catalogueRepository;

    public BiddingService(AuctionRepository auctionRepository, BiddingRepository bidRepository, UserRepository userRepository, CatalogueRepository catalogueRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.catalogueRepository = catalogueRepository;
    }

    @Transactional
    public boolean placeBid(Long catalogueId, Long userId, BigDecimal bidAmount) {
        // 1. Fetch Auction
        AuctionClass auction = auctionRepository.findByCatalogueId(catalogueId) 
                .orElseThrow(() -> new RuntimeException("Auction not found!"));
        
        User bidder = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Check Time and Status
        if (auction.isClosed() || LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new RuntimeException("Auction has ended or is closed.");
        }

        BigDecimal currentPrice = auction.getCurrentHighestBid();
        if (currentPrice == null) {
            currentPrice = auction.getStartingPrice();
        }
        
        // --- DUTCH LOGIC (Instant Buy) ---
        if ("DUTCH".equalsIgnoreCase(auction.getAuctionType())) {
            BiddingClass winBid = new BiddingClass(currentPrice, LocalDateTime.now(), bidder, auction);
            bidRepository.save(winBid);
            
            // ✅ FIX: Only close it. DO NOT change the End Time.
            // This ensures it stays visible in the 'Active' list until natural expiration.
            auction.setClosed(true); 
            
            // Set winner so CatalogueService can tell the frontend who won
            auction.setCurrentHighestBidder(bidder); 
            
            auctionRepository.save(auction);
            
            // Sync Catalogue (Required for UI update)
            updateCataloguePrice(catalogueId, currentPrice);
            
            return true;
        }

        // --- FORWARD LOGIC ---
        if (bidAmount.compareTo(currentPrice) > 0) {
            // Save Bid
            BiddingClass newBid = new BiddingClass(bidAmount, LocalDateTime.now(), bidder, auction);
            bidRepository.save(newBid);
            
            // Update Auction Price
            auction.setCurrentHighestBid(bidAmount);
            auction.setCurrentHighestBidder(bidder);
            auctionRepository.save(auction);

            // Sync Catalogue (Required for UI update)
            updateCataloguePrice(catalogueId, bidAmount);

            return true;
        } 
        
        // Final rejection: Bid too low
        throw new RuntimeException("Insufficient bid amount: Must be higher than $" + currentPrice);
    }
    
    /**
     * Helper method to keep Catalogue table price in sync with the auction table.
     */
    private void updateCataloguePrice(Long id, BigDecimal newPrice) {
        // The ID here is the Catalogue ID passed from the frontend.
        Optional<Catalogue> catOpt = catalogueRepository.findById(id);
        if (catOpt.isPresent()) {
            Catalogue c = catOpt.get();
            c.setCurrentBid(newPrice.doubleValue()); 
            // Note: We do NOT change the endTime here either.
            catalogueRepository.save(c);
        }
    }
}