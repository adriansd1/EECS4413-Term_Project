package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.eecs4413.eecs4413term_project.model.User;
import org.eecs4413.eecs4413term_project.repository.PurchasesRepository;
import org.eecs4413.eecs4413term_project.repository.UserRepository;

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
    private final UserRepository userRepository;

    public PurchaseController(PurchasesRepository purchasesRepository, UserRepository userRepository) {
        this.purchasesRepository = purchasesRepository;
        this.userRepository = userRepository;
    }

    static class PurchaseRequest {
        public String item;
        public Integer amount;
        public Double price;
        public String cardNumber;
        public String cardExpiry;
        public String cardCvv;
        public String userId;
    }

    @PostMapping("/makePurchase")
    public ResponseEntity<String> makePurchase(@RequestBody PurchaseRequest request) {

                User user = null;

        try {
            Optional<User> userOpt = userRepository.findById(Long.parseLong(request.userId));
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Purchase failed: User not found.");
            }
            user = userOpt.get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Purchase failed: " + e.getMessage());
        }

        try {
            // In a real app, you would authenticate() the user before making a purchase
            // just for API testing purposes
            user.setAuthenticated(true);
            Purchases purchase = new Purchases(request.item, request.amount, request.price, user, request.cardNumber, request.cardExpiry, request.cardCvv);
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

    @GetMapping("/getPurchase/{purchaseId}")
    public ResponseEntity<Purchases> getPurchase(@PathVariable String purchaseId) {
        Optional<Purchases> purchaseOpt = purchasesRepository.findById(UUID.fromString(purchaseId));
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(purchaseOpt.get());
    }

    //TODO: implement get purchase by user by using user_id once connected to user database
}