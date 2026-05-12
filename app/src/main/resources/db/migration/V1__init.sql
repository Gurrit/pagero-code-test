CREATE TABLE emails (
    id           UUID         PRIMARY KEY,
    sender_id    UUID         NOT NULL,
    sender_email VARCHAR(254) NOT NULL,
    recipient    VARCHAR(254) NOT NULL,
    subject      VARCHAR(998) NOT NULL,
    body         TEXT         NOT NULL,
    status       VARCHAR(32)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL
);

CREATE INDEX idx_emails_sender_id    ON emails (sender_id);
CREATE INDEX idx_emails_sent_at_desc ON emails (sent_at DESC);
