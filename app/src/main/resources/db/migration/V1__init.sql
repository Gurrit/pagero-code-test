CREATE TABLE emails (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    message_id   VARCHAR(36)  DEFAULT CAST(RANDOM_UUID() AS VARCHAR(36)) NOT NULL UNIQUE,
    sender_id    VARCHAR(64)  NOT NULL,
    sender_email VARCHAR(254) NOT NULL,
    recipient    VARCHAR(254) NOT NULL,
    subject      VARCHAR(998) NOT NULL,
    body         CLOB         NOT NULL,
    status       VARCHAR(32)  DEFAULT 'SENT' NOT NULL,
    sent_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_emails_sender_id    ON emails (sender_id);
CREATE INDEX idx_emails_sent_at_desc ON emails (sent_at DESC);
