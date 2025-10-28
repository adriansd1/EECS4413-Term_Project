package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam Double price,
            @RequestParam String shippingAddress,
            @RequestParam String cardNumber,
            @RequestParam String cardExpiry,
            @RequestParam String cardCvv,
            @RequestParam String userName,
            @RequestParam boolean isAuthenticated) {
        User user = new User(userName, isAuthenticated, shippingAddress);
        try {
            Purchases purchase = new Purchases(item, price, user, cardNumber, cardExpiry, cardCvv);
            Purchases saved = purchasesRepository.save(purchase);
            return ResponseEntity.ok("Purchase successful: " + saved.toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Purchase failed: " + e.getMessage());
        }
    }

    @GetMapping("/getAllPurchases")
    public ResponseEntity<List<Purchases>> getAllPurchases() {
        List<Purchases> purchases = purchasesRepository.findAll();
        return ResponseEntity.ok(purchases);
    }
}