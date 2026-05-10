# `app` module

This is the workspace for the exercise. The framework and the database
are wired up — you're filling in controllers, services and persistence.

See the top-level [`README.md`](../README.md) for the exercise spec.

## Source layout

```
src/
├── main/
│   ├── java/com/codetest/app/
│   │   ├── CodeTestApplication.java        # Spring Boot entry point
│   │   ├── auth/                           # @AuthenticatedUser bridge (wiring done; one method TODO)
│   │   ├── config/                         # ProvidedModulesConfig, WebConfig
│   │   └── web/EmailController.java        # routes mapped, bodies TODO
│   └── resources/
│       ├── application.yml                 # H2 + Flyway
│       └── db/migration/V1__init.sql       # emails table
├── test/java/                              # unit tests — run with `mvn test`
└── integrationTest/java/                   # integration tests — run with `mvn verify`
```

## The `@AuthenticatedUser` argument resolver

Spring MVC lets you write controller methods like this:

```java
@PostMapping("/emails")
public ResponseEntity<?> send(@AuthenticatedUser UserDetails caller,
                              @RequestBody SendEmailRequest body) { ... }
```

…and have the framework figure out where the `UserDetails` value comes
from. The "figuring out" is delegated to a
[`HandlerMethodArgumentResolver`][resolver-docs]: a class that says "I
know how to provide values for parameters of *this* shape" and then
returns the value. Other built-in resolvers do the same thing for
`@RequestBody`, `@PathVariable`, `@RequestParam`, etc.

[resolver-docs]: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/custom-arguments.html

### What's already wired

- `@AuthenticatedUser` — a marker annotation you stick on controller
  parameters.
- `AuthenticatedUserArgumentResolver`:
    - `supportsParameter(...)` — already implemented: it claims any
      parameter that's both annotated with `@AuthenticatedUser` and typed
      as `UserDetails`.
    - `resolveArgument(...)` — already implemented: pulls the
      `HttpServletRequest` out of the `NativeWebRequest` and delegates to
      `resolveUserDetails(request)`.
- `WebConfig` — registers the resolver on Spring's MVC pipeline so the
  whole thing actually runs.

### What you implement

The body of `resolveUserDetails(HttpServletRequest request)`. Whatever
non-null `UserDetails` you return is what Spring injects into the
controller parameter; throwing causes the controller to never be
invoked.

The contract:

1. Pull the bearer token out of the `Authorization` header
   (`request.getHeader("Authorization")`, strip the `"Bearer "` prefix).
2. Hand the token to `authService.authenticate(...)`.
3. If you get `AuthResult.Authenticated`, return its `UserDetails`. The
   controller method now receives it.
4. If you get `AuthResult.Failed` — or the header is missing or
   malformed — surface a failure that maps to HTTP **401**. The simplest
   way is `throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
   "...")`: Spring's default exception mapping turns that into a 401
   response for you, no `@RestControllerAdvice` required.

The two failing tests in `AuthIntegrationTest` are your acceptance
check — once `resolveUserDetails` is right, both should go green.

### Not your style?

The argument-resolver setup is there to remove a choice you'd otherwise
spend time on — but you don't have to use it. If you'd rather wire the
auth check as an `OncePerRequestFilter`, a `HandlerInterceptor`, a
`@RestControllerAdvice` reading the header in each handler, or
something else entirely, go ahead. Delete the
`auth/` scaffolding and roll your own. The acceptance checks in
`AuthIntegrationTest` only assert on HTTP behaviour (`401` on
missing/expired tokens), so any approach that satisfies them is fine.

## Tests

There are two test source roots, each with a different purpose and runtime
cost.

### Unit tests — `src/test/java`

- Run during `mvn test`.
- Should be fast and should **NOT** boot a Spring context — use plain
  JUnit 5 + (optionally) Mockito to exercise a single class in isolation.
- `spring-boot-starter-test` already brings JUnit 5, AssertJ and Mockito
  for you.
- `ExampleUnitTest.java` is a one-line placeholder so the directory isn't
  empty. Delete it once you have real unit tests.

### Integration tests — `src/integrationTest/java`

- Run during `mvn verify` (so they're skipped during `mvn test`).
- Extend `AbstractIntegrationTest` to get the full Spring context on a
  random port, a preconfigured `TestRestTemplate`, a `JdbcTemplate`, a
  per-test `emails`-table reset, and constants for the pre-baked
  tokens / user ids.
- `AuthIntegrationTest` next to it ships two **failing** examples
  (missing-token → 401, expired-token → 401). They're failing on
  purpose — they're the acceptance check for the auth resolver
  you're going to implement. They go green once
  `AuthenticatedUserArgumentResolver.resolveUserDetails` is done. Use
  the same shape for your own endpoint tests.

### Commands

```bash
./mvnw -pl app test     # unit tests only (fast)
./mvnw -pl app verify   # unit tests + integration tests
```
