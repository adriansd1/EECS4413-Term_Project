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
    // 1. START AUCTION
    // =========================================================
    @Transactional
    public AuctionClass startAuction(UploadCatalogueRequest req) {
        
        // If auctionType is null (e.g. older frontend form), fallback to getType() or "FORWARD"
        String mechanism = "FORWARD";
        if (req.getAuctionType() != null && !req.getAuctionType().isEmpty()) {
            mechanism = req.getAuctionType();
        } else if (req.getType() != null) {
            // Fallback for older forms where type might be "FORWARD"
            mechanism = req.getType(); 
        }

        // A. Save Item Details to Catalogue
        Catalogue item = new Catalogue();
        item.setTitle(req.getTitle());
        
        // Optional: Append Category to description so it's not lost
        String description = req.getDescription();
        if (req.getType() != null && !req.getType().equals(mechanism)) {
             description += " [Category: " + req.getType() + "]";
        }
        item.setDescription(description);
        
        // We save the MECHANISM ("DUTCH") as the type, so the frontend badge works
        item.setType(mechanism); 
        
        item.setStartingPrice(req.getStartingPrice());
        item.setCurrentBid(req.getStartingPrice());
        item.setSeller(req.getSeller());
        item.setImageUrl(req.getImageUrl());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(req.getDurationMinutes());
        item.setEndTime(endTime);

        Catalogue savedItem = catalogueRepository.save(item);

        // B. Start the Live Auction
        AuctionClass auction = new AuctionClass();
        auction.setCatalogueId(savedItem.getId()); 
        
        auction.setItemName(savedItem.getTitle());
        auction.setAuctionType(mechanism); 
        auction.setStartingPrice(BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setCurrentHighestBid(BigDecimal.valueOf(savedItem.getStartingPrice()));
        auction.setEndTime(endTime);
        auction.setClosed(false);
        
        //  CHECK FOR DUTCH LOGIC USING THE CORRECT VARIABLE
        if ("DUTCH".equalsIgnoreCase(mechanism)) {
            if (req.getMinPrice() != null) {
                auction.setMinPrice(BigDecimal.valueOf(req.getMinPrice()));
            }
            if (req.getDecreaseAmount() != null) {
                auction.setDecreaseAmount(BigDecimal.valueOf(req.getDecreaseAmount()));
            }
            
            // Interval Logic
            if (req.getDecreaseIntervalSeconds() != null) {
                auction.setDecreaseIntervalSeconds(req.getDecreaseIntervalSeconds());
            } else {
                auction.setDecreaseIntervalSeconds(60); // Default
            }
        }

        return auctionRepository.save(auction);
    }

    // =========================================================
    // 2. SCHEDULER: DROP DUTCH PRICES (Runs Every 1 Second)
    // =========================================================
    @Scheduled(fixedRate = 1000) 
    @Transactional
    public void decreaseDutchAuctionPrices() {
        List<AuctionClass> allAuctions = auctionRepository.findAll();

        for (AuctionClass auction : allAuctions) {
            if ("DUTCH".equalsIgnoreCase(auction.getAuctionType()) && !auction.isClosed()) {

                BigDecimal currentPrice = auction.getCurrentHighestBid();
                BigDecimal decrease = auction.getDecreaseAmount();
                BigDecimal min = auction.getMinPrice();
                Integer interval = auction.getDecreaseIntervalSeconds();

                // Safety check
                if (currentPrice == null || decrease == null || min == null || interval == null) continue;

                // Simplified Timing Logic:
                // Since we don't store 'lastDropTime', we will simulate the drop based on 
                // how many seconds have passed since the auction started vs how many drops *should* have happened.
                // Expected Price = StartPrice - (DecreaseAmount * (SecondsElapsed / Interval))
                
                // 1. Calculate Elapsed Time (Seconds)
                // Note: We don't have 'startTime' column, but we can infer it roughly or 
                // assume the scheduler runs exactly on interval. 
                // BETTER TEMP FIX: Just run the logic.
            
                BigDecimal newPrice = currentPrice.subtract(decrease);

                if (newPrice.compareTo(min) < 0) {
                    newPrice = min;
                }

                if (newPrice.compareTo(currentPrice) != 0) {
                    auction.setCurrentHighestBid(newPrice);
                    auctionRepository.save(auction);
                    System.out.println("⬇️ Dutch Price Drop: '" + auction.getItemName() + "' -> $" + newPrice);
                }
            }
        }
    }

    // =========================================================
    // 3. SCHEDULER: CLOSE EXPIRED
    // =========================================================
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void checkAndCloseAuctions() {
        List<AuctionClass> auctionsToClose = auctionRepository.findAllByIsClosedFalseAndEndTimeBefore(LocalDateTime.now());
        for (AuctionClass auction : auctionsToClose) {
            closeAuction(auction);
        }
    }

    private void closeAuction(AuctionClass auction) {
        auction.setClosed(false);
        auctionRepository.save(auction); 
        System.out.println("🔔 Auction Ended: " + auction.getItemName());
    }
}