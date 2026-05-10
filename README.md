# Pagero Code Test

Welcome! This is a small backend coding exercise. You'll build two HTTP endpoints
on top of a few pre-built modules. The goal is to see how you structure code,
handle integration points, and reason about a small but realistic system.

**Expected time:** 3–4 hours. Don't sink a whole weekend into this — if you're
running over, stop and write down what you'd do next.

---

## Scenario

You are extending an internal service that lets authenticated users send emails
and look at the history of what's been sent. The email-sending machinery and
authentication are already built (by another team) and are provided as
dependencies. Your job is to write the HTTP layer and persistence on top.

---

## What's provided

This is a multi-module Maven project. Two modules are pre-built and **must
not be modified**:

### `email-client/` — DO NOT MODIFY
A legacy email client. The only thing you need from it is:

```java
public final class EmailClient {
    public void send(String from, String to, String subject, String body);
}
```

Don't worry — it doesn't actually send mail. It logs the email and returns.
Treat it like any third-party SDK: integrate with the API as it is.

### `auth-server/` — DO NOT MODIFY
Authentication is exposed as:

```java
public final class AuthService {
    public AuthResult authenticate(String jwt);
}

// AuthResult is a sealed interface with two cases:
//   Authenticated(UserDetails user)
//   Failed(AuthError error)
```

`authenticate` decodes the JWT body (without verifying the signature — this is
a test, not production) and returns the user, or an error if the token is
malformed or expired. You don't need to understand JWT internals; just call
this method with the value of the `Authorization: Bearer <token>` header.

A handful of pre-baked tokens for testing are listed at the bottom of this
README.

### `app/` — this is where you work
A Spring Boot 3 / Java 21 application skeleton. It already has the two modules
above on its classpath and a Postgres connection configured. There is one
Flyway migration in `app/src/main/resources/db/migration/V1__init.sql` that
creates the `emails` table for you. You can add further migrations if you
want, but you don't have to.

The app has **no controllers, services, or repositories yet**. Structuring
the code is part of the exercise — pick a layout that makes sense to you.

### Infrastructure
At the repo root:
- `docker-compose.yml` — starts Postgres and the app together.
- `Dockerfile` — builds the app image.

Postgres runs on `localhost:5432` with database `codetest`, user `codetest`,
password `codetest`. The app is wired to connect to it both when running in
the compose network and when running locally against `localhost`.

---

## What you need to build

Two HTTP endpoints in the `app` module:

### 1. `POST /emails` — send an email

- Requires `Authorization: Bearer <token>`. Reject with **401** if missing or
  invalid.
- Request body (JSON):
  ```json
  {
    "to": "alice@example.com",
    "subject": "Hello",
    "body": "Hi there"
  }
  ```
- Behaviour:
  1. Authenticate the caller.
  2. Persist a record of the email (sender = authenticated user, plus
     to/subject/body and a sent-at timestamp).
  3. Call `EmailClient.send(...)` to "deliver" it.
- Response: **201 Created** with the persisted email (including its id and
  sent-at timestamp).

### 2. `GET /users/{userId}/emails` — list a user's sent emails

- Requires `Authorization: Bearer <token>`. Reject with **401** if missing or
  invalid.
- **No authorization check beyond authentication.** Any authenticated user may
  view any other user's history. (Yes, this is deliberate. We're not asking
  you to fix it.)
- Returns all emails *sent by* the user with id `userId`, newest first.
- Response: **200 OK** with a JSON array. Empty array if the user has sent
  nothing (or doesn't exist).

You decide the exact response shapes, validation rules for malformed input,
and how to model the persistence layer. There isn't one right answer — pick
something reasonable and be ready to talk about your choices.

---

## Running it

The fastest way:

```bash
docker compose up --build
```

This builds the app image and starts Postgres + the app together. The app
listens on `http://localhost:8080`.

If you'd rather iterate locally (faster reloads), run just the database in
Docker and the app from your IDE / Maven:

```bash
docker compose up -d postgres
./mvnw -pl app spring-boot:run
```

---

## What we'll look at

In rough order of importance:

1. **Does it work?** Both endpoints behave as specified.
2. **Code structure.** Separation between HTTP, domain, and persistence.
   Reasonable naming. We're not looking for elaborate architecture — just
   clarity.
3. **Tests.** At least one meaningful test per endpoint. Unit or integration,
   your call. We care more about *what* you test than how many tests there are.
4. **Error handling.** What happens on bad JSON, missing fields, an unknown
   user id, an expired token? You don't need to handle every edge case, but
   show you've thought about it.
5. **Trade-offs you can articulate.** It's fine to say "I'd do X in a real
   service but skipped it here because Y." Note these in a `NOTES.md` if you
   like, or just be ready to discuss in the follow-up.

We are **not** evaluating:
- Whether you secured `GET /users/{userId}/emails` — the spec says don't.
- Fancy frameworks or abstractions you wouldn't use in real life.
- Frontend, deployment, or observability.

---

## Test tokens

The auth module recognises these tokens (the JWT bodies decode to the
following users — signatures aren't checked):

| Token         | User id                                | Username |
|---------------|----------------------------------------|----------|
| (filled in once auth-server is wired up — see `auth-server/README.md`) | | |

---

## Submitting

Push to a fresh branch and share the link, or zip the repo. Please include any
notes about what you'd do with more time.

Good luck, and have fun.
