package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PurchasesRepository extends JpaRepository<Purchases, UUID> {
}