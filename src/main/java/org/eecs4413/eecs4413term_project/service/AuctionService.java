package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.dto.UploadCatalogueRequest; // ✅ Import DTO
import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.Catalogue; // ✅ Import Catalogue
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository; // ✅ Import Repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Handles scheduled tasks: Closing expired auctions AND Dropping Dutch prices.
 */
@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CatalogueRepository catalogueRepository; // ✅ New Dependency

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CatalogueRepository catalogueRepository) {
        this.auctionRepository = auctionRepository;
        this.catalogueRepository = catalogueRepository;
    }

    // =========================================================
    // 1. ✅ NEW: Start an Auction (Called by "Sell Item" Page)
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

        catalogueRepository.save(item);

        // B. Start the Live Auction (The Event)
        AuctionClass auction = new AuctionClass();
        auction.setItemName(item.getTitle()); // Link by name
        auction.setAuctionType(item.getType());
        auction.setStartingPrice(BigDecimal.valueOf(item.getStartingPrice()));
        auction.setCurrentHighestBid(BigDecimal.valueOf(item.getStartingPrice()));
        auction.setEndTime(endTime);
        auction.setClosed(false);
        // Note: Set other fields like minPrice/decreaseAmount if your AuctionClass supports them

        return auctionRepository.save(auction);
    }

    // =========================================================
    // 2. Scheduled Task: Close Expired Auctions
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
    // 2. ✅ NEW LOGIC: Drop Dutch Auction Prices (Every 60 sec)
    // =========================================================
    @Scheduled(fixedRate = 60000) 
    @Transactional
    public void decreaseDutchAuctionPrices() {
        // Fetch all auctions to check for Dutch ones
        List<AuctionClass> allAuctions = auctionRepository.findAll();

        for (AuctionClass auction : allAuctions) {
            
            // Check: Is it DUTCH? Is it still OPEN?
            // We use "FORWARD" as default, so we check if it equals "DUTCH"
            if ("DUTCH".equalsIgnoreCase(auction.getAuctionType()) && !auction.isClosed()) {

                BigDecimal currentPrice = auction.getCurrentHighestBid();
                BigDecimal decrease = auction.getDecreaseAmount();
                BigDecimal min = auction.getMinPrice();

                // Safety checks to prevent null pointer errors
                if (currentPrice == null || decrease == null || min == null) continue;

                // Calculate the new lower price
                BigDecimal newPrice = currentPrice.subtract(decrease);

                // Stop at the minimum price
                if (newPrice.compareTo(min) < 0) {
                    newPrice = min;
                }

                // If the price changed, save it
                if (newPrice.compareTo(currentPrice) != 0) {
                    auction.setCurrentHighestBid(newPrice);
                    auctionRepository.save(auction);
                    System.out.println("⬇️ Dutch Price Drop: '" + auction.getItemName() + "' dropped to $" + newPrice);
                }
            }
        }
    }

    // =========================================================
    // 3. HELPER: Close Logic
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

    // Helper to keep Catalogue synced (Optional but recommended)
    private void updateCataloguePriceByName(String title, BigDecimal price) {
        // You would need a method in CatalogueRepository: findByTitle(title)
        // This is just a placeholder logic to show where it goes
    }
}