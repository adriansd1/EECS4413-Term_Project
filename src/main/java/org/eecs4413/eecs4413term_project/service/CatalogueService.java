package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository;
import org.eecs4413.eecs4413term_project.repository.ReceiptsRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CatalogueService {

    private final CatalogueRepository repo;
    private final ReceiptsRepository receiptsRepo;

    // Single Constructor for Dependency Injection
    public CatalogueService(CatalogueRepository repo, ReceiptsRepository receiptsRepo) {
        this.repo = repo;
        this.receiptsRepo = receiptsRepo;
    }

    // --- UC2.1: Keyword search ---
    public List<Catalogue> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(); // Graceful fallback
        }
        return repo.searchByKeyword(keyword.trim());
    }

    // --- UC2.2: Display active auctions ---
    public List<Map<String, Object>> getActiveAuctions() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Extend visibility: Show items that expired up to 24 hours ago
        LocalDateTime cutoff = now.minusHours(24); 
        List<Catalogue> active = repo.findActiveAuctions(cutoff);

        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Catalogue c : active) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getTitle());
            item.put("currentBid", c.getCurrentBid());
            item.put("type", c.getType());
            item.put("timeLeft", formatTimeLeft(now, c.getEndTime()));
            item.put("sellerName", c.getSellerName());
            item.put("sellerAddress", c.getSellerAddress());
            item.put("sellerId", c.getSellerId());
            item.put("imageUrl", c.getImageUrl());
            // 2. Send the Winner's ID
            item.put("currentHighestBidderId", c.getCurrentHighestBidderId()); 

            // 3. Check if Receipt exists using the injected Repo
            boolean isPaid = receiptsRepo.existsByAuctionId(c.getId());
            item.put("hasReceipt", isPaid);
            response.add(item);
        }
        return response;
    }

    // --- Helper for time remaining display ---
    private String formatTimeLeft(LocalDateTime now, LocalDateTime end) {
        Duration d = Duration.between(now, end);
        if (d.isNegative()) return "Expired";
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();

        if (hours > 0) return String.format("%dh %dm", hours, minutes);
        if (minutes > 0) return String.format("%dm %ds", minutes, seconds);
        return String.format("%ds", seconds);
    }

    // --- UC2.3: Find by ID for selection ---
    public Optional<Catalogue> findById(Long id) {
        return repo.findById(id);
    }

    // --- UC7: create/upload a catalogue item ---
    public Catalogue createCatalogue(String title,
                                     String description,
                                     String type,
                                     Double startingPrice,
                                     Integer durationMinutes,
                                     String sellerName,
                                     String sellerAddress,
                                     Long sellerId,
                                     String imageUrl) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        Catalogue c = new Catalogue();
        c.setTitle(title);
        c.setDescription(description);
        c.setType(type);
        c.setStartingPrice(startingPrice);
        c.setCurrentBid(startingPrice); // initial current bid equals starting price
        c.setEndTime(endTime);
        c.setSellerName(sellerName);
        c.setSellerAddress(sellerAddress);
        c.setSellerId(sellerId);
        c.setImageUrl(imageUrl);

        return repo.save(c);
    }
}