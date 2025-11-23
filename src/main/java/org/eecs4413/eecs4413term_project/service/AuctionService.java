package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Handles scheduled tasks: Closing expired auctions AND Dropping Dutch prices.
 */
@Service
// @EnableScheduling is already on your main application, so it's not needed here
public class AuctionService {

    // 1. Make the repository 'final'. It will be initialized by the constructor.
    private final AuctionRepository auctionRepository;

    // 2. This is the CORRECT CONSTRUCTOR.
    // - It is public.
    // - It has no return type (no 'void').
    // - It matches the class name 'AuctionService'.
    // - @Autowired tells Spring to inject the repository.
    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }
    @Scheduled(fixedRate = 30000) // Runs every 30 seconds (30,000 milliseconds)
    @Transactional
    public void checkAndCloseAuctions() {
        System.out.println("Scheduler: Checking for auctions to close...");
        
        // 1. Find auctions in the SQL database that need to be closed
        List<AuctionClass> auctionsToClose = auctionRepository.findAllByIsClosedFalseAndEndTimeBefore(LocalDateTime.now());

        if (auctionsToClose.isEmpty()) {
            return; // Nothing to do
        }

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
        // This save() updates the is_closed column in the database
        auctionRepository.save(auction); 

        System.out.println("\n🔔 Auction for '" + auction.getItemName() + "' has ended!");

        User winner = auction.getCurrentHighestBidder();

        if (winner != null) {
            // 2. Notify Winner (from your notifyWinner method)
            System.out.println("🏆 Congratulations " + winner.getName() + 
                               "! You won the auction for " + auction.getItemName() + 
                               " with a bid of $" + auction.getCurrentHighestBid());
            
            // 3. Notify Losers (from your notifyLosers method)
            Set<BiddingClass> allBids = auction.getBids();
                for (BiddingClass bid : allBids) {
                    User bidder = bid.getUser();
                    if (!bidder.getId().equals(winner.getId())) {
                    System.out.println("📨 " + bidder.getName() + 
                                       ", the auction for " + auction.getItemName() + 
                                       " has ended. You did not win.");
                }
            }
        } else {
            System.out.println("No bids were placed for " + auction.getItemName() + ".");
        }
        System.out.println("---------------------------------------\n");
    }
}