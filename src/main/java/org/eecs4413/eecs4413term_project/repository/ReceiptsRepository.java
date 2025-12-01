package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.List;

public interface ReceiptsRepository extends JpaRepository<Receipt, UUID> {
    @Query("SELECT r FROM Receipt r WHERE r.purchase.purchaseId = :purchaseId")
    List<Receipt> findByPurchaseId(UUID purchaseId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Receipt r WHERE r.auctionId = :auctionId")
    boolean existsByAuctionId(UUID auctionId);
}
