-- Countries table
CREATE TABLE IF NOT EXISTS countries (
                                         id        BIGSERIAL PRIMARY KEY,
                                         name      VARCHAR(100) NOT NULL UNIQUE,
    continent VARCHAR(50)  NOT NULL
    );

-- Authors table
CREATE TABLE IF NOT EXISTS authors (
                                       id         BIGSERIAL PRIMARY KEY,
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    name       VARCHAR(100) NOT NULL,
    surname    VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL REFERENCES countries(id)
    );

-- Books table
CREATE TABLE IF NOT EXISTS books (
                                     id               BIGSERIAL PRIMARY KEY,
                                     created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    name             VARCHAR(255) NOT NULL,
    category         VARCHAR(50)  NOT NULL,
    author_id        BIGINT       NOT NULL REFERENCES authors(id),
    state            VARCHAR(10)  NOT NULL,
    available_copies INTEGER      NOT NULL CHECK (available_copies >= 0)
    );
