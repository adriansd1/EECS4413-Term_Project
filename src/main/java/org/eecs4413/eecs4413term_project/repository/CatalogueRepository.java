package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long> {

    // UC2.1: Search catalogue items
    @Query("""
        SELECT c FROM Catalogue c
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Catalogue> searchByKeyword(String keyword);

    // UC2.2: Display active auctions
   @Query("""
        SELECT c FROM Catalogue c
        WHERE c.endTime > :cutoff
        ORDER BY c.endTime ASC
    """)
    List<Catalogue> findActiveAuctions(LocalDateTime cutoff);

    // UC2.3: Select one auction item
    // (JpaRepository already includes findById)
    @Override
    Optional<Catalogue> findById(Long id);

    // UC7: Seller uploads new auction item
    // Optional: Check for duplicates before saving
    @Query("""
        SELECT c FROM Catalogue c
        WHERE LOWER(c.title) = LOWER(:title)
          AND LOWER(c.description) = LOWER(:description)
    """)
    Optional<Catalogue> findDuplicate(String title, String description);
}
