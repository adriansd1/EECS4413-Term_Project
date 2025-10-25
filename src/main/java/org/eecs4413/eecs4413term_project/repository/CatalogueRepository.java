package org.eecs4413.eecs4413term_project.repository;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long> {

    @Query("SELECT c FROM Catalogue c " +
            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Catalogue> searchByKeyword(String keyword);
}

