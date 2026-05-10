package com.codetest.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Legacy email client. Provided as a dependency — do not modify.
 *
 * <p>In production this would talk to an SMTP relay. For the purposes of the
 * exercise it logs the email and returns. Treat the public API of this class
 * as fixed.
 */
public final class EmailClient {

    private static final Logger log = LoggerFactory.getLogger(EmailClient.class);

    public void send(String from, String to, String subject, String body) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("from must not be blank");
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("to must not be blank");
        }
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("body must not be null");
        }

        log.info(
            "[email-client] delivering email\n  from: {}\n  to: {}\n  subject: {}\n  body: {}",
            from, to, subject, body
        );
    }
}
