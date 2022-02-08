package dev.adamhodgkinson;


import dev.adamhodgkinson.Models.User;
import dev.adamhodgkinson.Models.Weapon;

import java.sql.*;
import java.util.ArrayList;

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

    public Weapon[] getInventory(String name) {
        try {
            String statementString = "SELECT * FROM Weapons WHERE username = ?";
            PreparedStatement s = connection.prepareStatement(statementString);
            s.setString(1, name);

            ResultSet results = s.executeQuery();
            if (!results.next()) {
                return new Weapon[0];
            }
            ArrayList<Weapon> list = new ArrayList<>();
            do {
                list.add(resultsToWeaponObj(results));
            } while (results.next());
            Weapon[] array = new Weapon[list.size()];
            list.toArray(array);
            return array;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void insertWeapon(Weapon weapon) throws SQLException {
        String statementString = "INSERT INTO Weapons (weaponID, username, range, damage, knockback, attackspeed, isMelee, textureName) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, weapon.weaponID);
        s.setString(2, weapon.username);
        s.setInt(3, weapon.range);
        s.setInt(4, weapon.damage);
        s.setInt(5, weapon.knockback);
        s.setInt(6, weapon.attackspeed);
        s.setBoolean(7, weapon.isMelee);
        s.setString(8, weapon.textureName);

        s.execute();
    }

    private Weapon resultsToWeaponObj(ResultSet results) throws SQLException {
        Weapon w = new Weapon();
        w.attackspeed = results.getInt("attackspeed");
        w.knockback = results.getInt("knockback");
        w.damage = results.getInt("damage");
        w.isMelee = results.getBoolean("isMelee");
        w.textureName = results.getString("textureName");
        w.weaponID = results.getString("weaponID");
        w.username = results.getString("username");
        w.range = results.getInt("range");
        return w;
    }

    public Weapon getWeaponById(String id) throws SQLException {
        String statementString = "SELECT * FROM Weapons WHERE weaponID = ?";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, id);
        ResultSet results = s.executeQuery();
        if (!results.next()) {
            return null;
        }
        return resultsToWeaponObj(results);

    }

    public void setUserEquippedWeapon(String username, String weaponID) throws SQLException {
        String statementString = "UPDATE Users SET equipped_weapon = ? WHERE username = ?";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, weaponID);
        s.setString(2, username);
        s.executeUpdate();
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing SQL DB");
        }
    }

}
