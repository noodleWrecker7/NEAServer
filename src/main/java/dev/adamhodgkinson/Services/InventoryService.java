package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import dev.adamhodgkinson.Models.Weapon;
import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import spark.Route;

import java.sql.SQLException;
import java.util.Random;

public class InventoryService {
    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

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
        Gson g = new Gson();
        response.status(200);
        return g.toJson(inv);


    };

    public Route getEquippedWeapon = (request, response) -> {
        return null;
    };

    public Route setEquippedWeapon = (request, response) -> {
        String weaponID = request.body();
        // first get weapon to check it belongs to this user
        try {
            Weapon weapon = sqLiteDB.getWeaponById(weaponID);
            if(weapon == null){
                response.status(500);
                return "Could not locate weapon";
            }
            if (weapon.username != request.session().attribute("username")){
                response.status(403);
                return "You dont not own this weapon";
            }



        } catch (SQLException e) {
            response.status(500);
            return "Error reading from DB";
        }


        return null;
    };
    public Route createWeapon = (request, response) -> {
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to access this feature";
        }
        Weapon w = new Gson().fromJson(request.body(), Weapon.class);
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

    public Route removeWeapon = (request, response) -> {
        return null;
    };

    String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private String generateWeaponID() {
        Random r = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            s.append(charset.charAt(r.nextInt(charset.length())));
        }
        return "WEP" + s;
    }

}


