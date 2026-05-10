package com.codetest.auth;

public sealed interface AuthError {

    String message();

    record Missing(String message) implements AuthError {}

    record Malformed(String message) implements AuthError {}

    record Expired(String message) implements AuthError {}
}
