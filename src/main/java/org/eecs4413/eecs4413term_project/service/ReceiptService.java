package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.Receipt;
import org.eecs4413.eecs4413term_project.model.AuctionClass;

import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.eecs4413.eecs4413term_project.repository.ReceiptsRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.eecs4413.eecs4413term_project.repository.AuctionRepository;

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
    private final AuctionRepository auctionRepository;

    @Autowired
    public ReceiptService(ReceiptsRepository receiptsRepository, 
                          PurchasesRepository purchasesRepository,
                          UserRepository userRepository,
                          AuctionRepository auctionRepository) {
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
        
        receipt.setAuctionId(auctionId);
        Receipt saved = receiptsRepository.save(receipt);

        if (auctionId != null) {
            Optional<AuctionClass> auctionOpt = auctionRepository.findById(auctionId);
            if (auctionOpt.isPresent()) {
                AuctionClass auction = auctionOpt.get();
                auction.setClosed(true);
                auctionRepository.save(auction);
            }
        }

        return saved;
    }
}