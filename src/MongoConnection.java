import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public interface MongoConnection {
    public default MongoDatabase getDatabase(String uri, String databaseName) {
        ConnectionString connectionString = new ConnectionString(uri);
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString)
                .serverApi(serverApi)
                .build();
        try {
            MongoClient client = MongoClients.create(settings);
            MongoDatabase database = client.getDatabase(databaseName);
            System.out.println("Successfully connected to mongodb");
            return database;
        } catch (MongoException e) {
            System.out.println("Failed to connect to mongodb");
            return null;
        }
    }

    public default boolean collectionExists(MongoDatabase database, String collection) {
        for (String col : database.listCollectionNames()) {
            if (col.equals(collection))
                return true;
        }
        return false;
    }

}
