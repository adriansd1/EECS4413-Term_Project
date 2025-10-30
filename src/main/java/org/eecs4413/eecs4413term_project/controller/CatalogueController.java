package org.eecs4413.eecs4413term_project.controller;

import jakarta.servlet.http.HttpSession;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    // --- UC2.3: Select an item for this session ---
    @PostMapping("/select/{id}")
    public ResponseEntity<?> selectItem(@PathVariable Long id, HttpSession session) {
        // Enforce only one item per session
        if (session.getAttribute("selectedItemId") != null) {
            return ResponseEntity.badRequest().body("An item has already been selected this session.");
        }

        Optional<Catalogue> itemOpt = service.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        session.setAttribute("selectedItemId", id);
        return ResponseEntity.ok(itemOpt.get());
    }

    // --- UC2.3: Get the selected item details ---
    @GetMapping("/selected")
    public ResponseEntity<?> getSelectedItem(HttpSession session) {
        Object selectedId = session.getAttribute("selectedItemId");
        if (selectedId == null) {
            return ResponseEntity.badRequest().body("No item selected for this session.");
        }

        Optional<Catalogue> itemOpt = service.findById(Long.valueOf(selectedId.toString()));
        return itemOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
