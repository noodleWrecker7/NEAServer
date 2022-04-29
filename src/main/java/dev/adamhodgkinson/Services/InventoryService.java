package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import dev.adamhodgkinson.Models.Weapon;
import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import spark.Route;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class InventoryService {
    MongoDB mongoDB;
    SQLiteDB sqLiteDB;
    Gson g = new Gson();

    public InventoryService(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
    }

    public Route getInventory = (request, response) -> {
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to access this feature";
        }
        Weapon[] inv = sqLiteDB.getInventory(request.session().attribute("username"));
        if (inv == null) {
            response.status(500);
            return "Error reading inventory";
        }
        response.status(200);
        return g.toJson(inv);


    };

    public Route getEquippedWeapon = (request, response) -> {
        // authenticates user
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to access this feature";
        }
        Weapon wep = sqLiteDB.getEquippedWeapon(request.session().attribute("username"));
        if (wep == null) {
            sqLiteDB.equipDefaultWeapon(request.session().attribute("username"));
        }
        try {
            wep = sqLiteDB.getEquippedWeapon(request.session().attribute("username"));
        } catch (SQLException e) {
            response.status(500);
            return "Error occurred inserting default weapon";
        }
        response.status(200);
        return g.toJson(wep, Weapon.class);
    };

    /**
     * Sets a players equipped weapon
     */
    public Route setEquippedWeapon = (request, response) -> {
        String weaponID = request.body();
        // first get weapon to check it belongs to this user
        try {
            // retreives weapon data first
            Weapon weapon = sqLiteDB.getWeaponById(weaponID);
            if (weapon == null) {// checks the weapon actually exists
                response.status(500);
                return "Could not locate weapon";
            }
            // checks the weapon is actually owned by the user
            if (!weapon.username.equals(request.session().attribute("username"))) {
                response.status(403);
                return "You dont not own this weapon";
            }
            // sets users equipped weapon
            sqLiteDB.setUserEquippedWeapon(request.session().attribute("username"), weaponID);

            response.status(200);
            return "Successfully equipped";
        } catch (SQLException e) {
            response.status(500);
            return "Error reading from DB";
        }
    };

    public Route createWeapon = (request, response) -> {
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to access this feature";
        }
        Weapon w = g.fromJson(request.body(), Weapon.class);
        w.weaponID = generateWeaponID();
        w.username = request.session().attribute("username");
        System.out.println(w.username);
        try {
            sqLiteDB.insertWeapon(w);
        } catch (SQLException e) {
            response.status(500);
            return "Error inserting to DB " + e.getMessage();
        }
        response.status(200);
        return "Success";
    };

    static String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";


    static int increment = 0;

    public static String generateWeaponID() {
        Random r = new Random();
        StringBuilder s = new StringBuilder();
        s.append(new Date().getTime());
        s.append(increment++);
        if (increment >= 100000) {
            increment = 0;
        }
        for (int i = 0; i < 5; i++) {
            s.append(charset.charAt(r.nextInt(charset.length())));
        }
        return "WEP" + s;
    }

}


