package com.codetest.app.database.repository;

import com.codetest.app.database.entities.Email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Email}. The standard CRUD
 * methods come from {@link JpaRepository}; the derived query below
 * powers {@code GET /users/{userId}/emails}.
 */
@Repository
public interface EmailRepository extends JpaRepository<Email, UUID> {

    /** Emails sent by {@code senderId}, newest first. */
    List<Email> findBySenderIdOrderByCreatedAtDesc(UUID senderId);
}
