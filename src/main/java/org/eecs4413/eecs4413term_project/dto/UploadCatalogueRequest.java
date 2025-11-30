package org.eecs4413.eecs4413term_project.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public class UploadCatalogueRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "type is required")
    private String type;

    @NotNull(message = "startingPrice is required")
    @Positive(message = "startingPrice must be positive")
    private Double startingPrice;

    @NotNull(message = "durationMinutes is required")
    @Positive(message = "durationMinutes must be positive")
    private Integer durationMinutes;

    @NotBlank(message = "auctionType is required")
    private String auctionType;


    private String seller; // optional
    private String imageUrl; // optional
    private Double minPrice;
    private Double decreaseAmount;
    private Integer decreaseIntervalSeconds;

    public UploadCatalogueRequest() {}

    // getters & setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getDecreaseAmount() { return decreaseAmount; }
    public void setDecreaseAmount(Double decreaseAmount) { this.decreaseAmount = decreaseAmount; }

    public Integer getDecreaseIntervalSeconds() { return decreaseIntervalSeconds;}
    public void setDecreaseIntervalSeconds(Integer decreaseIntervalSeconds) {this.decreaseIntervalSeconds = decreaseIntervalSeconds;}

    public String getAuctionType() { return auctionType; }
    public void setAuctionType(String auctionType) { this.auctionType = auctionType; }
}
