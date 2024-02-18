DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT                  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255),
    email               VARCHAR(255)            NOT NULL UNIQUE
);