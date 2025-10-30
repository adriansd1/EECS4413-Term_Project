package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long> {

    // --- UC2.1: Search catalogue items ---
    @Query("SELECT c FROM Catalogue c " +
            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Catalogue> searchByKeyword(String keyword);

    // --- UC2.2: Display active auctions ---
    @Query("SELECT c FROM Catalogue c WHERE c.endTime > :now ORDER BY c.endTime ASC")
    List<Catalogue> findActiveAuctions(LocalDateTime now);
}
