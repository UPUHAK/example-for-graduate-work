-- liquibase formatted sql

-- changeset dmitriy:1

CREATE TABLE IF NOT EXISTS comment (
    pk SERIAL PRIMARY KEY,                   -- Уникальный идентификатор комментария
    author_id INTEGER NOT NULL,              -- Внешний ключ на пользователя (автора)
    ad_id INTEGER NOT NULL,                  -- Внешний ключ на объявление
    created_at BIGINT NOT NULL,              -- Дата и время создания (миллисекунды с 1970-01-01)
    text TEXT NOT NULL,                      -- Текст комментария
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comment_ad FOREIGN KEY (ad_id) REFERENCES ads(id)
);
