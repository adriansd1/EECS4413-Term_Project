package org.eecs4413.eecs4413term_project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogue")
public class Catalogue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String type;
    private Double currentBid;
    private Double startingPrice;
    private LocalDateTime endTime;

    private String sellerName;
    private String sellerAddress;
    private Long sellerId;
    private String imageUrl;

    private Double minPrice;
    private Double decreaseAmount;

    public Catalogue() {}

    public Catalogue(String title, String description, String type,
                     Double currentBid, Double startingPrice, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.currentBid = currentBid;
        this.startingPrice = startingPrice;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getCurrentBid() { return currentBid; }
    public void setCurrentBid(Double currentBid) { this.currentBid = currentBid; }

    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerAddress() { return sellerAddress; }
    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getDecreaseAmount() { return decreaseAmount; }
    public void setDecreaseAmount(Double decreaseAmount) { this.decreaseAmount = decreaseAmount; }
}