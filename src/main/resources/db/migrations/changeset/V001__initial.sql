CREATE TABLE roles (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    type varchar(32) UNIQUE NOT NULL
);

INSERT INTO roles (type)
VALUES
    ('ADMIN'),
    ('USER');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username varchar(64) UNIQUE NOT NULL,
    password varchar(128) NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    is_banned boolean DEFAULT false NOT NULL,

    role_id bigint NOT NULL,

    deleted_at timestamptz,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE refresh_tokens (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id     UUID NOT NULL,
    token       TEXT    NOT NULL UNIQUE,
    is_revoked  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at  TIMESTAMP NOT NULL,

    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);