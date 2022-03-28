DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Weapons;
DROP TABLE IF EXISTS Level_Meta;
DROP TABLE IF EXISTS Leaderboard;


create table Users
(
    'username'        varchar(45) NOT NULL
        primary key,
    'password_hash'   varchar(45) NOT NULL,
    'password_salt'   varchar(45) NOT NULL,
    'equipped_weapon' varchar(45),
    FOREIGN KEY (equipped_weapon) REFERENCES Weapons (weaponID)
);

CREATE TABLE `Weapons`
(
    'weaponID'    varchar(45) NOT NULL,
    'username'    varchar(45) NOT NULL,
    'range'       int         NOT NULL,
    'damage'      int         NOT NULL,
    'knockback'   int         NOT NULL,
    'attackspeed' int         NOT NULL,
    'isMelee'     boolean     NOT NULL,
    'textureName' varchar(45) NOT NULL,

    PRIMARY KEY (weaponID),
    FOREIGN KEY (username) REFERENCES Users (username)
);

CREATE TABLE 'Level_Meta'
(
    'LevelID'      varchar(45) NOT NULL,
    'creator'      varchar(45) NOT NULL,
    'date_created' datetime    NOT NULL,
    'title'        varchar(45) NOT NULL,

    PRIMARY KEY (LevelID),
    FOREIGN KEY (creator) REFERENCES Users (username)
);

CREATE TABLE 'Leaderboard'
(
    'LevelID'   varchar(45) NOT NULL,
    'username'  varchar(45) NOT NULL,
    'completed' int        NOT NULL,

    FOREIGN KEY (LevelID) REFERENCES Level_Meta (LevelID),
    FOREIGN KEY (username) REFERENCES Users (username)
);