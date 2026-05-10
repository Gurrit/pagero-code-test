package com.codetest.auth;

public sealed interface AuthResult {

    record Authenticated(UserDetails user) implements AuthResult {}

    record Failed(AuthError error) implements AuthResult {}
}
