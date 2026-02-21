CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    holder VARCHAR(100) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    balance_after DECIMAL(19, 2),
    transfer_type VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    correlation_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS outbox (
    id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100),
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS transfer_log (
    id BIGSERIAL PRIMARY KEY,
    correlation_id VARCHAR(100) NOT NULL,
    step INTEGER NOT NULL,
    message VARCHAR(500) NOT NULL,
    transfer_type VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_correlation_id ON transactions(correlation_id);
CREATE INDEX IF NOT EXISTS idx_outbox_published_at ON outbox(published_at);
CREATE INDEX IF NOT EXISTS idx_transfer_log_correlation_id ON transfer_log(correlation_id);
