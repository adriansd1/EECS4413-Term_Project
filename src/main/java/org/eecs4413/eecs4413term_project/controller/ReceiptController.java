package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.Receipt;
import org.eecs4413.eecs4413term_project.model.User;

import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.eecs4413.eecs4413term_project.repository.ReceiptsRepository;
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

    public ReceiptController(ReceiptsRepository receiptsRepository, PurchasesRepository purchasesRepository) {
        this.receiptsRepository = receiptsRepository;
        this.purchasesRepository = purchasesRepository;
    }

    @PostMapping("/createReceipt")
    public ResponseEntity<String> createReceipt(@RequestParam String purchaseId,                                      
                                                @RequestParam String ownerAddress,
                                                @RequestParam String ownerName,
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
        try {
            User owner = new User(ownerName, true, ownerAddress);
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
