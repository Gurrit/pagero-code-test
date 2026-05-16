package com.codetest.app.web;

/** Request body for {@code POST /emails}. */
public record SendEmailRequest(
        String subject,
        String body,
        String to) {

    @Override
    public String toString() {
        return "SendEmailRequest [to=" + to + ", subject=" + subject + "]";
    }
}
