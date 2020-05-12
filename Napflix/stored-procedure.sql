USE Napflix;
-- ADD EMPLOYEES TO DB --
CREATE TABLE IF NOT EXISTS employees(
									  email varchar(50) primary key,
									  password varchar(20) not null,
					                  fullname varchar(100)
);

INSERT INTO employees VALUES('classta@email.edu','classta','TA CS122B');

-- Store Procedures --
DELIMITER $$
-- ADDSTAR --
CREATE PROCEDURE add_stars (IN NID VARCHAR(10),IN Nname VARCHAR(100),IN NbirthYear INTEGER)
BEGIN
IF (NbirthYear = 0) THEN
INSERT INTO stars (id, name) VALUES(NID,Nname);
ELSE
INSERT INTO stars (id, name, birthYear) VALUES(NID,Nname,NbirthYear);
END IF;
END
$$

-- ADDMOVIE --
CREATE PROCEDURE add_movie (
IN NmovieID VARCHAR(10),
IN Ntitle VARCHAR(100),
IN Nyear INTEGER,
IN Ndirector VARCHAR(100),
IN NstarID VARCHAR(10),
IN Nstar VARCHAR(100),
IN Nbirthday INTEGER,
IN NgenreID INTEGER,
IN Ngenre VARCHAR(32),
IN genreFlag INTEGER,
IN starFlag INTEGER)

BEGIN
INSERT INTO movies VALUES(NmovieID,Ntitle,Nyear,Ndirector);

IF (genreFlag = 0) THEN
INSERT INTO genres VALUES(NgenreID,Ngenre);
END IF;

IF (starFlag = 0) THEN
	IF (Nbirthday = -1) THEN
	INSERT INTO stars (id, name) VALUES(NstarID,Nstar);
	ELSE
	INSERT INTO stars (id, name, birthYear) VALUES(NstarID,Nstar,Nbirthday);
	END IF;
END IF;
INSERT INTO ratings VALUES(NmovieID,0,0);
INSERT INTO stars_in_movies VALUES(NstarID,NmovieID);
INSERT INTO genres_in_movies VALUES(NgenreID,NmovieID);
END
$$
-- ADDMOVIETASK7 --

CREATE PROCEDURE add_movietask7 (
IN NmovieID VARCHAR(10),
IN Ntitle VARCHAR(100),
IN Nyear INTEGER,
IN Ndirector VARCHAR(100))
BEGIN
DECLARE CONTINUE HANDLER FOR 1062 SELECT "Duplicate Movie";
INSERT INTO movies VALUES(NmovieID,Ntitle,Nyear,Ndirector);
END
$$
DELIMITER ;


CREATE INDEX starName ON stars(name);
CREATE INDEX genreName ON genres(name);


DELIMITER ##
CREATE TRIGGER AfterINSERTTrigger
    AFTER INSERT
    ON movies FOR EACH ROW
BEGIN
    INSERT INTO ratings
        VALUE (new.ID,0,0);
end ##
DELIMITER ;