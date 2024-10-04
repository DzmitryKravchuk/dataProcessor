DROP DATABASE IF EXISTS federation_db;
CREATE DATABASE federation_db;

DROP TABLE IF EXISTS vessels;
CREATE TABLE vessels(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255),
    class         VARCHAR(255),
    captain       VARCHAR(255),
    launched_year INT
);

ALTER TABLE vessels ALTER COLUMN name SET NOT NULL