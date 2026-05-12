# Getting started

A suggested path through the exercise if you're not sure where to start.
Not a script — feel free to ignore.

## 1. Get the green/red bar

```bash
./mvnw -pl app verify
```

You should see one unit test passing and two integration tests failing
with `expected: 401 / but was: 500`. Those failures are the entry
point: making them go green is the smallest first deliverable that
gives you working auth across both endpoints.

## 2. Implement auth first

The failing tests live in `app/src/integrationTest/java/.../AuthIntegrationTest`.
They exercise `AuthenticatedUserArgumentResolver.resolveUserDetails`,
which is the only TODO in the auth bridge. Everything around it
(`@AuthenticatedUser`, the `WebConfig` registration, the controller
parameter binding) is already wired — see [`app/README.md`](app/README.md#the-authenticateduser-argument-resolver)
for the contract.

Once those two tests pass, every controller method already in
`EmailController` will receive an authenticated `UserDetails` (or 401)
without further work.

## 3. Build out the endpoints

With auth solid, the rest is the standard HTTP-controller arc:

- Decide the request shape — add fields to
  [`SendEmailRequest`](app/src/main/java/com/codetest/app/web/SendEmailRequest.java)
  and (optionally) validation annotations.
- Decide the response shape and write whatever DTOs / mappers you need.
- Implement the logic and persistence layer for both the endpoints. 
- Test what you feel is relevant to test. Templates and basic wiring for both unit and integration tests are provided in 
 `app/src/test` and `app/src/integrationTest`

## 4. Wrap up

- Skim the `Must` checklist in the top-level [`README.md`](README.md#scope), those are the things we definitely care about.
- Jot anything worth discussing in [`NOTES.md`](NOTES.md).
- Submit the PR (see [Submitting](README.md#submitting) for the flow).
