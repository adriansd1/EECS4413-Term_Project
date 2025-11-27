package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.eecs4413.eecs4413term_project.model.Catalogue; // ✅ Import Catalogue
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;
import org.eecs4413.eecs4413term_project.repository.BiddingRepository;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository; // ✅ Import Repo
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BiddingService {

    private final AuctionRepository auctionRepository;
    private final BiddingRepository bidRepository;
    private final UserRepository userRepository;
    private final CatalogueRepository catalogueRepository; // ✅ New Dependency

    public BiddingService(AuctionRepository auctionRepository, 
                          BiddingRepository bidRepository, 
                          UserRepository userRepository,
                          CatalogueRepository catalogueRepository) { // ✅ Inject it
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.catalogueRepository = catalogueRepository;
    }

    @Transactional
    public boolean placeBid(Long auctionId, Long userId, BigDecimal bidAmount) {
        // 1. Fetch Auction
        AuctionClass auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found!"));
        
        User bidder = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            return false;
        }

        BigDecimal currentPrice = auction.getCurrentHighestBid();
        
        // --- DUTCH LOGIC ---
        if ("DUTCH".equalsIgnoreCase(auction.getAuctionType())) {
            BiddingClass winBid = new BiddingClass(currentPrice, LocalDateTime.now(), bidder, auction);
            bidRepository.save(winBid);
            
            // Close Auctiona
            auction.setClosed(true);
            auctionRepository.save(auction);
            
            // ✅ SYNC CATALOGUE (So the UI updates)
            updateCataloguePrice(auctionId, currentPrice);
            
            return true;
        }

        // --- FORWARD LOGIC ---
        if (bidAmount.compareTo(currentPrice) > 0) {
            BiddingClass newBid = new BiddingClass(bidAmount, LocalDateTime.now(), bidder, auction);
            bidRepository.save(newBid);
            
            // Update Auction Price
            auction.setCurrentHighestBid(bidAmount);
            auctionRepository.save(auction);

            // ✅ SYNC CATALOGUE (So the UI updates)
            updateCataloguePrice(auctionId, bidAmount);

            return true;
        } 
        
        return false;
    }

    // ✅ Helper method to keep Catalogue table in sync
    private void updateCataloguePrice(Long id, BigDecimal newPrice) {
        Optional<Catalogue> catOpt = catalogueRepository.findById(id);
        if (catOpt.isPresent()) {
            Catalogue c = catOpt.get();
            c.setCurrentBid(newPrice.doubleValue()); // Assuming Catalogue uses Double
            catalogueRepository.save(c);
        }
    }
}