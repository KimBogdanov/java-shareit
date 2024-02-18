DROP TABLE IF EXISTS users, items;

CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT                  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255),
    email               VARCHAR(255)            NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
    id                  BIGINT                  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255)            NOT NULL,
    description         VARCHAR(1000),
    is_available        BOOLEAN,
    owner_id            BIGINT                  REFERENCES users(id),
    request_id          BIGINT
)