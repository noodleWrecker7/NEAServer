package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.adamhodgkinson.Models.User;
import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import spark.Route;
import spark.Session;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;

public class InventoryService {
    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

    public InventoryService(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
    }

}


