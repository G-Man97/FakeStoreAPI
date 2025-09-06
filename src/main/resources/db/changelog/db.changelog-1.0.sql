--liquibase formatted sql

--changeset gman97:1
CREATE TABLE categories
(
    name VARCHAR(64) PRIMARY KEY
);

--changeset gman97:2
CREATE TABLE ratings
(
    rate NUMERIC(2, 1),
    count INTEGER,
    PRIMARY KEY (rate, count)
);

--changeset gman97:3
CREATE TABLE products
(
    id SERIAL PRIMARY KEY ,
    title VARCHAR(128) ,
    price NUMERIC(6, 2),
    description VARCHAR,
    category_id VARCHAR(64) REFERENCES categories (name),
    image VARCHAR,
    rate NUMERIC(2, 1),
    count INTEGER,
    FOREIGN KEY (rate, count) REFERENCES ratings (rate, count)
)