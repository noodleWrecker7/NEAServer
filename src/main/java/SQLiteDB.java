import java.sql.*;

public class SQLiteDB {
    Connection connection;


    public SQLiteDB(String filepath) {
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + filepath);
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO Users VALUES ('adam', 'testhash', 'testsalt')");
            statement.close();
            statement = connection.createStatement();
            statement.execute("SELECT * FROM Users");
            ResultSet results = statement.getResultSet();
            System.out.println("Record found: " + results.getString("username"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
