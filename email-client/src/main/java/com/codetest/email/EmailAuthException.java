package com.codetest.email;

/**
 * Thrown by {@link EmailClient#send} when the supplied credentials are
 * rejected by the upstream mail relay.
 **/

public final class EmailAuthException extends RuntimeException {
    public EmailAuthException(String message) {
        super(message);
    }
}
