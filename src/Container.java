import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class Container extends Item {
    private double capacity;
    private String capacityUnit;

    public void inputInfo(Scanner scan) {
        super.inputInfo(scan);
        System.out.print("Unit of Capacity: ");
        capacityUnit = scanner.next();
        capacity = validateDouble(0, 1000, "Capacity (" + capacityUnit + ")");
    }

    public Container() {
    }

    public Container(Scanner scan) {
        super();
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        super.addToCollection(collection);
        Document search = new Document("uid", getUid());
        Document document = collection.find(search).first();
        Document newDoc=new Document();
        newDoc.put("capacity", capacity);
        newDoc.put("capacityUnit", capacityUnit);
        Document updateOp = new Document("$set", newDoc);
        collection.updateOne(document, updateOp);
    }

    public void getFromDocument(Document document) {
        super.getFromDocument(document);
        capacity = (double) document.get("capacity");
        capacityUnit = (String) document.get("capacityUnit");
    }

    public String toString() {
        return ("[Container]\n" + super.toString() +
                "Capacity: " + capacity + " " + capacityUnit + "\n");
    }
}
