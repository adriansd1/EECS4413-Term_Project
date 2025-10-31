package org.eecs4413.eecs4413term_project.service;

// Removed unused imports for old classes like AuctionClass and BiddingClass
import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * This service handles scheduled tasks, like closing auctions.
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

    /**
     * This is the logic from your closeAuction, notifyWinner, and notifyLosers methods.
     * It's now part of a Service and operates on data from the database.
     */
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