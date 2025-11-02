package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.BiddingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- Make sure to import List

@Repository
public interface BiddingRepository extends JpaRepository<BiddingClass, Long> {
    
    /**
     * Finds all bids associated with a specific auction,
     * ordered by bid time (e.g., newest first).
     * This supports GET /api/bids?auctionId={id}
     */
    List<BiddingClass> findByAuctionIdOrderByBidTimeDesc(Long auctionId);

    /**
     * Finds all bids placed by a specific user,
     * ordered by bid time (e.g., newest first).
     * This supports GET /api/bids?userId={id}
     */
    List<BiddingClass> findByUserIdOrderByBidTimeDesc(Long userId);
}