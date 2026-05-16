"""
Smoke-tests the /emails endpoints by sending a batch of emails as alice and
then retrieving them, mixing in failure paths (expired token, malformed body,
cross-user access) alongside the happy path.

Run the Spring app first (`./mvnw -pl app spring-boot:run`), then:

    python caller.py

Tokens and user ids are taken from the README's "Valid tokens" table.
"""

import sys
import time

import requests

base_url = "http://localhost:8080"

# How many emails to send in the bulk-send phase.
BATCH_SIZE = 20

# --- Tokens & user ids (from README) --------------------------------------

ALICE_ID = "00000000-0000-0000-0000-000000000001"
BOB_ID = "00000000-0000-0000-0000-000000000002"

ALICE_TOKEN = (
    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
    "eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2Vy"
    "bmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6NDEw"
    "MjQ0NDgwMH0."
)

BOB_TOKEN = (
    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
    "eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDIiLCJ1c2Vy"
    "bmFtZSI6ImJvYiIsImVtYWlsIjoiYm9iQGV4YW1wbGUuY29tIiwiZXhwIjo0MTAyNDQ0"
    "ODAwfQ."
)

# Same payload as alice but with an `exp` in the past — exercises the
# AuthError.Expired failure path.
EXPIRED_TOKEN = (
    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
    "eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2Vy"
    "bmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6MTcw"
    "MDAwMDAwMH0."
)


# --- HTTP helpers ---------------------------------------------------------

def auth_headers(token: str) -> dict:
    return {"Authorization": f"Bearer {token}"}


def send_email(token: str, body: dict) -> requests.Response:
    return requests.post(
        f"{base_url}/emails",
        json=body,
        headers=auth_headers(token),
    )


def list_emails(token: str, user_id: str) -> requests.Response:
    return requests.get(
        f"{base_url}/users/{user_id}/emails",
        headers=auth_headers(token),
    )


# --- Test harness ---------------------------------------------------------

results = {"passed": 0, "failed": 0}


def expect(label: str, response: requests.Response, expected_status: int) -> None:
    actual = response.status_code
    ok = actual == expected_status
    marker = "PASS" if ok else "FAIL"
    print(f"[{marker}] {label} — expected {expected_status}, got {actual}")
    if not ok:
        print(f"        body: {response.text!r}")
    results["passed" if ok else "failed"] += 1


def make_email(i: int) -> dict:
    return {
        "to": "bob@example.com",
        "subject": f"Hello from caller.py #{i}",
        "body": f"This is bulk message {i} of {BATCH_SIZE}.",
    }


# --- Scenarios ------------------------------------------------------------

def bulk_send_as_alice() -> None:
    print(f"\n== Sending {BATCH_SIZE} emails as alice ==")
    start = time.perf_counter()
    for i in range(1, BATCH_SIZE + 1):
        resp = send_email(ALICE_TOKEN, make_email(i))
        expect(f"send #{i}", resp, 201)
    elapsed = time.perf_counter() - start
    print(f"   ({BATCH_SIZE} requests in {elapsed:.2f}s)")


def send_failure_paths() -> None:
    print("\n== Failing send requests ==")
    expect(
        "send with expired token → 401",
        send_email(EXPIRED_TOKEN, make_email(0)),
        401,
    )
    expect(
        "send with malformed body (missing 'to') → 400",
        send_email(ALICE_TOKEN, {"subject": "no recipient", "body": "oops"}),
        400,
    )
    expect(
        "send with no token → 401",
        requests.post(f"{base_url}/emails", json=make_email(0)),
        401,
    )


def retrieve_alice_history() -> None:
    print("\n== Retrieving alice's history as alice ==")
    resp = list_emails(ALICE_TOKEN, ALICE_ID)
    expect("list alice's emails as alice → 200", resp, 200)
    if resp.status_code == 200:
        try:
            payload = resp.json()
        except ValueError:
            print(f"        body was not JSON: {resp.text!r}")
            results["failed"] += 1
            return
        count = len(payload)
        print(f"        returned {count} emails")
        if count >= BATCH_SIZE:
            results["passed"] += 1
            print(f"[PASS] list contains at least the {BATCH_SIZE} we just sent")
        else:
            results["failed"] += 1
            print(f"[FAIL] expected at least {BATCH_SIZE} emails, got {count}")


def retrieve_failure_paths() -> None:
    print("\n== Failing retrieve requests ==")
    expect(
        "list alice's emails as bob → 403",
        list_emails(BOB_TOKEN, ALICE_ID),
        403,
    )
    expect(
        "list alice's emails with expired token → 401",
        list_emails(EXPIRED_TOKEN, ALICE_ID),
        401,
    )
    expect(
        "list bob's emails as alice → 403",
        list_emails(ALICE_TOKEN, BOB_ID),
        403,
    )


# --- Entry point ----------------------------------------------------------

if __name__ == "__main__":
    bulk_send_as_alice()
    send_failure_paths()
    retrieve_alice_history()
    retrieve_failure_paths()

    print(f"\n== Summary: {results['passed']} passed, {results['failed']} failed ==")
    sys.exit(0 if results["failed"] == 0 else 1)
