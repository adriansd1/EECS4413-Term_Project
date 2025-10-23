package org.eecs4413.eecs4413term_project;

import java.math.BigDecimal;

class User {
    private final String name;
    private boolean isAuthenticated;

    public User(String name, boolean isAuthenticated) {
        this.name = name;
        this.isAuthenticated = isAuthenticated;
    }

    public String getName() {
        return name;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    // Simulate successful sign-up/authentication
    public void authenticate() {
        this.isAuthenticated = true;
        System.out.println("✅ " + name + " has been authenticated successfully!");
    }
}

class BiddingClass {
    private final String itemName;
    private BigDecimal currentHighestBid;
    private User currentHighestBidder;

    public BiddingClass(String itemName, BigDecimal startingPrice) {
        this.itemName = itemName;
        this.currentHighestBid = startingPrice;
        this.currentHighestBidder = null;
    }

    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }

    public User getCurrentHighestBidder() {
        return currentHighestBidder;
    }

    public String getItemName() {
        return itemName;
    }

    // ✅ Core bid logic with authentication check
    public boolean placeBid(User bidder, BigDecimal bidAmount) {
        if (!bidder.isAuthenticated()) {
            System.out.println("⚠️  " + bidder.getName() + " is not authenticated. Please sign up or log in before bidding.");
            return false;
        }

        if (bidAmount.compareTo(currentHighestBid) > 0) {
            currentHighestBid = bidAmount;
            currentHighestBidder = bidder;
            System.out.println("💰 " + bidder.getName() + " placed a bid of $" + bidAmount);
            return true;
        } else {
            System.out.println("❌ Bid too low. Current highest: $" + currentHighestBid);
            return false;
        }
    }

    public void displayStatus() {
        System.out.println("\n=== Auction Status ===");
        System.out.println("Item: " + itemName);
        System.out.println("Current highest bid: $" + currentHighestBid);
        System.out.println("Highest bidder: " +
            (currentHighestBidder != null ? currentHighestBidder.getName() : "None yet"));
        System.out.println("======================\n");
    }
}
