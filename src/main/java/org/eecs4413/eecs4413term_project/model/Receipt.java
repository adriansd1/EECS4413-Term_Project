package org.eecs4413.eecs4413term_project.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    @JsonIgnore
    private Purchases purchase; // changed type

    //TODO: winner name temporarily stored as string, will use winner_id from user table as foreign key later
    @Column(name = "winner_name")
    private String winnerName;

    //TODO: owner name temporarily stored as string, will use owner_id from user table as foreign key later
    @Column(name = "owner_name")
    private String ownerName;

    //TODO: will fetch winner address from user table using winner_id foreign key
    @Column(name = "winner_address")
    private String winnerAddress;

    //TODO: will fetch owner address from user table using owner_id foreign key
    @Column(name = "owner_address")
    private String ownerAddress;

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

    // updated to accept Purchases entity
    // this constructor needs to be linked to users table and use user_id as foreign key for owner and winner
    public Receipt(Purchases purchase, User owner, Integer shippingDays) {
        if (purchase == null) {
            throw new IllegalArgumentException("All receipt fields must be valid and non-null.");
        }
        if (owner == null || !owner.isAuthenticated()) {
            throw new IllegalArgumentException("Owner must be an authenticated user.");
        }
        this.receiptId = UUID.randomUUID();
        this.purchase = purchase;
        this.winnerName = purchase.getUserName();
        this.winnerAddress = purchase.getShippingAddress();
        this.amount = purchase.getAmount();
        this.price = purchase.getPrice();
        this.finalPrice = purchase.getPrice() * this.amount;
        this.shippingDays = shippingDays;
        this.auctionItem = purchase.getItem();
        this.ownerName = owner.getName();
        this.ownerAddress = owner.getAddress();
        if (!validEntries()) {
            throw new IllegalArgumentException("All receipt fields must be valid and non-null.");
        }
    }

    public boolean validEntries() {
        return purchase != null &&
               ownerName != null && ownerAddress != null &&
               shippingDays != null && shippingDays >= 0;
    }
    
    public UUID getReceiptId() {
        return receiptId;
    }

    public Purchases getPurchase() { return purchase; } // getter for purchase entity

    @JsonProperty("purchaseId")
    public UUID getPurchaseId() { return purchase != null ? purchase.getPurchaseId() : null; } // convenience

    public User getWinner() {
        return new User(winnerName, true, winnerAddress);
    }

    public User getOwner() {
        return new User(ownerName, true, ownerAddress);
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getWinnerAddress() {
        return winnerAddress;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public String getOwnerName() {
        return ownerName;
    }

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
                ", winnerName='" + winnerName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", auctionItem='" + auctionItem + '\'' +
                ", finalPrice=" + finalPrice +
                ", shippingDays=" + shippingDays +
                '}';
    }
}
