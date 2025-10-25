package org.eecs4413.eecs4413term_project.service;

import org.springframework.stereotype.Service;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.repository.CatalogueRepository;
import java.util.List;

@Service
public class CatalogueService {

    private final CatalogueRepository repo;

    public CatalogueService(CatalogueRepository repo) {
        this.repo = repo;
    }

    public List<Catalogue> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(); // Gracefully return all items
        }
        return repo.searchByKeyword(keyword.trim());
    }
}
