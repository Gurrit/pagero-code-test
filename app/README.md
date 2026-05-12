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
from. 

### What's already wired

- `@AuthenticatedUser` — a marker annotation you stick on controller
  parameters.
- `AuthenticatedUserArgumentResolver`: mostly implemented, what you need to implement is the `resolveUserDetails` method. 
- throwing `ResponseStatusException` with a status will be propagated to the HTTP response

The two failing tests in `AuthIntegrationTest` are your acceptance
check — once `resolveUserDetails` is right, both should go green.

### Not your style?

The argument-resolver setup is there to remove a choice you'd otherwise
spend time on — but you don't have to use it. If you'd rather do something else, go ahead!

## Tests

There are two test source roots, each with a different purpose and runtime
cost.

### Unit tests — `src/test/java`

- Run during `mvn test`.
- Should be fast and should **NOT** boot a Spring context, use plain
  JUnit 5 + and optionally mocks to test a single class in isolation.
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
  `AuthenticatedUserArgumentResolver.resolveUserDetails` is done.

### Commands

```bash
./mvnw -pl app test     # unit tests only (fast)
```
```bash
./mvnw -pl app verify   # unit tests + integration tests
```
