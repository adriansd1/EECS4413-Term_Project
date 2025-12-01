package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.Receipt;
import org.eecs4413.eecs4413term_project.model.Auction; // Import your Auction model
import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.eecs4413.eecs4413term_project.repository.ReceiptsRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository; // Import Auction Repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {
    
    private final ReceiptsRepository receiptsRepository;
    private final PurchasesRepository purchasesRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository; // Add this

    @Autowired
    public ReceiptService(ReceiptsRepository receiptsRepository, 
                          PurchasesRepository purchasesRepository,
                          UserRepository userRepository,
                          AuctionRepository auctionRepository) { // Inject here
        this.receiptsRepository = receiptsRepository;
        this.purchasesRepository = purchasesRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public Object auctionReceiptCreation(UUID purchaseId, User owner, Integer shippingDays, Long auctionId) {
        Optional<Purchases> purchaseOpt = purchasesRepository.findById(purchaseId);
        if (purchaseOpt.isEmpty()) {
            return null;
        }
        Purchases purchase = purchaseOpt.get();
        Receipt receipt = new Receipt(purchase, owner, shippingDays);
        
        // 1. Save the receipt
        receipt.setAuctionId(auctionId);
        Receipt saved = receiptsRepository.save(receipt);

        // 2. CLOSE THE AUCTION
        if (auctionId != null) {
            Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
            if (auctionOpt.isPresent()) {
                Auction auction = auctionOpt.get();
                auction.setClosed(true); // Or auction.setStatus("ENDED");
                auctionRepository.save(auction);
            }
        }

        return saved;
    }
}