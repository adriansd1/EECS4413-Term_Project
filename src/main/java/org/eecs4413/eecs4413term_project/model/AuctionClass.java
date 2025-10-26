// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.eecs4413.eecs4413term_project.model;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionClass {
   private final String itemName;
   private final BigDecimal startingPrice;
   private BigDecimal currentHighestBid;
   private User currentHighestBidder;
   private final LocalDateTime endTime;
   private boolean isClosed;
   private final List<User> allBidders;
   private static final List<AuctionClass> CATALOGUE = new ArrayList();

   public AuctionClass(String itemName, BigDecimal startingPrice, LocalDateTime endTime) {
      this.itemName = itemName;
      this.startingPrice = startingPrice;
      this.currentHighestBid = startingPrice;
      this.currentHighestBidder = null;
      this.endTime = endTime;
      this.isClosed = false;
      this.allBidders = new ArrayList();
      CATALOGUE.add(this);
      this.scheduleAuctionEnd();
   }

   public String getItemName() {
      return this.itemName;
   }

   public BigDecimal getCurrentHighestBid() {
      return this.currentHighestBid;
   }

   public User getCurrentHighestBidder() {
      return this.currentHighestBidder;
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   public synchronized boolean placeBid(User bob, BigDecimal bidAmount) {
      if (this.isClosed) {
         System.out.println("⚠️ Auction for " + this.itemName + " has already ended.");
         return false;
      } else if (!bob.isAuthenticated()) {
         System.out.println("⚠️ " + bob.getName() + " must sign in before bidding.");
         return false;
      } else if (bidAmount.compareTo(this.currentHighestBid) > 0) {
         this.currentHighestBid = bidAmount;
         this.currentHighestBidder = bob;
         this.allBidders.add(bob);
         System.out.println("\ud83d\udcb0 " + bob.getName() + " placed a bid of $" + bidAmount + " on " + this.itemName);
         return true;
      } else {
         System.out.println("❌ Bid too low. Current highest: $" + this.currentHighestBid);
         return false;
      }
   }

   private void scheduleAuctionEnd() {
    long delayMillis = Duration.between(LocalDateTime.now(), this.endTime).toMillis();
    
    if (delayMillis <= 0L) {
        // Auction time is already past or current, close it immediately
        this.closeAuction();
      } else {
        // Create a new Timer (daemon thread = true)
        Timer timer = new Timer(true);
        
        // Use an anonymous inner class that extends TimerTask
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // This code runs when the delayMillis time has passed
                // Call the method to end the auction
                closeAuction();
            }
           }, delayMillis);
       }
   }

   private synchronized void closeAuction() {
      if (!this.isClosed) {
         this.isClosed = true;
         System.out.println("\n\ud83d\udea8 Auction for '" + this.itemName + "' has ended!");
         if (this.currentHighestBidder != null) {
            this.notifyWinner(this.currentHighestBidder);
         } else {
            System.out.println("No bids were placed for " + this.itemName + ".");
         }

         this.notifyLosers();
         this.removeFromCatalogue();
      }
   }

   private void notifyWinner(User winner) {
      PrintStream var10000 = System.out;
      String var10001 = winner.getName();
      var10000.println("\ud83c\udfc6 Congratulations " + var10001 + "! You won the auction for " + this.itemName + " with a bid of $" + this.currentHighestBid);
   }

   private void notifyLosers() {
      Iterator var1 = this.allBidders.iterator();

      while(var1.hasNext()) {
         User bidder = (User)var1.next();
         if (!bidder.equals(this.currentHighestBidder)) {
            PrintStream var10000 = System.out;
            String var10001 = bidder.getName();
            var10000.println("\ud83d\udce2 " + var10001 + ", the auction for " + this.itemName + " has ended. You did not win.");
         }
      }

   }

   private void removeFromCatalogue() {
      CATALOGUE.remove(this);
      System.out.println("\ud83d\uddd1️ " + this.itemName + " has been removed from the catalogue.\n");
   }

   public void displayStatus() {
      System.out.println("\n=== Auction Status ===");
      System.out.println("Item: " + this.itemName);
      System.out.println("Current highest bid: $" + this.currentHighestBid);
      String var10001 = this.currentHighestBidder != null ? this.currentHighestBidder.getName() : "None";
      System.out.println("Highest bidder: " + var10001);
      LocalDateTime var1 = this.endTime;
      System.out.println("End Time: " + var1);
      System.out.println("Status: " + (this.isClosed ? "Closed" : "Active"));
      System.out.println("======================\n");
   }
}
