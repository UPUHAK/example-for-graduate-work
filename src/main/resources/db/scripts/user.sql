-- liquibase formatted sql

-- changeset dmitriy:1
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(16) NOT NULL,
    last_name VARCHAR(16) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    image VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE
);

-- changeset dmitriy:2
ALTER TABLE users
ADD COLUMN is_authorize BOOLEAN;

-- changeset dmitriy:3
UPDATE users SET is_authorize = FALSE;

-- changeset dmitriy:4
ALTER TABLE users
ALTER COLUMN is_authorize SET NOT NULL;

-- changeset dmitriy:5
ALTER TABLE users
ADD COLUMN image_id INT;

-- Убедитесь, что таблица image уже создана
-- changeset dmitriy:6
ALTER TABLE users
ADD CONSTRAINT fk_users_image FOREIGN KEY (image_id) REFERENCES image(id_image) ON DELETE SET NULL;

-- changeset dmitriy:9
ALTER TABLE users
DROP COLUMN username;

