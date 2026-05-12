package com.codetest.app.web;

import com.codetest.app.auth.AuthenticatedUser;
import com.codetest.auth.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry points for the exercise. Routes, parameter binding and the
 * auth hook-up are wired for you — request/response shapes and the
 * implementation are up to you.
 *
 * You decide:
 *
 * * what the request body for {@code POST /emails} looks like (implement the {@code SendEmailRequest} class)
 * * what the response body looks like (replace {@code ?} in {@code ResponseEntity<?>} and return the appropriate status)
 * * where the persistence + delivery logic lives
 *
 */
@RestController
public class EmailController {

    @PostMapping("/emails")
    public ResponseEntity<?> send(
            @AuthenticatedUser UserDetails caller,
            @RequestBody SendEmailRequest request
    ) {
        // TODO (candidate): Implement this flow endpoint end-2-end
        throw new UnsupportedOperationException("POST /emails not implemented");
    }

    @GetMapping("/users/{userId}/emails")
    public ResponseEntity<?> listSentBy(
            @AuthenticatedUser UserDetails caller,
            @PathVariable String userId
    ) {
        // TODO (candidate): Implement this flow endpoint end-2-end
        throw new UnsupportedOperationException("GET /users/{userId}/emails not implemented");
    }
}
