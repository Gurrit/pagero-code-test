package com.codetest.app.database.dto;

import com.codetest.app.database.entities.Email;

import java.time.Instant;

public record EmailDTO(
        String sender,
        String reciever,
        Instant sentAt,
        String subject,
        String body) {

    public static EmailDTO from(Email email) {
        return new EmailDTO(
                email.getSenderEmail(),
                email.getRecipient(),
                email.getCreatedAt(),
                email.getSubject(),
                email.getBody());
    }
}
