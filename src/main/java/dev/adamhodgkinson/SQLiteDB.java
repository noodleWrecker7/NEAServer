package dev.adamhodgkinson;


import dev.adamhodgkinson.Models.User;

import java.sql.*;

public class SQLiteDB {
    Connection connection;


    public SQLiteDB(String filepath) {
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + filepath);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            Server.close();
        }
    }

    public void insertUser(User u) throws SQLException {
        String statementString = "INSERT INTO Users (username, password_hash, password_salt) VALUES (?,?,?)";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, u.username);
        s.setString(2, u.password_hash);
        s.setString(3, u.password_salt);
        s.execute();
    }

    public User findUserByName(String name) {
        // will be executed on the database
        String statementString = "SELECT * FROM Users WHERE username = ?";
        try {
            PreparedStatement s = connection.prepareStatement(statementString);
            // inserts the parameter to the statement - avoids sql injection
            s.setString(1, name);

            ResultSet result = s.executeQuery();
            // checks that a row was actually found
            if (!result.next()) {
                return null;
            }
            // creates a new user object and inserts the data before returning it
            User u = new User();
            u.username = result.getString("username");
            u.password_hash = result.getString("password_hash");
            u.password_salt = result.getString("password_salt");
            return u;
        } catch (SQLException e) {
            System.out.println("Error reading from database, has the connection been closed?");
            return null;
        }
    }


    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing SQL DB");
        }
    }

}
