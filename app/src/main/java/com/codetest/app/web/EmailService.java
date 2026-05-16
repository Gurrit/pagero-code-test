package com.codetest.app.web;

import com.codetest.app.database.entities.Email;
import com.codetest.app.database.repository.EmailRepository;
import com.codetest.app.util.Printer;
import com.codetest.auth.UserDetails;
import com.codetest.email.EmailAuthException;
import com.codetest.email.EmailClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     */
    public void send(UserDetails caller, SendEmailRequest request) {
        Printer.debug("EmailService.send invoked by " + caller);

    }

    /**
     * Returns the emails sent by {@code userId}. Callers may only view
     * their own history — the controller is responsible for enforcing
     * that {@code caller.id().equals(userId)}.
     */
    public void listSentBy(UserDetails caller, String userId) {
        Printer.debug("EmailService.listSentBy invoked by " + caller + " for " + userId);
    }
}
