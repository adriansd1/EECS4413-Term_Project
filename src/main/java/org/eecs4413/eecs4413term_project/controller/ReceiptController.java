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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptsRepository receiptsRepository;
    private final PurchasesRepository purchasesRepository;
    private final UserRepository userRepository; // --- 2. ADD USER REPO FIELD ---

    // --- 3. UPDATE CONSTRUCTOR ---
    public ReceiptController(ReceiptsRepository receiptsRepository,
                             PurchasesRepository purchasesRepository,
                             UserRepository userRepository) {
        this.receiptsRepository = receiptsRepository;
        this.purchasesRepository = purchasesRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/createReceipt")
    public ResponseEntity<String> createReceipt(@RequestParam String purchaseId,
                                                @RequestParam String ownerName, // 'ownerAddress' param removed, it's redundant
                                                @RequestParam Integer shippingDays) {
        UUID pid;
        try {
            pid = UUID.fromString(purchaseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid purchaseId UUID format");
        }

        Optional<Purchases> purchaseOpt = purchasesRepository.findById(pid);
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase not found for id: " + purchaseId);
        }
        Purchases purchase = purchaseOpt.get();

        // --- 4. FIND THE REAL USER (OWNER) ---
        Optional<User> userOpt = userRepository.findByUsername(ownerName);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User (owner) not found: " + ownerName);
        }
        User owner = userOpt.get(); // This is the real, managed user object

        // --- 5. CREATE RECEIPT WITH REAL OBJECTS ---
        try {
            // We pass the 'owner' object, not the 'ownerName' or 'ownerAddress'
            Receipt receipt = new Receipt(purchase, owner, shippingDays);
            Receipt saved = receiptsRepository.save(receipt);
            return ResponseEntity.ok("Receipt created: " + saved.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receipt creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/getReceipt")
    public ResponseEntity<String> getReceipt(@RequestParam String receiptId) {
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

    @GetMapping("/getReceiptsByPurchase")
    public ResponseEntity<List<Receipt>> getReceiptsByPurchase(@RequestParam String purchaseId) {
        UUID pid;
        try {
            pid = UUID.fromString(purchaseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
        List<Receipt> receipts = receiptsRepository.findAll()
                .stream()
                .filter(r -> r.getPurchase() != null && pid.equals(r.getPurchase().getPurchaseId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(receipts);
    }

    //TODO: impplement API to get receipts by winner and owner using their id's once connected to user database
}