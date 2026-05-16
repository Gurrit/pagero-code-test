package com.codetest.app.web;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.codetest.app.database.dto.EmailDTO;
import com.codetest.app.database.entities.Email;
import com.codetest.app.database.repository.EmailRepository;
import com.codetest.app.util.Printer;
import com.codetest.auth.UserDetails;
import com.codetest.email.EmailClient;

/**
 * Handles the persistence + delivery logic for emails. The controller
 * should stay thin and delegate to this service.
 */
@Service
public class EmailService {

    @Value("${email-config.url}")
    private String emailServerUrl;

    @Value("${email-config.username}")
    private String emailServerUsername;

    @Value("${email-config.password}")
    private String emailServerPassword;

    private final EmailClient emailClient;

    private final EmailRepository emailRepository;

    public EmailService(EmailClient emailClient, EmailRepository emailRepository) {
        this.emailClient = emailClient;
        this.emailRepository = emailRepository;
    }

    /**
     * Persists the email row and attempts delivery via the legacy
     * {@link EmailClient}. The row must be persisted regardless of
     * whether delivery succeeded — the status column reflects the
     * outcome.
     * 
     * @param caller  The sender of the email
     * @param request The data of the email to be sent
     * @throws ResponseStatusException If the mail couldn't be sent.
     */
    public void send(UserDetails caller, SendEmailRequest request) throws ResponseStatusException {
        Email mail = newPendingEmail(caller, request);

        try {
            emailClient.send(emailServerUrl, emailServerUsername, emailServerPassword, caller.email(), request.to(),
                    request.subject(), request.body());
            mail.setStatus(Email.Status.SENT);

        } catch (Exception e) {
            mail.setStatus(Email.Status.FAILED);
            emailRepository.saveAndFlush(mail);
            Printer.error("The email was not sent as it should have.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "There was an error when sending the mail. The mail has not been sent");
        }
    }

    /**
     * Returns the emails sent by {@code userId}. Callers may only view
     * their own history — the controller is responsible for enforcing
     * that {@code caller.id().equals(userId)}.
     */
    public List<EmailDTO> listSentBy(UserDetails caller, String userId) throws IllegalArgumentException {
        Printer.debug("EmailService.listSentBy invoked by " + caller + " for " + userId);
        if (!caller.id().equals(userId)) {
            Printer.security("listSentBy method got breached by the caller " + caller + " with userId " + userId);
            throw new IllegalArgumentException();
        }

        List<Email> emails = emailRepository.findBySenderIdOrderByCreatedAtDesc(UUID.fromString(userId));
        List<EmailDTO> emailDTOs = new ArrayList<>();

        for (Email email : emails)
            emailDTOs.add(EmailDTO.from(email));

        return emailDTOs;

    }

    /**
     * Creates a new email row and marks it as PENDING.
     * 
     * @param caller  The sender of the email
     * @param request The email details
     * @return An Email object.
     */
    private Email newPendingEmail(UserDetails caller, SendEmailRequest request) {
        Email email = new Email();
        email.setSenderEmail(caller.email());
        email.setSenderId(UUID.fromString(caller.id()));

        email.setStatus(Email.Status.PENDING);
        email.setBody(request.body());
        email.setCreatedAt(Instant.now());
        email.setSubject(request.subject());
        email.setRecipient(request.to());
        return emailRepository.saveAndFlush(email);
    }
}
