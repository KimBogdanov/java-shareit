DROP TABLE IF EXISTS users, items, bookings;

CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT                          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255)                    NOT NULL,
    email               VARCHAR(255)                    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
    id                  BIGINT                          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255)                    NOT NULL,
    description         VARCHAR(2000),
    is_available        BOOLEAN,
    owner_id            BIGINT                          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_id          BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    id                  BIGINT                          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_time          TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    end_time            TIMESTAMP WITHOUT TIME ZONE     NOT NULL ,
    booker_id           BIGINT                          NOT NULL REFERENCES users(id),
    item_id             BIGINT                          NOT NULL REFERENCES items(id),
    status              VARCHAR(20)                     NOT NULL
);