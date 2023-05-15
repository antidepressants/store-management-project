import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class ChemicalCompound extends Item {
    private String formula = "";

    public void inputInfo(Scanner scan) {
        super.inputInfo(scan);
        while (true) {
            String element;
            int num;
            try {
                System.out.println("Enter new element or type \"end\" to stop");
                element = scanner.next();
                if (element.equals("end"))
                    break;
                if (Character.isLowerCase(element.charAt(0)))
                    throw new ArithmeticException();
                for (int i = 1; i < element.length(); i++) {
                    if (Character.isUpperCase(element.charAt(i)))
                        throw new ArithmeticException();
                }
                System.out.println("Enter number of " + element + " atoms");
                num = scanner.nextInt();
                formula += element + Integer.toString(num);
            } catch (Exception e) {
                System.out.println(
                        "Invalid input, element must follow this format: [Uppercase letter][lowercase letter(s)]");
            }
        }
    }

    public ChemicalCompound() {
    }

    public ChemicalCompound(Scanner scan) {
        super();
        inputInfo(scan);
    }

    public void addToCollection(MongoCollection<Document> collection) {
        super.addToCollection(collection);
        Document search = new Document("uid", getUid());
        Document document = collection.find(search).first();
        Document newDoc = new Document();
        newDoc.put("formula", formula);
        Document updateOp = new Document("$set", newDoc);
        collection.updateOne(document, updateOp);
    }

    public void getFromDocument(Document document) {
        super.getFromDocument(document);
        formula = (String) document.get("formula");
    }

    public String toString() {
        return ("[Chemical Compound]\n" + super.toString() + "Formula: " + formula + "\n");
    }
}
