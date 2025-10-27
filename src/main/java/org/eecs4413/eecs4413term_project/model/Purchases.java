package org.eecs4413.eecs4413term_project.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "purchases")
public class Purchases {

    @Id
    @Column(name = "purchase_id", columnDefinition = "uuid")
    private UUID purchaseId;

    @Column(name = "item")
    private String item;

    @Column(name = "price")
    private double price;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "card_tail")
    private Integer cardTail;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "user_name")
    private String userName;

    public Purchases() {
        // JPA
    }

    public Purchases(String item, Double price, User user, String shippingAddress, String cardNumber, String cardExpiry, String cardCvv) {
        if (user == null || !user.isAuthenticated() || !validCardPurchase(cardNumber, cardExpiry, cardCvv)) {
            throw new IllegalArgumentException("User must be authenticated and card details valid to make a purchase.");
        }
        this.purchaseId = UUID.randomUUID();
        this.purchasedAt = LocalDateTime.now();
        this.item = item;
        this.price = price;
        this.userName = user.getName();
        this.shippingAddress = shippingAddress;
        // cardTail set by validCardPurchase
    }

    public boolean validCardPurchase(String cardNumber, String cardExpiry, String cardCvv) {
        if (cardNumber == null || cardExpiry == null || cardCvv == null) return false;
        if (cardNumber.length() == 16 && cardExpiry.matches("\\d{2}/\\d{2}") && cardCvv.length() == 3) {
            try {
                this.cardTail = Integer.parseInt(cardNumber.substring(cardNumber.length() - 4));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /* Getters and setters */

    public UUID getPurchaseId() { return purchaseId; }
    public void setPurchaseId(UUID purchaseId) { this.purchaseId = purchaseId; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public Integer getCardTail() { return cardTail; }
    public void setCardTail(Integer cardTail) { this.cardTail = cardTail; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public String toString() {
        return "Purchase{" +
                "purchaseId=" + purchaseId +
                ", item='" + item + '\'' +
                ", userName='" + userName + '\'' +
                ", price=" + price +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", cardTail=" + cardTail +
                ", purchasedAt=" + purchasedAt +
                '}';
    }
}