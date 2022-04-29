package dev.adamhodgkinson;


import dev.adamhodgkinson.Models.LeaderboardEntry;
import dev.adamhodgkinson.Models.LevelMeta;
import dev.adamhodgkinson.Models.User;
import dev.adamhodgkinson.Models.Weapon;

import java.sql.*;
import java.util.ArrayList;

import static dev.adamhodgkinson.Services.InventoryService.generateWeaponID;

/**Class to manage the interactions with the SQLite database*/
public class SQLiteDB {
    Connection connection;

    /**Loads the database data from the passed file
     * @param filepath Path of the file to open*/
    public SQLiteDB(String filepath) {
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + filepath);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            Server.close();
        }
    }

    /**Inserts the user object into the Users table in the database*/
    public void insertUser(User u) throws SQLException {
        // uses prepared statements to prevent SQL injection attacks
        String statementString = "INSERT INTO Users (username, password_hash, password_salt) VALUES (?,?,?)";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, u.username);
        s.setString(2, u.password_hash);
        s.setString(3, u.password_salt);
        s.execute();
    }

    /**Inserts the level object into the Levels table in the database*/
    public void insertLevel(LevelMeta level) throws SQLException {
        String statementString = "INSERT INTO Level_Meta (LevelID, creator, date_created, title) VALUES (?,?,?,?)";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, level.levelID);
        s.setString(2, level.creatorID);
        s.setDate(3, level.dateCreated);
        s.setString(4, level.title);
        s.execute();
    }

    /**
     * Gets list of levels sorted by age,
     *
     * @param page      - Which page to retrieve, starts at 1
     * @param page_size - Rows per page, max of 50
     */
    public LevelMeta[] getLevels(int page, int page_size) {
        try {
            String statementString = "SELECT * FROM Level_Meta ORDER BY date_created LIMIT ? OFFSET ?";
            PreparedStatement s = connection.prepareStatement(statementString);
            s.setInt(1, page_size);
            s.setInt(2, (page - 1) * page_size);
            ResultSet results = s.executeQuery();
            return handleResultsOfLevelList(results);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**Parses a ResultSet object's data and formats it into an array of LevelMeta objects*/
    public LevelMeta[] handleResultsOfLevelList(ResultSet results) throws SQLException {
        if (!results.next()) {
            return new LevelMeta[0];
        }
        // converts result set to array.
        ArrayList<LevelMeta> list = new ArrayList<>();
        do {
            list.add(resultToLevelMetaObj(results));
        } while (results.next());
        LevelMeta[] array = new LevelMeta[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Gets list of levels sorted by age, also includes a search parameter
     *
     * @param page      - Which page to retrieve, starts at 1
     * @param page_size - Rows per page, max of 50
     * @param searchTerm    - Search parameter
     */
    public LevelMeta[] getLevelsWithSearch(int page, int page_size, String searchTerm) {
        try {
            String statementString = "SELECT * FROM Level_Meta WHERE title LIKE ? OR instr(title, ?) > 0 ORDER BY date_created LIMIT ? OFFSET ?";
            PreparedStatement s = connection.prepareStatement(statementString);
            s.setString(1, "%"+searchTerm+"%");
            s.setString(2, searchTerm);
            s.setInt(3, page_size);
            s.setInt(4, (page - 1) * page_size);
            ResultSet results = s.executeQuery();
            return handleResultsOfLevelList(results);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**Inserts leaderboard entry of a users completion time for a level into the leaderboard entry table*/
    public void insertLeaderBoardEntry(String levelID, String username, int time) throws SQLException {
        String statementString = "INSERT INTO Leaderboard (LevelID, username, completed) VALUES (?,?,?)";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, levelID);
        s.setString(2, username);
        s.setInt(3, time);
        s.execute();
    }

    /**Returns fastest time for a level
     * @param id the level to get the time for*/
    public LeaderboardEntry getLeaderboardForLevelById(String id) {
        try {
            String statementString = "SELECT * FROM Leaderboard WHERE LevelID = ? ORDER BY completed ASC LIMIT 1 ";
            PreparedStatement s = connection.prepareStatement(statementString);
            s.setString(1, id);
            ResultSet results = s.executeQuery();
            if (!results.next()) {
                return null;
            }
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.levelID = id;
            entry.time = Long.parseLong(results.getString("completed"));
            entry.username = results.getString("username");

            return entry;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public LevelMeta findLevelMeta(String id) {
        try {
            String statementString = "SELECT * FROM Level_Meta WHERE LevelID = ?";
            PreparedStatement s = connection.prepareStatement(statementString);
            s.setString(1, id);
            ResultSet results = s.executeQuery();

            if (!results.next()) {
                return null;
            }

            return resultToLevelMetaObj(results);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new weapon and equips for the user, should be used when a new user is created to ensure that their inventory is not empty
     */
    public void equipDefaultWeapon(String username) throws SQLException {
        // generates default weapon
        Weapon w = new Weapon();
        w.username = username;
        w.weaponID = generateWeaponID();
        w.range = 3;
        w.damage = 1;
        w.isMelee = true;
        w.knockback = 1;
        w.attackspeed = 500;
        w.textureName = "game/weapons/weapon_anime_sword";
        this.insertWeapon(w);
        this.setUserEquippedWeapon(username, w.weaponID);
    }

    /**Converts a ResultSet object into a LevelMeta object. Assumes that there is onyl one entry in the
     * result set object but will not fail if there are more.*/
    public LevelMeta resultToLevelMetaObj(ResultSet results) {
        LevelMeta levelMeta = new LevelMeta();
        try {
            levelMeta.levelID = results.getString("LevelID");
            levelMeta.creatorID = results.getString("creator");
            levelMeta.dateCreated = results.getDate("date_created");
            levelMeta.title = results.getString("title");

        } catch (SQLException e) {
            System.out.println("Error reading level row");
            System.out.println(e.getMessage());
            return null;
        }
        return levelMeta;
    }

    /**Returns a user object where the username matches the passed in name. Returns null if it does not exist.*/
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

    private Weapon resultsToWeaponObj(ResultSet results) {
        Weapon w = new Weapon();
        try {
            w.attackspeed = results.getInt("attackspeed");
            w.knockback = results.getInt("knockback");
            w.damage = results.getInt("damage");
            w.isMelee = results.getBoolean("isMelee");
            w.textureName = results.getString("textureName");
            w.weaponID = results.getString("weaponID");
            w.username = results.getString("username");
            w.range = results.getInt("range");
            return w;
        } catch (SQLException e) {
            System.out.println("Error reading weapon");
            System.out.println(e.getMessage());
            return null;
        }
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

    public Weapon getEquippedWeapon(String userId) throws SQLException {
        String statementString = "SELECT Weapons.* FROM Weapons INNER JOIN Users U on Weapons.username = U.username and Weapons.weaponID = U.equipped_weapon WHERE U.username = ?";
        PreparedStatement s = connection.prepareStatement(statementString);
        s.setString(1, userId);
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
