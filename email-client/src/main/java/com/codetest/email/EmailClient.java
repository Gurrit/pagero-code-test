package com.codetest.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Legacy email client. Provided as a dependency — do not modify.
 */
public final class EmailClient {

    private static final Logger log = LoggerFactory.getLogger(EmailClient.class);

    // Canonical "valid" credentials. Anything else fails authentication.
    private static final String VALID_URL = "https://smtp.example.com/send";
    private static final String VALID_USERNAME = "mailer-bot";
    private static final String VALID_PASSWORD = "s3cr3t";

    public void send(
            String serverUrl,
            String serverUsername,
            String serverPassword,
            String from,
            String to,
            String subject,
            String body
    ) {
        if (!VALID_URL.equals(serverUrl)
                || !VALID_USERNAME.equals(serverUsername)
                || !VALID_PASSWORD.equals(serverPassword)) {
            throw new EmailAuthException("relay rejected credentials");
        }

        log.info(
            "[email-client] delivering email via {}\n  from: {}\n  to: {}\n  subject: {}\n  body: {}",
                serverUrl, from, to, subject, body
        );
    }
}
