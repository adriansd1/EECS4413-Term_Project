package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.dto.UploadCatalogueRequest; 
import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CatalogueRepository catalogueRepository; 

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CatalogueRepository catalogueRepository) {
        this.auctionRepository = auctionRepository;
        this.catalogueRepository = catalogueRepository;
    }

    // =========================================================
    // 1. ✅ NEW: Start an Auction (The Fix for the data gap)
    // =========================================================
    @Transactional
    public AuctionClass startAuction(UploadCatalogueRequest req) {
        
        // A. Save Item Details to Catalogue (The Library)
        Catalogue item = new Catalogue();
        item.setTitle(req.getTitle());
        item.setDescription(req.getDescription());
        item.setType(req.getType());
        item.setStartingPrice(req.getStartingPrice());
        item.setCurrentBid(req.getStartingPrice());
        item.setSeller(req.getSeller());
        item.setImageUrl(req.getImageUrl());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(req.getDurationMinutes());
        item.setEndTime(endTime);

        // 1. Save Catalogue FIRST to generate the ID
        Catalogue savedItem = catalogueRepository.save(item); 

        // 2. B. Start the Live Auction (The Event)
        AuctionClass auction = new AuctionClass();
        
        // ✅ CRITICAL FIX: Link the Auction to the Catalogue ID
        auction.setCatalogueId(savedItem.getId()); 
        
        auction.setItemName(savedItem.getTitle());
        auction.setAuctionType(savedItem.getType());
        auction.setStartingPrice(BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setCurrentHighestBid(BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setEndTime(endTime);
        auction.setClosed(false);
        
        // Set Dutch-specific fields (assuming they exist in UploadCatalogueRequest/AuctionClass)
        // If your DTO has minPrice/decreaseAmount, they should be mapped here
        // Example: 
        // auction.setMinPrice(req.getMinPrice());

        return auctionRepository.save(auction);
    }

    // =========================================================
    // 2. Scheduled Task: Close Expired Auctions (Your Existing Logic)
    // =========================================================
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void checkAndCloseAuctions() {
        System.out.println("Scheduler: Checking for auctions to close...");
        List<AuctionClass> auctionsToClose = auctionRepository.findAllByIsClosedFalseAndEndTimeBefore(LocalDateTime.now());

        if (auctionsToClose.isEmpty()) return;

        System.out.println("Scheduler: Found " + auctionsToClose.size() + " auctions to close.");
        for (AuctionClass auction : auctionsToClose) {
            closeAuction(auction);
        }
    }

    // =========================================================
    // 3. Scheduled Task: Drop Dutch Auction Prices (Your Existing Logic)
    // =========================================================
    @Scheduled(fixedRate = 60000) 
    @Transactional
    public void decreaseDutchAuctionPrices() {
        List<AuctionClass> allAuctions = auctionRepository.findAll();

        for (AuctionClass auction : allAuctions) {
            if ("DUTCH".equalsIgnoreCase(auction.getAuctionType()) && !auction.isClosed()) {

                BigDecimal currentPrice = auction.getCurrentHighestBid();
                BigDecimal decrease = auction.getDecreaseAmount();
                BigDecimal min = auction.getMinPrice();

                if (currentPrice == null || decrease == null || min == null) continue;

                BigDecimal newPrice = currentPrice.subtract(decrease);

                if (newPrice.compareTo(min) < 0) {
                    newPrice = min;
                }

                if (newPrice.compareTo(currentPrice) != 0) {
                    auction.setCurrentHighestBid(newPrice);
                    auctionRepository.save(auction);
                    
                    // Note: Update catalogue price sync logic is missing here, but outside the scope of this fix.
                    
                    System.out.println("⬇️ Dutch Price Drop: '" + auction.getItemName() + "' dropped to $" + newPrice);
                }
            }
        }
    }

    // =========================================================
    // 4. HELPER: Close Logic (Your Existing Logic)
    // =========================================================
    private void closeAuction(AuctionClass auction) {
        auction.setClosed(true);
        auctionRepository.save(auction); 

        System.out.println("\n🔔 Auction for '" + auction.getItemName() + "' has ended!");

        User winner = auction.getCurrentHighestBidder();

        if (winner != null) {
            System.out.println("🏆 Winner: " + winner.getName() + " for $" + auction.getCurrentHighestBid());
            
            Set<BiddingClass> allBids = auction.getBids();
            if (allBids != null) {
                for (BiddingClass bid : allBids) {
                    User bidder = bid.getUser();
                    if (!bidder.getId().equals(winner.getId())) {
                        System.out.println("📨 Notification to loser: " + bidder.getName());
                    }
                }
            }
        } else {
            System.out.println("No bids were placed for " + auction.getItemName() + ".");
        }
        System.out.println("---------------------------------------\n");
    }
}