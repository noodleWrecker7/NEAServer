package dev.adamhodgkinson.Services;

import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import spark.Route;

public class LevelService {

    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

    public LevelService(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
    }

    public Route listAll = (request, response) -> {
        return null;
    };

    public Route getLeaderboard = (request, response) -> {
        return null;
    };

    public Route getLevel = (request, response) -> {
        return null;
    };

    public Route createLevel = (request, response) -> {
        return null;
    };
}
