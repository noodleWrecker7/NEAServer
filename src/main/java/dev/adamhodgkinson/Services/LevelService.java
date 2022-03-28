package dev.adamhodgkinson.Services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.ClientSession;
import dev.adamhodgkinson.Models.LeaderboardEntry;
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

    /**
     * Lists all level metadata, excludes json data,
     * requires page_size and page_num to be defined in request params
     */
    public Route listLevels = (request, response) -> {
//        ListLevelsBody body;
//        body = Utils.convertBodyToObject(request.body(), ListLevelsBody.class);

//        if (body == null) {
//            response.status(400);
//            return "Failed to read request";
//        }
        int page_size;
        int page_num;
        try {
            page_size = Integer.parseInt(request.queryParams("page_size"));
            page_num = Integer.parseInt(request.queryParams("page_num"));
        } catch (NumberFormatException e) {
            response.status(400);
            return "Params not valid integers";
        }
        if (page_size > 50) {
            response.status(400);
            return "Page size too large";
        }
        if (page_num < 1) {
            response.status(400);
            return "Page number invalid";
        }
        LevelMeta[] levels;

        if (request.queryParams("search") != null && !request.queryParams("search").equals("")) {
            levels = sqLiteDB.getLevelsWithSearch(page_num, page_size, request.queryParams("search"));
        } else {
            levels = sqLiteDB.getLevels(page_num, page_size);
        }

        if (levels == null) {
            response.status(500);
            return "Error reading levels";
        }
        response.status(200);
        return g.toJson(levels);

    };

    public Route setTime = (request, response) -> {
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to do that!";
        }
        if (request.params("id") == null) {
            response.status(400);
            return "Level not specified";
        }

        System.out.println("setting new time");
        System.out.println(request.body());
        SetTimeBody body = g.fromJson(request.body(), SetTimeBody.class);
        System.out.println(body.levelID);
        System.out.println(body.levelID + " " + request.session().attribute("username") + " " + body.time);
        try {
            sqLiteDB.insertLeaderBoardEntry(body.levelID, request.session(false).attribute("username"), body.time);
            response.status(200);
            return "Success";
        } catch (SQLException e) {
            response.status(500);
            return "Error inserting record";
        }
    };

    static class SetTimeBody {
        String levelID;
        int time;
    }


    public Route getLeaderboard = (request, response) -> {
        if (request.params("id") == null) {
            response.status(400);
            return "Level not specified";
        }

        System.out.println("Requesting level " + request.params("id"));

        LeaderboardEntry entry = sqLiteDB.getLeaderboardForLevelById(request.params("id"));
        if (entry != null) {
            response.status(200);
            return g.toJson(entry);
        } else {
            response.status(404);
            return null;
        }
    };

    /**
     * Sends the all the data for a level including json data
     */
    public Route getLevel = (request, response) -> {
        if (request.session(false) == null) {
            response.status(401);
            return "You must be logged in to do that!";
        }
        if (request.params("id") == null) {
            response.status(400);
            return "Level not specified";
        }
        System.out.println(request.params("id"));
        String levelcode = mongoDB.getLevelJsonData(request.params("id"));

        if (levelcode == null) {
            response.status(404);
            return "Failed to find leveldata";
        }

        response.status(200);
        return levelcode;
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


