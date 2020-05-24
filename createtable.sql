/*
This code is use to initilize the Napflix database
Author: Ganyu Luo

How to run this script in mysql? (On Mac/Linux):
mysql> source this.sql
*/
CREATE DATABASE IF NOT EXISTS Napflix;
USE Napflix;

CREATE TABLE IF NOT EXISTS movies(
                                     ID VARCHAR(10) PRIMARY KEY NOT NULL DEFAULT '',
                                     title TEXT,
                                     year INTEGER NOT NULL,
                                     director VARCHAR(100) NOT NULL DEFAULT '',
									 FULLTEXT (title)
);

CREATE TABLE IF NOT EXISTS stars(
                                    ID VARCHAR(10) PRIMARY KEY NOT NULL DEFAULT '',
                                    name VARCHAR(100) NOT NULL DEFAULT '',
                                    birthYear INTEGER DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS stars_in_movies(
                                              starID VARCHAR(10) NOT NULL DEFAULT '',
                                              movieID VARCHAR(10) NOT NULL DEFAULT '',
                                              FOREIGN KEY (starID) REFERENCES stars(ID),
                                              FOREIGN KEY (movieID) REFERENCES movies(ID)
);

CREATE TABLE IF NOT EXISTS genres(
                                     ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                     name VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS genres_in_movies(
                                               genreID INTEGER NOT NULL,
                                               movieID VARCHAR(10) NOT NULL DEFAULT '',
                                               FOREIGN KEY (genreID) REFERENCES genres(ID),
                                               FOREIGN KEY (movieID) REFERENCES movies(ID)
);

CREATE TABLE IF NOT EXISTS creditcards(
                                          ID VARCHAR(20) PRIMARY KEY NOT NULL DEFAULT '',
                                          firstName VARCHAR(50) NOT NULL DEFAULT '',
                                          lastName VARCHAR(50) NOT NULL DEFAULT '',
                                          expiration DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS customers(
                                        ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                        firstName VARCHAR(50) DEFAULT '' NOT NULL ,
                                        lastName VARCHAR(50) DEFAULT '' NOT NULL ,
                                        ccID VARCHAR(20) NOT NULL DEFAULT '' ,
                                        address VARCHAR(200) NOT NULL DEFAULT '',
                                        email VARCHAR(50) NOT NULL DEFAULT '',
                                        password VARCHAR(20) NOT NULL DEFAULT '',
                                        FOREIGN KEY (ccID) REFERENCES creditcards(ID)
);

CREATE TABLE IF NOT EXISTS sales(
                                    ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                    customerID INTEGER NOT NULL,
                                    movieID VARCHAR(10) NOT NULL DEFAULT '',
                                    saleDate DATE NOT NULL,
                                    FOREIGN KEY (customerID) REFERENCES customers(ID),
                                    FOREIGN KEY (movieID)  REFERENCES movies(ID)
);

CREATE TABLE IF NOT EXISTS ratings(
                                      movieID VARCHAR(10) NOT NULL DEFAULT '',
                                      rating FLOAT NOT NULL,
                                      numVotes INTEGER NOT NULL,
                                      FOREIGN KEY (movieID) REFERENCES movies(ID)
);
