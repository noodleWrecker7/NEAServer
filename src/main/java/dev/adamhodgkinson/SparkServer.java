package dev.adamhodgkinson;

import dev.adamhodgkinson.Routes.UserRoutes;

import static spark.Spark.*;

public class SparkServer {

    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

    public SparkServer(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
        port(26500);

        UserRoutes.init(mongoDB, sqLiteDB);
        System.out.println(port());
    }

    public void close() {
        stop();
    }
}
