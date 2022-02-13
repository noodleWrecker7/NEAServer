package dev.adamhodgkinson.Routes;

import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import dev.adamhodgkinson.Services.LevelService;


import static spark.Spark.*;

public class LevelRoutes {

    public static void init(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        LevelService levelService = new LevelService(mongoDB, sqLiteDB);
        path("/level", () -> {
            get("/list", levelService.listLevels); // default to first 50, take page in query params
            get("/:id/leaderboard", levelService.getLeaderboard);
            get("/:id", levelService.getLevel);
            // todo search levels route
            post("/create", levelService.createLevel);
        });
    }
}
