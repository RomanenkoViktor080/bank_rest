CREATE TABLE cards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pan_hash VARCHAR(255) UNIQUE NOT NULL,
    pan_last4 CHAR(4) NOT NULL,
    status VARCHAR(32),
    balance NUMERIC(19,4) NOT NULL DEFAULT 0,
    user_id UUID NOT NULL REFERENCES users(id),
    first_name_snapshot VARCHAR(124) NOT NULL,
    last_name_snapshot VARCHAR(124) NOT NULL,
    expiry_month SMALLINT NOT NULL,
    expiry_year SMALLINT NOT NULL,
    deleted_at timestamptz,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE card_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_id UUID NOT NULL REFERENCES cards(id),
    type varchar(64) NOT NULL,
    amount NUMERIC(15,4) NOT NULL,
    related_card_id UUID REFERENCES cards(id),
    idempotency_key varchar(128),
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_card_id ON card_transactions(card_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_idempotency_key ON card_transactions(idempotency_key)
    WHERE idempotency_key IS NOT NULL;

CREATE TABLE card_requests (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  card_id UUID NOT NULL REFERENCES cards(id),
  processed_by_user_id UUID REFERENCES users(id),
  type VARCHAR(64) NOT NULL,
  status VARCHAR(64) NOT NULL,
  idempotency_key varchar(128) UNIQUE,
  updated_at TIMESTAMP,
  created_at TIMESTAMP
);

CREATE INDEX idx_card_requests_by_status ON card_requests(status);
CREATE INDEX idx_card_processed_by_user_id ON card_requests(processed_by_user_id);
