package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.Receipt;
import org.eecs4413.eecs4413term_project.model.User;

import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.eecs4413.eecs4413term_project.repository.ReceiptsRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptsRepository receiptsRepository;
    private final PurchasesRepository purchasesRepository;
    private final UserRepository userRepository;

    public ReceiptController(ReceiptsRepository receiptsRepository, PurchasesRepository purchasesRepository,
            UserRepository userRepository) {
        this.receiptsRepository = receiptsRepository;
        this.purchasesRepository = purchasesRepository;
        this.userRepository = userRepository;
    }
    
    static class ReceiptRequest {
        public String purchaseId;
        public String owner_id;
        public Integer shippingDays;
    }

    @PostMapping("/createReceipt")
    public ResponseEntity<String> createReceipt(@RequestBody ReceiptRequest request) {
        UUID pid;
        try {
            pid = UUID.fromString(request.purchaseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid purchaseId UUID format");
        }

        Optional<Purchases> purchaseOpt = purchasesRepository.findById(pid);
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase not found for id: " + request.purchaseId);
        }
        Purchases purchase = purchaseOpt.get();
        try {
           Optional<User> userOpt = userRepository.findById(Long.parseLong(request.owner_id));
           if (userOpt.isEmpty()) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for id: " + request.owner_id);
           }
           User owner = userOpt.get();
           // In a real app, you would authenticate() the user before making a purchase
            // just for API testing purposes
            owner.setAuthenticated(true);

            Receipt receipt = new Receipt(purchase, owner, request.shippingDays);
            Receipt saved = receiptsRepository.save(receipt);
            return ResponseEntity.ok("Receipt created: " + saved.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receipt creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/getReceipt/{receiptId}")
    public ResponseEntity<String> getReceipt(@PathVariable String receiptId) {
        Optional<Receipt> receiptOpt = receiptsRepository.findById(UUID.fromString(receiptId));
        if (receiptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found for id: " + receiptId);
        }
        Receipt receipt = receiptOpt.get();
        return ResponseEntity.ok("Receipt details for ID: " + receiptId + " - " + receipt.toString());
    }

    @GetMapping("/getAllReceipts")
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> allReceipts = receiptsRepository.findAll();
        return ResponseEntity.ok(allReceipts);
    }

    @GetMapping("/getReceiptsByPurchase/{purchaseId}")
    public ResponseEntity<List<Receipt>> getReceiptsByPurchase(@PathVariable String purchaseId) {
        List<Receipt> receipts = receiptsRepository.findByPurchaseId(UUID.fromString(purchaseId));
        if (receipts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(receipts);
    }

    //TODO: impplement API to get receipts by winner and owner using their id's once connected to user database
}