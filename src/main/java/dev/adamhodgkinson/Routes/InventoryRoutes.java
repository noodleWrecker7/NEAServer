package dev.adamhodgkinson.Routes;

import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import dev.adamhodgkinson.Services.InventoryService;
import dev.adamhodgkinson.Services.UserService;

import static spark.Spark.*;

public class InventoryRoutes {

    public static void init(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        InventoryService inventoryService = new InventoryService(mongoDB, sqLiteDB);
        path("/inventory", () -> {
        });
    }
}
