import org.bson.Document;
import com.mongodb.client.MongoCollection;

public interface DataOps {
    public void addToCollection(MongoCollection<Document> collection);

    public void getFromDocument(Document document);
}
