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
import java.util.HashMap;
import java.util.Map;

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
// 1. Change <String> to <?> to allow returning a Map (JSON) OR String (Error)
public ResponseEntity<?> makePurchase(@RequestBody PurchaseRequest request) {

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
        user.setAuthenticated(true);
        
        Purchases purchase = new Purchases(
            request.item, 
            request.amount, 
            request.price, 
            user, 
            request.cardNumber, 
            request.cardExpiry, 
            request.cardCvv
        );
        
        Purchases saved = purchasesRepository.save(purchase);

        // 2. Create a Map to represent the JSON object
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Purchase successful");
        response.put("purchaseId", saved.getPurchaseId()); // This solves the frontend requirement
        
        // 3. Return the Map. Spring Boot will automatically convert this to valid JSON
        return ResponseEntity.ok(response);
        
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

}