# auth-server (provided)

Authentication façade. **Do not modify.**

## API

```java
public final class AuthService {
    public AuthResult authenticate(String jwt);
}

public sealed interface AuthResult {
    record Authenticated(UserDetails user) implements AuthResult {}
    record Failed(AuthError error)         implements AuthResult {}
}

public sealed interface AuthError {
    String message();
    record Missing(String message)   implements AuthError {}
    record Malformed(String message) implements AuthError {}
    record Expired(String message)   implements AuthError {}
}

public record UserDetails(String id, String username, String email) {}
```

## How it works

`authenticate` decodes the JWT body **without verifying the signature**. In
production a service like this would verify against a public key; for the
purposes of the code test this is deliberately stubbed.

The JWTs we ship for testing use `alg: none`. The parser is configured to
accept those.

The token is read from the body's claims:

| JWT claim  | Mapped to             |
|------------|-----------------------|
| `sub`      | `UserDetails.id`      |
| `username` | `UserDetails.username`|
| `email`    | `UserDetails.email`   |
| `exp`      | rejected if in the past |

Test tokens are listed in the [main README](../README.md#test-tokens).
