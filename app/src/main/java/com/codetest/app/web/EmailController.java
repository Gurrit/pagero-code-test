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
 * * what the request body for {@code POST /emails} looks like (replace the {@code Object} parameter with your own DTO)

 * * what the response body looks like (replace {@code ?} in {@code ResponseEntity<?>} and return the appropriate status)

 * * where the persistence + delivery logic lives — usually a service behind this controller
 *
 */
@RestController
public class EmailController {

    @PostMapping("/emails")
    public ResponseEntity<?> send(
            @AuthenticatedUser UserDetails caller,
            @RequestBody SendEmailRequest request
    ) {
        // TODO (candidate):
        //   1. Fill in the fields on SendEmailRequest.
        //   2. Persist the email (sender = caller, plus to/subject/body
        //      and a server-assigned sent_at).
        //   3. Call EmailClient.send(...).
        //   4. Return 201 Created with the persisted email.
        throw new UnsupportedOperationException("POST /emails not implemented");
    }

    @GetMapping("/users/{userId}/emails")
    public ResponseEntity<?> listSentBy(
            @AuthenticatedUser UserDetails caller,
            @PathVariable String userId
    ) {
        // TODO (candidate):
        //   1. Enforce that `userId` matches the authenticated caller.
        //   2. Load all emails sent by `userId`, newest first.
        //   3. Return 200 OK with a JSON array (empty if none).
        throw new UnsupportedOperationException("GET /users/{userId}/emails not implemented");
    }
}
