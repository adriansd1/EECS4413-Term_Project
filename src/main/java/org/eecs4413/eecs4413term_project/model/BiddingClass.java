package org.eecs4413.eecs4413term_project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class BiddingClass {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private LocalDateTime bidTime;

    // --- Relationships ---

    // The User who placed this bid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The Auction this bid was placed on
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private AuctionClass auction;

    // --- Constructors ---
    public BiddingClass() {
    }

    public BiddingClass(BigDecimal bidAmount, LocalDateTime bidTime, User user, AuctionClass auction) {
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.user = user;
        this.auction = auction;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuctionClass getAuction() {
        return auction;
    }

    public void setAuction(AuctionClass auction) {
        this.auction = auction;
    }
}
