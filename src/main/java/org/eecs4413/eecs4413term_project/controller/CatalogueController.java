package org.eecs4413.eecs4413term_project.controller;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final CatalogueService service;

    public CatalogueController(CatalogueService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<Catalogue> searchCatalogue(@RequestParam(required = false) String keyword) {
        return service.search(keyword);
    }
}
