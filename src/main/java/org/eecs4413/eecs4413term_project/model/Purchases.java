package org.eecs4413.eecs4413term_project.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class Purchases {
    private UUID purchase_id;
    private String item;
    private User user;
    private double price;
    private String shippingAddress;
    private int cardTail;
    private LocalDateTime purchasedAt;
    private static List<Purchases> allPurchases = new ArrayList<>();

    public Purchases(String item, Double price, User user, String shippingAddress, String cardNumber, String cardExpiry, String cardCvv) {
        if (user.isAuthenticated() && validCardPurchase(cardNumber, cardExpiry, cardCvv)) {
            this.purchasedAt = LocalDateTime.now();
            this.item = item;
            this.price = price;
            this.user = user;
            this.shippingAddress = shippingAddress;
            UUID uuid = UUID.randomUUID();
            this.purchase_id = uuid;
            allPurchases.add(this);
        } else {
            throw new IllegalArgumentException("User must be authenticated to make a purchase.");
        }
    }

    public boolean validCardPurchase(String cardNumber, String cardExpiry, String cardCvv) {
        // Basic validation logic (for demonstration purposes)
        if (cardNumber.length() == 16 && cardExpiry.matches("\\d{2}/\\d{2}") && cardCvv.length() == 3) {
            this.cardTail = Integer.parseInt(cardNumber.substring(cardNumber.length() - 4));
            return true;
        }
        System.out.println("Invalid card details provided.");
        return false;
    }

    public UUID getId() { return purchase_id; }
    public void setId(UUID id) { this.purchase_id = id; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public int getCardTail() { return cardTail; }
    public void setCardTail(int cardTail) { this.cardTail = cardTail; }

    public static java.util.List<Purchases> getAllPurchases() {
        // Placeholder for actual data retrieval logic
        return allPurchases;
    }

    public String toString() {
        return "Purchase{" +
                "purchase_id=" + purchase_id +
                ", item='" + item + '\'' +
                ", user=" + user.getName() +
                ", price=" + price +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", cardTail=" + cardTail +
                ", purchasedAt=" + purchasedAt +
                '}';
    }
}
