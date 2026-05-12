# Wire-Me-Up Code Test

Welcome! This is a small backend coding exercise. You'll build two HTTP endpoints
on top of a few pre-built modules. The goal is to see how you structure code,
handle integration points, and reason about a small but realistic system.

**Expected time:** 3–4 hours. Don't sink a whole weekend into this — if you're
running over, stop and write down what you'd do next.

If you'd like a suggested path through the exercise, see
[`GETTING_STARTED.md`](GETTING_STARTED.md). Otherwise read on for the spec.

---

## Scenario

You are extending an internal service that lets authenticated users send emails
and look at the history of what's been sent. The email-sending machinery and
authentication are already built (by another team) and are provided as
dependencies. Your job is to write the HTTP layer and persistence on top.

---

## What's provided

This is a multi-module Maven project.

```
.
├── email-client/   # provided — do not modify (third-party-style SDK)
├── auth-server/    # provided — do not modify (JWT decoding)
└── app/            # your workspace (Spring Boot 3 / Java 21)
```

feel free to update build tooling as you wish as long as `email-client` and `auth-server` stays the same.
Two modules are pre-built and **must not be modified**:

### `email-client/` — DO NOT MODIFY
A legacy email client.

Don't worry — it doesn't actually deliver mail. just get the relay URL and the credentials used to authenticate against it 
from `application.yml` and pass it to the `send` method 
(there's some hardcoded assertions that the `url` / `username` / `password` matches the expectations).

### `auth-server/` — DO NOT MODIFY
Authentication server

the `authenticate` method decodes the JWT body and returns the user, or an error if the token is
malformed or expired. You don't need to understand JWT internals; just call
this method with the value of the `Authorization: Bearer <token>` header.

A handful of pre-baked tokens for testing are listed at the bottom of this
README.

### `app/` — this is where you work
A **Spring Boot 3 / Java 21** application skeleton. It already has:

- the two provided modules on the classpath, exposed as Spring beans in
  `ProvidedModulesConfig`
- an embedded **H2** database (in-memory) configured in `application.yml`
- **Flyway** wired up so the migration in
  `app/src/main/resources/db/migration/V1__init.sql` runs automatically on
  startup and creates the `emails` table
- a **controller skeleton** at `web/EmailController` with both routes
  (`POST /emails` and `GET /users/{userId}/emails`) mapped, parameter
  binding stub in place, and method bodies left as `TODO`s
- an **auth bridge skeleton** at `auth/AuthenticatedUserArgumentResolver`
  (registered in `config/WebConfig`). (see `GETTING_STARTED.md`)

See [`app/README.md`](app/README.md) for the source-tree layout and the
unit-vs-integration test split.

You decide the request/response shapes and the persistence/service layout
behind the controller.

### Infrastructure
At the repo root:
- `Dockerfile` — builds the app image (uses `spring-boot-maven-plugin` to
  produce an executable jar, no extra config needed).
- `docker-compose.yml` — runs the built app image on `http://localhost:8080`.

The database is **embedded H2 in-memory** — no external container needed,
no data on disk. State resets on every restart, If you want it to survive restarts, switch the URL in
`application.yml` to `jdbc:h2:file:./data/codetest`.

---

## What you need to build

Two HTTP endpoints in the `app` module:

### 1. `POST /emails` — send an email

- Requires `Authorization: Bearer <token>`. Reject with **401** if missing or
  invalid.
- The email row must be persisted **regardless of whether delivery
  succeeded** include the delivery status in the row . You decide what HTTP status to return on the delivery-failed path, just make sure the outcome is captured.
- Response on success: **201 Created** with the persisted email (including its id, status, and sent-at timestamp).

### 2. `GET /users/{userId}/emails` — list a user's sent emails

- Requires `Authorization: Bearer <token>`. Reject with **401** if missing or
  invalid.
- A user should only be allowed to view their own sent history.
- The user should be able to filter by whether the email was successfully sent or not.
- No pagination needed — return the full list.

You decide the exact response shapes, validation rules for malformed input,
and how to model the persistence layer. There isn't one right answer — pick
something reasonable and be ready to talk about your choices.

---

## Running it

The fastest way:

```bash
docker compose up --build
```

This builds the app image and starts it on `http://localhost:8080`. The
database is in-process, so there's nothing else to spin up.

If you'd rather iterate locally (faster reloads), run from Maven or your
IDE:

```bash
./mvnw -pl app spring-boot:run
```

---

## Scope

To keep the time-box honest, here's what's actually expected versus what's
just nice-to-have.

### Must

| Requirement                                                              |
|--------------------------------------------------------------------------|
| Both endpoints work as specified                                         |
| 401 on missing/invalid token                                             |
| Caller can only list their own sent history                              |
| Email is persisted **and** `EmailClient.send` is invoked                 |
| Delivery outcome (SENT / FAILED) is recorded on the row                  |
| A reasonable separation of HTTP, domain, persistence                     |
| At least one meaningful test                                             |

### Nice-to-have (skip if short on time)

| Idea                                                                     |
|--------------------------------------------------------------------------|
| Thoughtful error responses (problem+json, etc.)                          |
| Structured / contextual logging                                          |
| Integration test that exercises both endpoints                           |
| `NOTES.md` with trade-offs you'd revisit                                 |
| Input validation beyond what's required for 401                          |
| OpenAPI spec / API docs                                                  |

If you finish the "Must" list quickly, pick from "Nice-to-have" — but
don't feel obliged to clear it. We'd rather see a small, clean submission
than a sprawling one.
If you have any more suggestions, feel free to either implement the feature, or put it in `NOTES.md`

--- 

## What we'll look at

In rough order of importance:

1. **Does it work?** Both endpoints behave as specified.
2. **Code structure.** Separation between HTTP, domain, and persistence.
   Reasonable naming. We're not looking for elaborate architecture — just
   clarity.
3. **Tests.** Think about what would be meaningful to test. We care more
   about *what* you test than how many tests there are. How you decide to
   test is up to you.
4. **Error handling.** What happens on bad JSON, missing fields, an unknown
   user id, an expired token? You don't need to handle every edge case, but
   show you've thought about it.
5. **Logging and observability.** Think about what would be meaningful to
   log for the sake of debugging the system and understanding actions
   taken. You can ignore tracing / actuators / metrics endpoints.
6. **Trade-offs you can articulate.** It's fine to say "I'd do X in a real
   service but skipped it here because Y." Note these in a `NOTES.md` if
   you like, or just be ready to discuss in the follow-up.

We are **not** evaluating:
- Fancy frameworks or abstractions you wouldn't use in real life.
- Frontend or deployment.

---

## Test tokens

The auth module decodes JWTs without verifying their signature. Pass them as a header: `Authorization: Bearer <token>`.

### Valid tokens

| User  | User id                                | JWT |
|-------|----------------------------------------|-----|
| alice | `00000000-0000-0000-0000-000000000001` | `eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2VybmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6NDEwMjQ0NDgwMH0.` |
| bob   | `00000000-0000-0000-0000-000000000002` | `eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDIiLCJ1c2VybmFtZSI6ImJvYiIsImVtYWlsIjoiYm9iQGV4YW1wbGUuY29tIiwiZXhwIjo0MTAyNDQ0ODAwfQ.` |
| carol | `00000000-0000-0000-0000-000000000003` | `eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDMiLCJ1c2VybmFtZSI6ImNhcm9sIiwiZW1haWwiOiJjYXJvbEBleGFtcGxlLmNvbSIsImV4cCI6NDEwMjQ0NDgwMH0.` |

### Expired token (for testing the failure path)

```
eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2VybmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6MTcwMDAwMDAwMH0.
```

Anything else (random strings, JWTs missing a `sub`, etc.) will come back as
an `AuthError.Malformed`.

---

## Submitting

To make the diff easy to review, please submit your work as a pull
request **in your own fork**:

1. **Fork** this repository to your own GitHub account.
2. Clone your fork locally and create a **fresh branch** off `master`
   (e.g. `solution`, `your-name/solution`, `feat/emails-endpoint` —
   whatever you like; just don't commit to `master`).
3. Do your work on that branch, committing as you go. Many small commits
   tell a clearer story than one giant squash, but we're not picky
   about the exact granularity.
4. Push the branch to your fork and open a **pull request from your
   branch → `master` on your own fork** (not against this repo). Share
   the URL of that PR when you submit.
5. Use `NOTES.md` for anything you want us to know — trade-offs you'd
   revisit, things you'd add with more time, decisions you want to
   discuss in the follow-up.

If for some reason you can't use GitHub, a zipped repo with `.git/`
intact is also fine.

Good luck, and have fun.
