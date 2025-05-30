-- liquibase formatted sql

-- changeset dmitriy:7

CREATE TABLE IF NOT EXISTS image (
    id_image SERIAL PRIMARY KEY,                -- Уникальный идентификатор изображения
    image_url VARCHAR(255) NOT NULL,           -- URL изображения (обязательное поле)
    data BYTEA,                                 -- Данные изображения в бинарном формате
    ad_id INTEGER,                              -- Внешний ключ на таблицу ads
    user_id INTEGER,                            -- Внешний ключ на таблицу users
    CONSTRAINT fk_image_ad FOREIGN KEY (ad_id) REFERENCES ads(id),
    CONSTRAINT fk_image_user FOREIGN KEY (user_id) REFERENCES users(id)
);
