package dev.adamhodgkinson.Routes;

import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import dev.adamhodgkinson.Services.InventoryService;

import static spark.Spark.*;

public class InventoryRoutes {

    public static void init(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        InventoryService inventoryService = new InventoryService(mongoDB, sqLiteDB);
        path("/inventory", () -> {
            get("", inventoryService.getInventory); // entire inv

            get("/weapon/equipped", inventoryService.getEquippedWeapon);
            post("/weapon/equipped", inventoryService.setEquippedWeapon);
            post("/weapon", inventoryService.createWeapon);
        });
    }
}
