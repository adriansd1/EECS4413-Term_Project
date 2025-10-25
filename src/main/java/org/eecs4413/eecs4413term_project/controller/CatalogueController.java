package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final CatalogueService service;

    public CatalogueController(CatalogueService service) {
        this.service = service;
    }

    // --- UC2.1: Search catalogue items ---
    @GetMapping("/search")
    public List<Catalogue> searchCatalogue(@RequestParam(required = false) String keyword) {
        return service.search(keyword);
    }

    // --- UC2.2: Display active auction items ---
    @GetMapping("/active")
    public List<Map<String, Object>> getActiveItems() {
        return service.getActiveAuctions();
    }
}
