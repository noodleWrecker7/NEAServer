
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];

        MongoDB mongoDB = new MongoDB(username, password);
        SQLiteDB sqliteDB = new SQLiteDB("sample.db");



    }



}
