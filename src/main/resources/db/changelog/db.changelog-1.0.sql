--liquibase formatted sql

--changeset gman97:1
CREATE TABLE categories
(
    id   SERIAL      PRIMARY KEY ,
    name VARCHAR(64) UNIQUE
);

--changeset gman97:2
CREATE TABLE ratings
(
    id    BIGSERIAL     PRIMARY KEY ,
    rate  NUMERIC(2, 1) ,
    count INTEGER ,
    UNIQUE (rate, count)
);

--changeset gman97:3
CREATE TABLE products
(
    id          SERIAL      PRIMARY KEY ,
    external_id INTEGER     UNIQUE ,
    title       VARCHAR(128) ,
    price       NUMERIC(6, 2) ,
    description VARCHAR ,
    category_id INTEGER     REFERENCES categories (id) ,
    image       VARCHAR ,
    rating_id   BIGINT      REFERENCES ratings (id)
);