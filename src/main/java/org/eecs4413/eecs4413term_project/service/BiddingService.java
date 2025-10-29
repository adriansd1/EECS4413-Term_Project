package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.eecs4413.eecs4413term_project.repository.BiddingRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BiddingService {

    private final AuctionRepository auctionRepository;
    private final BiddingRepository bidRepository;
    private final UserRepository userRepository;


    // Spring injects the repositories
    public BiddingService(AuctionRepository auctionRepository, 
                          BiddingRepository bidRepository, 
                          UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    /**
     * This is the core logic to place a bid.
     * It saves the bid to the database and updates the auction.
     */
    @Transactional // Ensures this operation is all-or-nothing
    public boolean placeBid(Long auctionId, Long userId, BigDecimal bidAmount) {
        // 1. Fetch entities from SQL
        AuctionClass auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found!"));
        
        User bidder = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            System.out.println("❌ Auction has ended.");
            return false;
        }

        if (bidAmount.compareTo(auction.getCurrentHighestBid()) > 0) {
            // 3. Create and SAVE the new Bid
            BiddingClass newBid = new BiddingClass(bidAmount, LocalDateTime.now(), bidder, auction);
            bidRepository.save(newBid);

            // 4. Update and SAVE the Auction
            auction.setCurrentHighestBid(bidAmount);
            auction.setCurrentHighestBidder(bidder);
            auctionRepository.save(auction);

            System.out.println("💰 " + bidder.getName() + " placed a bid of $" + bidAmount);
            return true;
        } else {
            System.out.println("❌ Bid too low. Current highest: $" + auction.getCurrentHighestBid());
            return false;
        }
    }
}
