package dev.adamhodgkinson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class MongoDB {

    MongoDatabase database;
    MongoClient mongoClient;

    public MongoDB(String username, String password) {
        try {
            ConnectionString connectionString = new ConnectionString("mongodb+srv://" + username + ":" + password + "@cluster0.tty2i.mongodb.net/ShootyStabby?retryWrites=true&w=majority");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("ShootyStabby");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Database name refused by server");
            e.printStackTrace();
            Server.close();
        } catch (MongoConfigurationException e) {
            System.out.println("Error: Mongo Connection invalid");
            e.printStackTrace();
            Server.close();
        }
    }

    public ClientSession newSession() {
        return mongoClient.startSession();
    }

    public String getLevelJsonData(String id){
        Bson filter = Filters.eq("_id", new ObjectId( id));
        Document doc = database.getCollection("Levels").find(filter).first();
        if(doc == null){
            return null;
        }
        return doc.get("levelcode", String.class);
    }

    public String insertNewLevelDoc(String levelcode, ClientSession session) {
        Document level = new Document("levelcode", levelcode);
        InsertOneResult result = database.getCollection("Levels").insertOne(session, level);
        System.out.println("Inserted new level: " + result.getInsertedId());
        try {
            return result.getInsertedId().asObjectId().getValue().toString();
        } catch (NullPointerException e) {
            System.out.println("Error inserting document");
            return null;
        }
    }

    public void close() {
        mongoClient.close();
    }
}
