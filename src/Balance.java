import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class Balance extends Item {
    private double maxWeight;

    public void inputInfo(Scanner scan) {
        super.inputInfo(scan);
        maxWeight = validateDouble(0, 9999, "Max weight (g)");
    }

    public Balance() {
    }

    public Balance(Scanner scan) {
        super();
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        super.addToCollection(collection);
        Document search = new Document("uid", getUid());
        Document document = collection.find(search).first();
        Document newDoc = new Document();
        newDoc.put("maxWeight", maxWeight);
        Document updateOp = new Document("$set", newDoc);
        collection.updateOne(document, updateOp);
    }

    public void getFromDocument(Document document) {
        super.getFromDocument(document);
        maxWeight = (double) document.get("maxWeight");
    }

    public String toString() {
        return ("[Balance]\n" + super.toString() +
                "Max weight: " + maxWeight + "g\n");
    }
}