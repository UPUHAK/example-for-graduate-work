-- liquibase formatted sql

-- changeset dmitriy:7

CREATE TABLE IF NOT EXISTS ads (
    id SERIAL PRIMARY KEY,           -- Уникальный идентификатор объявления
    image VARCHAR(255),              -- Ссылка на картинку объявления
    price INTEGER NOT NULL,          -- Цена объявления (обязательное поле)
    title VARCHAR(255) NOT NULL,     -- Заголовок объявления (обязательное поле)
    count INTEGER,                   -- Общее количество объявлений
    author_id INTEGER NOT NULL,      -- Идентификатор автора (внешний ключ)
    CONSTRAINT fk_ads_author FOREIGN KEY (author_id) REFERENCES users(id) -- Связь с таблицей users
);
