import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public abstract class Item implements UI, DataOps {
    private int uid;
    private static int idCount = 0;
    private String name;
    private String unit = "Unit";
    private double volume = 0;
    private double pricePerUnit = 0;

    public void inputInfo(Scanner scan) {
        System.out.print("Name: ");
        name = scan.nextLine();
        System.out.print("Unit of measurement: ");
        unit = scan.nextLine();
        volume = validateDouble(0, 9999, "Available volume (" + unit + ")");
        pricePerUnit = validateDouble(0, 9999, "Price ($/" + unit + ")");
    }

    public Item() {
        uid = ++idCount;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void modify(Scanner scan) {
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        Document search = new Document("uid", uid);
        Document document = collection.find(search).first();
        Document update = new Document("uid", uid);
        update.put("name", name);
        update.put("unit", unit);
        update.put("volume", volume);
        update.put("pricePerUnit", pricePerUnit);
        if (document != null) {
            Document updateOp = new Document("$set", update);
            collection.updateOne(document, updateOp);
            return;
        }
        collection.insertOne(update);
    }

    public void getFromDocument(Document document) {
        uid = (int) document.get("uid");
        name = (String) document.get("name");
        unit = (String) document.get("unit");
        volume = (double) document.get("volume");
        pricePerUnit = (double) document.get("pricePerUnit");
    }

    public String toString() {
        return ("ID: " + Integer.toString(uid) + "\n" + "Name: " + name + "\n" + "Volume: " + Double.toString(volume)
                + " " + unit + "\n" + "Pricing: " + pricePerUnit + "$/" + unit + "\n");
    }
}
