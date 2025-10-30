package org.eecs4413.eecs4413term_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "auctions")
public class AuctionClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private BigDecimal startingPrice;

    private BigDecimal currentHighestBid;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private boolean isClosed;

    // --- Relationships ---

    // Tracks who is currently winning
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_highest_bidder_id")
    private User currentHighestBidder;

    // Tracks all bids placed on this auction.
    // This replaces your "allBidders" list.
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BiddingClass> bids;

    // --- Constructors ---
    public AuctionClass() {
    }

    public AuctionClass(String itemName, BigDecimal startingPrice, LocalDateTime endTime) {
        this.itemName = itemName;
        this.startingPrice = startingPrice;
        this.currentHighestBid = startingPrice; // Starts at the starting price
        this.endTime = endTime;
        this.isClosed = false;
        this.currentHighestBidder = null;
    }

    // --- Getters and Setters ---
    // (JPA needs these to function)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(BigDecimal currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public User getCurrentHighestBidder() {
        return currentHighestBidder;
    }

    public void setCurrentHighestBidder(User currentHighestBidder) {
        this.currentHighestBidder = currentHighestBidder;
    }

    public Set<BiddingClass> getBids() {
        return bids;
    }

    public void setBids(Set<BiddingClass> bids) {
        this.bids = bids;
    }
}

