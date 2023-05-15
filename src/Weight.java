import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class Weight extends Item {
    private double weight;

    public void inputInfo(Scanner scan) {
        super.inputInfo(scan);
        weight = validateDouble(0, 9999, "Weight (g)");
    }

    public Weight() {
    }

    public Weight(Scanner scan) {
        super();
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        super.addToCollection(collection);
        Document search = new Document("uid", getUid());
        Document document = collection.find(search).first();
        Document newDoc = new Document();
        newDoc.put("weight", weight);
        Document updateOp = new Document("$set", newDoc);
        collection.updateOne(document, updateOp);
    }

    public void getFromDocument(Document document) {
        super.getFromDocument(document);
        weight = (double) document.get("weight");
    }

    public String toString() {
        return ("[Weight]\n" + super.toString() +
                "Weight: " + weight + "g\n");
    }
}