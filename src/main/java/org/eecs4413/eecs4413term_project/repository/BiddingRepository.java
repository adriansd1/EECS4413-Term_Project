package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiddingRepository extends JpaRepository<BiddingClass, Long> {
    // Spring Data JPA provides save(), findById(), findAll(), etc.
}
