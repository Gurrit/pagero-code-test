package com.codetest.app.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Usage:</p>
 * <pre>
 *   {@code
 *   @PostMapping("/emails")
 *      public ResponseEntity<?> send(@AuthenticatedUser UserDetails, ...) { ... }
 *   }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUser {
}
