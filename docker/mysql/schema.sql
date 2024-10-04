DROP DATABASE IF EXISTS starfleet_db;
CREATE DATABASE IF NOT EXISTS starfleet_db;

USE starfleet_db;

DROP TABLE IF EXISTS `starships`;
CREATE TABLE `starships`(
    `id`            INT PRIMARY KEY AUTO_INCREMENT,
    `name`          VARCHAR(255),
    `class`         VARCHAR(255),
    `captain`       VARCHAR(255),
    `launched_year` INT
);
SET GLOBAL local_infile=1;