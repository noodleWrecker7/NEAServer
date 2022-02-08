package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.adamhodgkinson.Models.User;
import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;

public class UserService {
    MongoDB mongoDB;
    SQLiteDB sqLiteDB;

    public UserService(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
    }

    public Route testService = (request, response) -> {
        System.out.println("test service");
        System.out.println(request.session().id());
        Thread.currentThread().wait(1000);
        response.status(200);
        return "Hello World!";
    };
    public Route getUser = (request, response) -> {
        return null;
    };

    public Route login = (request, response) -> {
        Gson g = new Gson();
        SignupBody body;
        try {
            body = g.fromJson(request.body(), SignupBody.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Error reading json");
            response.status(400);
            return "Failed to read request";
        }

        User u = sqLiteDB.findUserByName(body.username);
        if (u == null || !u.password_hash.equals(hash_password(body.password, u.password_salt))) {
            response.status(401);
            return "Invalid login details";
        }
        response.status(200);
        request.session(request.session(false) == null)
                .attribute("username", u.username);

        return "Log in Successful";
    };

    public Route signup = (request, response) -> {
        Gson g = new Gson();
        SignupBody body;
        try {
            body = g.fromJson(request.body(), SignupBody.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Error reading json");
            response.status(400);
            return "Failed to read request";
        }
        if (sqLiteDB.findUserByName(body.username) != null) {
            response.status(409);
            return "Username already taken";
        }
        User u = new User();
        u.username = body.username;
        u.password_salt = generateSalt();
        u.password_hash = hash_password(body.password, u.password_salt);
        try {
            sqLiteDB.insertUser(u);
        } catch (SQLException e) {
            response.status(500);
            return "Error inserting record";
        }

        response.status(200);
        request.session(request.session(false) == null)
                .attribute("username", u.username);
        return "Account creation successful!";
    };

    // generates random salt
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Arrays.toString(salt);
    }

    public String hash_password(String password, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Arrays.toString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
            System.out.println("Password hashing algorithm failed");
            return null;
        }
    }

    static class SignupBody {
        String username;
        String password;
    }
}


