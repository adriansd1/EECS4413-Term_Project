package org.eecs4413.eecs4413term_project.repository;

import org.eecs4413.eecs4413term_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA automatically provides methods like save(), findAll(), findById()
}