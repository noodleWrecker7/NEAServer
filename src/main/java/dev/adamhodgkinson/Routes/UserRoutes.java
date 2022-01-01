package dev.adamhodgkinson.Routes;

import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import dev.adamhodgkinson.Services.UserService;

import static spark.Spark.*;

public class UserRoutes {

    public static void init(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        UserService userService = new UserService(mongoDB, sqLiteDB);
        path("/user", () -> {
            get("/test", userService.testService);

            post("/signup", userService.signup);
            post("/login", userService.login);

            get("/:username", userService.getUser);
            get("/:username/inventory", userService.getInventory);
        });
    }
}
