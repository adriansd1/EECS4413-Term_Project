package org.eecs4413.eecs4413term_project.service;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CatalogueService {

    private final CatalogueRepository repo;

    public CatalogueService(CatalogueRepository repo) {
        this.repo = repo;
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
        List<Catalogue> active = repo.findActiveAuctions(now);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Catalogue c : active) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getTitle());
            item.put("currentBid", c.getCurrentBid());
            item.put("type", c.getType());
            item.put("timeLeft", formatTimeLeft(now, c.getEndTime()));
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
}
