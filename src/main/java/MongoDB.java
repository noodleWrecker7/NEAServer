import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

    MongoDatabase database;

    public MongoDB(String username, String password) {
        try {
            ConnectionString connectionString = new ConnectionString("mongodb+srv://" + username + "x:" + password + "@cluster0.tty2i.mongodb.net/ShootyStabby?retryWrites=true&w=majority");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("ShootyStabby");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Database name refused by server");
            e.printStackTrace();
        } catch (MongoConfigurationException e) {
            System.out.println("Error: Mongo Connection invalid");
            e.printStackTrace();
        }
    }
}
