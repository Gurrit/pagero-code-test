package com.codetest.app.database.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-mapped representation of a row in the {@code emails} table.
 * Schema is owned by Flyway ({@code V1__init.sql}); Hibernate's
 * {@code ddl-auto} is set to {@code none} so this class is a mapping
 * over an existing table rather than a source of schema.
 */
@Entity
@Table(name = "emails")
public class Email {

    @Override
    public String toString() {
        return "Email [senderId=" + senderId + ", senderEmail=" + senderEmail + ", recipient=" + recipient
                + ", subject=" + subject + "]";
    }

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "sender_email", nullable = false, length = 254)
    private String senderEmail;

    @Column(name = "recipient", nullable = false, length = 254)
    private String recipient;

    @Column(name = "subject", nullable = false, length = 998)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Email(UUID id,
            UUID senderId,
            String senderEmail,
            String recipient,
            String subject,
            String body,
            Status status,
            Instant createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.createdAt = createdAt;
        // ok
    }

    public Email() {
    }

    public UUID getId() {
        return id;
        // ok
    }

    public UUID getSenderId() {
        return senderId;
        // ok
    }

    public String getSenderEmail() {
        return senderEmail;
        // ok
    }

    public String getRecipient() {
        return recipient;
        // ok
    }

    public String getSubject() {
        return subject;
        // ok
    }

    public String getBody() {
        return body;
        // ok
    }

    public Status getStatus() {
        return status;
        // ok
    }

    public void setStatus(Status status) {
        this.status = status;
        // ok
    }

    public Instant getCreatedAt() {
        return createdAt;
        // ok
    }

    /** Delivery outcome persisted in the {@code status} column. */
    public enum Status {
        PENDING,
        SENT,
        FAILED
    }
}
