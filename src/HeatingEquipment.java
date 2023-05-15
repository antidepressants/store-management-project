import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class HeatingEquipment extends Item {
    private double maxTemp;

    public void inputInfo(Scanner scan) {
        super.inputInfo(scan);
        maxTemp = validateDouble(0, 1000, "Max temperature (C)");
    }

    public HeatingEquipment() {
    }

    public HeatingEquipment(Scanner scan) {
        super();
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        super.addToCollection(collection);
        Document search = new Document("uid", getUid());
        Document document = collection.find(search).first();
        Document newDoc = new Document();
        newDoc.put("maxTemp", maxTemp);
        Document updateOp = new Document("$set", newDoc);
        collection.updateOne(document, updateOp);
    }

    public void getFromDocument(Document document) {
        super.getFromDocument(document);
        maxTemp = (double) document.get("maxTemp");
    }

    public String toString() {
        return ("[Heating Equipment]\n" + super.toString() +
                "Max temperature: " + maxTemp + "C\n");
    }
}
