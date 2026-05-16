package com.codetest.app.web;

import com.codetest.app.auth.AuthenticatedUser;
import com.codetest.app.util.Printer;
import com.codetest.auth.UserDetails;
import com.codetest.email.EmailClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * * what the request body for {@code POST /emails} looks like (implement the
 * {@code SendEmailRequest} class)
 * * what the response body looks like (replace {@code ?} in
 * {@code ResponseEntity<?>} and return the appropriate status)
 * * where the persistence + delivery logic lives
 *
 */
@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Sends an email to a person", description = "This operation sends an email to a person who is defined by the request parameter. The caller must be an AuthenticatedUser with the correct UserDetails in order for this method to succeed.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The email was succesfully sent"),
            @ApiResponse(responseCode = "401", description = "The provided JWT was invalid or missing.")
    })
    @PostMapping("/emails")
    public ResponseEntity<Void> send(
            @Parameter(description = "The authenticated caller of this endpoint. ", schema = @Schema(implementation = UserDetails.class)) @AuthenticatedUser UserDetails caller,
            @Parameter(description = "The request to actually send an email to a person. ", schema = @Schema(implementation = UserDetails.class)) @RequestBody SendEmailRequest request) {

        Printer.debug("The send() endpoint has been reached for the user: " + caller);
        Printer.debug("Sending mail: " + request);

        emailService.send(caller, request);

        throw new UnsupportedOperationException("POST /emails not implemented");
    }

    @Operation(summary = "Lists emails sent by a user", description = "This operation lists the emails that were sent by the user identified by the path variable. The caller must be an AuthenticatedUser with the correct UserDetails in order for this method to succeed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The emails were succesfully retrieved"),
            @ApiResponse(responseCode = "401", description = "The provided JWT was invalid or missing.")
    })
    @GetMapping("/users/{userId}/emails")
    public ResponseEntity<?> listSentBy(
            @Parameter(description = "The authenticated caller of this endpoint. ", schema = @Schema(implementation = UserDetails.class)) @AuthenticatedUser UserDetails caller,
            @Parameter(description = "The id of the user whose sent emails should be listed. ") @PathVariable String userId) {
        // TODO (candidate): Implement this flow endpoint end-2-end
        throw new UnsupportedOperationException("GET /users/{userId}/emails not implemented");
    }
}
