package com.codetest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication façade. Provided as a dependency — do not modify.
 *
 * <p>{@link #authenticate(String)} accepts the raw JWT string (i.e. the value
 * of an {@code Authorization: Bearer <token>} header, with the {@code Bearer }
 * prefix already stripped by the caller). It decodes the body without
 * verifying the signature — this is a code-test stub, not a real auth
 * service. Test tokens shipped alongside this module use {@code alg: none}
 * for that reason.
 */
public final class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JwtParser parser = Jwts.parser().unsecured().build();

    public AuthResult authenticate(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            return new AuthResult.Failed(new AuthError.Missing("authorization token is missing"));
        }

        try {
            Claims claims = parser.parseUnsecuredClaims(jwt).getPayload();

            String id = claims.getSubject();
            if (id == null || id.isBlank()) {
                return new AuthResult.Failed(new AuthError.Malformed("token is missing 'sub' claim"));
            }

            String username = claims.get("username", String.class);
            String email = claims.get("email", String.class);

            log.debug("authenticated user id={} username={}", id, username);
            return new AuthResult.Authenticated(new UserDetails(id, username, email));

        } catch (ExpiredJwtException e) {
            return new AuthResult.Failed(new AuthError.Expired(
                "token expired at " + e.getClaims().getExpiration()));
        } catch (JwtException | IllegalArgumentException e) {
            return new AuthResult.Failed(new AuthError.Malformed(
                "token could not be parsed: " + e.getMessage()));
        }
    }
}
