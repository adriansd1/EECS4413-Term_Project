package org.eecs4413.eecs4413term_project.model;

import java.util.UUID;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @Column(name = "receipt_id", columnDefinition = "uuid")
    private UUID receiptId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "purchase_id")
    @JsonIgnore // Prevents infinite loops when sending JSON
    private Purchases purchase; // This object contains the "winner" (buyer)

    // --- FIX 1: Replaced String fields with a real User relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id") // Links to the User table
    @JsonIgnore // Prevents infinite loops
    private User owner; // This is the "owner" (seller)

    // --- All old String fields for user data are now removed ---
    // (e.g., winnerName, winnerAddress, ownerName, ownerAddress are GONE)

    @Column(name = "auction_item")
    private String auctionItem;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private Double price;

    @Column(name = "final_price")
    private Double finalPrice;

    @Column(name = "shipping_days")
    private Integer shippingDays;

    public Receipt() {
        // JPA
    }

    // --- FIX 2: Constructor now accepts and stores the real User object ---
    public Receipt(Purchases purchase, User owner, Integer shippingDays) {
        if (purchase == null || purchase.getUser() == null) {
            throw new IllegalArgumentException("Purchase and its user (the winner) cannot be null.");
        }
        if (owner == null || !owner.isAuthenticated()) {
            throw new IllegalArgumentException("Owner must be an authenticated user.");
        }

        this.receiptId = UUID.randomUUID();
        
        // --- Store the actual objects ---
        this.purchase = purchase;
        this.owner = owner;
        
        // --- Copy data from the purchase ---
        this.auctionItem = purchase.getItem();
        this.amount = purchase.getAmount();
        this.price = purchase.getPrice();
        this.finalPrice = purchase.getPrice() * this.amount;
        this.shippingDays = shippingDays;

        if (!validEntries()) {
            throw new IllegalArgumentException("All receipt fields must be valid and non-null.");
        }
    }

    // --- FIX 3: validEntries now checks the real User objects ---
    public boolean validEntries() {
        return purchase != null &&
               owner != null &&
               purchase.getUser() != null && // Check winner
               shippingDays != null && shippingDays >= 0;
    }
    
    public UUID getReceiptId() {
        return receiptId;
    }

    public Purchases getPurchase() { return purchase; } // getter for purchase entity

    @JsonProperty("purchaseId")
    public UUID getPurchaseId() { return purchase != null ? purchase.getPurchaseId() : null; } // convenience

    // --- FIX 4: The methods you asked for, now correct ---
    
    /**
     * Gets the Winner (Buyer) User object from the associated Purchase.
     */
    public User getWinner() {
        return (this.purchase != null) ? this.purchase.getUser() : null;
    }

    /**
     * Gets the Owner (Seller) User object.
     */
    public User getOwner() {
        return this.owner;
    }

    // --- FIX 5: Convenience getters for JSON serialization ---
    // This keeps your API output clean without storing redundant data
    
    @JsonProperty("winnerName")
    public String getWinnerName() {
        User winner = getWinner();
        return (winner != null) ? winner.getFirstName() + " " + winner.getLastName() : null;
    }

    @JsonProperty("winnerAddress")
    public String getWinnerAddress() {
        User winner = getWinner();
        return (winner != null) ? winner.getShippingAddress() : null; 
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return (this.owner != null) ? owner.getFirstName() + " " + owner.getLastName() : null;
    }

    @JsonProperty("ownerAddress")
    public String getOwnerAddress() {
        return (this.owner != null) ? owner.getShippingAddress() : null;
    }
    
    // --- Other Getters (unchanged) ---

    public String getAuctionItem() {
        return auctionItem;
    }

    public Integer getAmount() {
        return amount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public int getShippingDays() {
        return shippingDays;
    }
    
    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId=" + receiptId +
                ", purchaseId=" + (purchase != null ? purchase.getPurchaseId() : null) +
                ", winnerName='" + getWinnerName() + '\'' +
                ", ownerName='" + getOwnerName() + '\'' +
                ", auctionItem='" + auctionItem + '\'' +
                ", finalPrice=" + finalPrice +
                ", shippingDays=" + shippingDays +
                '}';
    }
}