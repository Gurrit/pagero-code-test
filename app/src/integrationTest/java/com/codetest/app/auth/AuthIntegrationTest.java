package com.codetest.app.auth;

import com.codetest.app.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Worked example showing how to write integration tests against the
 * wired-up Spring context. The tests below exercise the auth bridge,
 * which is already implemented for you — feel free to delete them once
 * you've written your own coverage for the endpoints.
 */
class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void rejects_request_without_bearer_token() {
        ResponseEntity<String> response = http.exchange(
                "/users/" + ALICE_ID + "/emails",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void rejects_request_with_expired_token() {
        ResponseEntity<String> response = http.exchange(
                "/users/" + ALICE_ID + "/emails",
                HttpMethod.GET,
                new HttpEntity<>(bearer(EXPIRED_TOKEN)),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
