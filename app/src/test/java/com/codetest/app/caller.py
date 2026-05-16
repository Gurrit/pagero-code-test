"""
Smoke-tests the POST /emails endpoint with one succeeding and one failing
request. Run the Spring app first (`./mvnw -pl app spring-boot:run`), then:

    python caller.py
"""

import requests

base_url = "http://localhost:8080"

# alice — listed under "Valid tokens" in the repo README
VALID_TOKEN = (
    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
    "eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2Vy"
    "bmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6NDEw"
    "MjQ0NDgwMH0."
)

# Same payload as alice but with an `exp` in the past — exercises the
# AuthError.Expired failure path.
EXPIRED_TOKEN = (
    "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0."
    "eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDEiLCJ1c2Vy"
    "bmFtZSI6ImFsaWNlIiwiZW1haWwiOiJhbGljZUBleGFtcGxlLmNvbSIsImV4cCI6MTcw"
    "MDAwMDAwMH0."
)

# TODO: align with SendEmailRequest's fields once they exist.
email_body = {
    "to": "bob@example.com",
    "subject": "Hello from caller.py",
    "body": "This is a smoke test.",
}


def send_email(token: str, body: dict) -> requests.Response:
    return requests.post(
        f"{base_url}/emails",
        json=body,
        headers={"Authorization": f"Bearer {token}"},
    )


def show(label: str, response: requests.Response) -> None:
    print(f"--- {label} ---")
    print(f"status: {response.status_code}")
    print(f"body:   {response.text!r}")
    print()


if __name__ == "__main__":
    show("succeeding request (valid token)", send_email(VALID_TOKEN, email_body))
    show("failing request (expired token)", send_email(EXPIRED_TOKEN, email_body))
