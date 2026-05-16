package com.codetest.app.web;

import com.codetest.app.auth.AuthenticatedUser;
import com.codetest.app.database.dto.EmailDTO;
import com.codetest.app.util.Printer;
import com.codetest.auth.UserDetails;
import com.codetest.email.EmailClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "Send an email", description = "Persists an email row and attempts delivery via the upstream mail server. The sender is taken from the JWT-resolved caller, not from the request body. The row is persisted regardless of delivery outcome; the persisted status reflects whether delivery succeeded.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The email was successfully accepted and persisted."),
            @ApiResponse(responseCode = "400", description = "The request body was malformed or failed validation.", content = @Content),
            @ApiResponse(responseCode = "401", description = "The provided JWT was invalid or missing.", content = @Content)
    })
    @PostMapping("/emails")
    public ResponseEntity<Void> send(
            @Parameter(hidden = true) @AuthenticatedUser UserDetails caller,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The email to send.", required = true, content = @Content(schema = @Schema(implementation = SendEmailRequest.class))) @RequestBody SendEmailRequest request) {

        Printer.debug("The send() endpoint has been reached for the user: " + caller);
        Printer.debug("Sending mail: " + request);

        emailService.send(caller, request);
        Printer.debug("Managed to send the mail.");
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Operation(summary = "List emails sent by a user", description = "Returns the emails sent by the user identified by the path variable, newest first. Callers may only view their own history — a 403 is returned if the JWT-resolved caller does not match {userId}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The emails sent by the user.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmailDTO.class)))),
            @ApiResponse(responseCode = "401", description = "The provided JWT was invalid or missing.", content = @Content),
            @ApiResponse(responseCode = "403", description = "The caller tried to view another user's email history.", content = @Content)
    })
    @GetMapping("/users/{userId}/emails")
    public ResponseEntity<List<EmailDTO>> listSentBy(
            @Parameter(hidden = true) @AuthenticatedUser UserDetails caller,
            @Parameter(description = "The id of the user whose sent emails should be listed.") @PathVariable String userId) {

        if (!caller.id().equals(userId)) {
            Printer.security("The caller " + caller + " tried to access the mails of userId " + userId);
        }
        Printer.debug("Reached the listSentBy endpoint. ");

        throw new UnsupportedOperationException("GET /users/{userId}/emails not implemented");
    }
}
