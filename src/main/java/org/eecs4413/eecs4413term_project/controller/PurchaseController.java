package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchasesRepository purchasesRepository;

    public PurchaseController(PurchasesRepository purchasesRepository) {
        this.purchasesRepository = purchasesRepository;
    }

    @PostMapping("/makePurchase")
    public ResponseEntity<String> makePurchase(@RequestParam String item,
            @RequestParam Integer amount,
            @RequestParam Double price,
            @RequestParam String shippingAddress,
            @RequestParam String cardNumber,
            @RequestParam String cardExpiry,
            @RequestParam String cardCvv,
            @RequestParam String userName,
            @RequestParam boolean isAuthenticated) {
        User user = new User(userName, isAuthenticated, shippingAddress);
        try {
            Purchases purchase = new Purchases(item, amount, price, user, cardNumber, cardExpiry, cardCvv);
            Purchases saved = purchasesRepository.save(purchase);
            return ResponseEntity.ok("Purchase successful: " + saved.toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Purchase failed: " + e.getMessage());
        }
    }

    @GetMapping("/getAllPurchases")
    public ResponseEntity<List<Purchases>> getAllPurchases() {
        List<Purchases> allPurchases = purchasesRepository.findAll();
        return ResponseEntity.ok(allPurchases);
    }

    @GetMapping("/getPurchase")
    public ResponseEntity<Purchases> getPurchase(@RequestParam String purchaseId) {
        Optional<Purchases> purchaseOpt = purchasesRepository.findById(UUID.fromString(purchaseId));
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(purchaseOpt.get());
    }

    //TODO: implement get purchase by user by using user_id once connected to user database
}