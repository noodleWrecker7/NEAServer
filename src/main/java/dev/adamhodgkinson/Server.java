package dev.adamhodgkinson;


public class Server {

    static MongoDB mongoDB;
    static SQLiteDB sqliteDB;
    static SparkServer sparkServer;

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];

        mongoDB = new MongoDB(username, password);
        sqliteDB = new SQLiteDB("sample.db");
        sparkServer = new SparkServer(mongoDB, sqliteDB);
    }


    public static void close() {
        sparkServer.close();
        mongoDB.close();
        sqliteDB.close();
        System.exit(1);
    }
}
