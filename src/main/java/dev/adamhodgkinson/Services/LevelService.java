package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.ClientSession;
import dev.adamhodgkinson.Models.LevelMeta;
import dev.adamhodgkinson.MongoDB;
import dev.adamhodgkinson.SQLiteDB;
import dev.adamhodgkinson.Utils;
import spark.Route;

import java.sql.Date;
import java.sql.SQLException;

public class LevelService {

    MongoDB mongoDB;
    SQLiteDB sqLiteDB;
    Gson g = new Gson();

    public LevelService(MongoDB mongoDB, SQLiteDB sqLiteDB) {
        this.mongoDB = mongoDB;
        this.sqLiteDB = sqLiteDB;
    }

    public Route listLevels = (request, response) -> {
        ListLevelsBody body;
        body = Utils.convertBodyToObject(request.body(), ListLevelsBody.class);

        if (body == null) {
            response.status(400);
            return "Failed to read request";
        }
        if (body.page_size > 50) {
            response.status(400);
            return "Page size too large";
        }
        if (body.page < 1) {
            response.status(400);
            return "Page number invalid";
        }
        LevelMeta[] levels = sqLiteDB.getLevels(body.page, body.page_size);
        if (levels == null) {
            response.status(500);
            return "Error reading levels";
        }
        response.status(200);
        return g.toJson(levels);

    };

    public Route getLeaderboard = (request, response) -> {
        return null;
    };

    public Route getLevel = (request, response) -> {
        return null;
    };

    public Route createLevel = (request, response) -> {
        // todo check input level data is actually valid
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to do that!";
        }
        CreateLevelBody body = Utils.convertBodyToObject(request.body(), CreateLevelBody.class);
        if (body == null) {
            response.status(400);
            return "Failed to read request";
        }
        LevelMeta meta = new LevelMeta();
        meta.creatorID = request.session(false).attribute("username");
        meta.title = body.title;
        meta.dateCreated = new Date(System.currentTimeMillis());
        ClientSession session = mongoDB.newSession();
        session.startTransaction();

        String levelString = g.toJson(body.levelCode);
        meta.levelID = mongoDB.insertNewLevelDoc(levelString, session);
        try {
            sqLiteDB.insertLevel(meta);
        } catch (SQLException e) {
            session.abortTransaction();
            session.close();
            System.out.println(e.getMessage());
            response.status(500);
            return "Failed to insert level into database";
        }

        session.commitTransaction();
        session.close();
        response.status(200);
        return "Success";
    };

    static class ListLevelsBody {
        int page;
        int page_size; // max 50
    }

    static class CreateLevelBody {
        String title;
        JsonObject levelCode;
    }
}


