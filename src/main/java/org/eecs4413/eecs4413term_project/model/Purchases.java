package org.eecs4413.eecs4413term_project.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "purchases")
public class Purchases {

    @Id
    @Column(name = "purchase_id", columnDefinition = "uuid")
    private UUID purchaseId;

    @Column(name = "item")
    private String item;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private double price;

    // --- FIX 1: Replaced String fields with a real User relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Links to the User table
    @JsonIgnore // Prevents infinite loops
    private User user; // This is the user (buyer)

    // --- 'shippingAddress' and 'userName' columns are GONE ---

    @Column(name = "card_tail")
    private Integer cardTail;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    public Purchases() {
        // JPA
    }

    //card details
    private transient String cardNumber;
    private transient String cardExpiry;
    private transient String cardCvv;

    // --- FIX 2: Constructor now correctly assigns the User object ---
    public Purchases(String item, Integer amount, Double price, User user, String cardNumber, String cardExpiry, String cardCvv) {
        if (user == null || !user.isAuthenticated()) {
            throw new IllegalArgumentException("User must be authenticated to make a purchase.");
        }

        this.purchaseId = UUID.randomUUID();
        this.purchasedAt = LocalDateTime.now();
        this.item = item;
        this.price = price;
        this.amount = amount;
        
        // --- Store the actual User object ---
        this.user = user; 
        
        // --- These lines are no longer needed, as the data lives on the user object ---
        // this.userName = user.getFirstName() +" "+ user.getLastName();
        // this.shippingAddress = user.getShippingAddress();
        
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCvv = cardCvv;

        if (!validEntries()) {
            throw new IllegalArgumentException("All purchase fields must be valid and non-null.");
        }

        if (!validCardPurchase(cardNumber, cardExpiry, cardCvv)) {
            throw new IllegalArgumentException("Invalid card details provided.");
        }
    }


    public boolean validCardPurchase(String cardNumber, String cardExpiry, String cardCvv) {
        if (cardNumber == null || cardExpiry == null || cardCvv == null)
            return false;
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
    
    // --- FIX 3: validEntries now checks the User object ---
    public boolean validEntries() {
        return item != null && !item.isEmpty()
                && price >= 0
                && user != null // Check the user object
                && amount != null && amount > 0;
    }

    /* Getters and setters */

    public UUID getPurchaseId() { return purchaseId; }
    public void setPurchaseId(UUID purchaseId) { this.purchaseId = purchaseId; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Integer getCardTail() { return cardTail; }
    public void setCardTail(Integer cardTail) { this.cardTail = cardTail; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    // --- FIX 4: The method that was failing (purchase.getUser()) ---
    /**
     * Gets the User object associated with this purchase.
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // --- FIX 5: Keep API output consistent (optional but recommended) ---
    // These getters create "virtual" JSON properties from the User object
    // so your API output doesn't break.
    
    @JsonProperty("shippingAddress")
    public String getShippingAddress() { 
        return (this.user != null) ? user.getShippingAddress() : null; 
    }

    @JsonProperty("userName")
    public String getUserName() { 
        return (this.user != null) ? user.getFirstName() + " " + user.getLastName() : null;
    }

    // --- Card getters/setters (unchanged) ---
    
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() {
        return cardExpiry;
    }
    
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCvv() {
        return cardCvv;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
    
    // --- FIX 6: toString now uses the new getter ---
    @Override
    public String toString() {
        return "Purchase{" +
                "purchaseId=" + purchaseId +
                ", item='" + item + '\'' +
                ", amount=" + amount +
                ", userName='" + getUserName() + '\'' + // Calls the new getter
                ", price=" + price +
                ", shippingAddress='" + getShippingAddress() + '\'' + // Calls the new getter
                ", cardTail=" + cardTail +
                ", purchasedAt=" + purchasedAt +
                '}';
    }
}