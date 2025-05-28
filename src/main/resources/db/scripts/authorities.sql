-- liquibase formatted sql

-- changeset dmitriy:9
CREATE TABLE IF NOT EXISTS authorities (
    id SERIAL PRIMARY KEY,
    username VARCHAR(16) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);