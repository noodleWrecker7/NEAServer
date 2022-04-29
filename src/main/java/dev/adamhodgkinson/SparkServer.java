package dev.adamhodgkinson;

import dev.adamhodgkinson.Routes.InventoryRoutes;
import dev.adamhodgkinson.Routes.LevelRoutes;
import dev.adamhodgkinson.Routes.UserRoutes;

import static spark.Spark.*;

/**Main class for handling HTTP API interactions*/
public class SparkServer {

    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

    public SparkServer(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;

        // sets port the server is listening on
        port(26500);

        // calls the route API route definitions
        UserRoutes.init(mongoDB, sqLiteDB);
        LevelRoutes.init(mongoDB, sqLiteDB);
        InventoryRoutes.init(mongoDB,sqLiteDB);
    }

    public void close() {
        stop();
    }
}
