package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.AuctionClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionClass, Long> {
    
    /**
     * This is a custom query method that Spring Data JPA will automatically implement.
     * It finds all auctions where 'isClosed' is false AND 'endTime' is before the given time.
     * This is used by the AuctionSchedulerService.
     */
    List<AuctionClass> findAllByIsClosedFalseAndEndTimeBefore(LocalDateTime time);
}

