import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Store implements UI, MongoConnection {
    ArrayList<Item> items = new ArrayList<Item>();
    static private String menu[] = { "Quit", "Add item", "Remove item", "Modify item", "Search" };
    String databaseType;
    String databasePath = "data";
    private static String categories[] = { "Chemical Compounds", "Containers", "Balances",
            "Weights", "Heating Equipment" };
    private static HashMap<Class<?>, String> classMap = new HashMap<Class<?>, String>();

    public Store() {
        classMap.put(ChemicalCompound.class, "compounds");
        classMap.put(Container.class, "containers");
        classMap.put(Balance.class, "balances");
        classMap.put(Weight.class, "weights");
        classMap.put(HeatingEquipment.class, "heating");
    }

    public void selectDB(Scanner scan) {
        int choice = -1;
        String options[] = { "mongodb", "json" };
        choice = chooseFromList(options);
        if (choice == -1) {
            selectDB(scan);
            return;
        }
        databaseType = options[choice];
        System.out.print("Database path/URI: ");
        databasePath = scan.nextLine();
    }

    private void loadData() {
        if (databaseType == "mongodb") {
            for (Class<?> className : classMap.keySet()) {
                MongoDatabase database = getDatabase(databasePath, "store");
                MongoCollection<Document> collection = database.getCollection(classMap.get(className));
                for (Document document : collection.find()) {
                    try {
                        Item item; // i died trying to do this some other way, java is a language written by the
                                   // devil
                        if (className == ChemicalCompound.class) {
                            item = new ChemicalCompound();
                            item.getFromDocument(document);
                            items.add(item);
                            break;
                        }
                        if (className == Container.class) {
                            item = new Container();
                            item.getFromDocument(document);
                            items.add(item);
                            break;
                        }
                        if (className == Balance.class) {
                            item = new Balance();
                            item.getFromDocument(document);
                            items.add(item);
                            break;
                        }
                        if (className == Weight.class) {
                            item = new Weight();
                            item.getFromDocument(document);
                            items.add(item);
                            break;
                        }
                        if (className == HeatingEquipment.class) {
                            item = new HeatingEquipment();
                            item.getFromDocument(document);
                            items.add(item);
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to fetch object");
                    }
                }
            }
            return;
        }
        Reader reader;
        Gson gson = new Gson();
        for (Class<?> className : classMap.keySet()) {
            try {
                reader = Files.newBufferedReader(Paths.get(databasePath + "/" + classMap.get(className) + ".json"));
                ArrayList<Item> tempItems = new ArrayList<Item>();
                tempItems = gson.fromJson(reader,
                        TypeToken.getParameterized(ArrayList.class, className).getType());
                tempItems.forEach(item -> items.add(item));
                reader.close();
            } catch (Exception e) {
                System.out
                        .println("Failed to read from file " + databasePath + "/" + classMap.get(className) + ".json");
                selectDB(scanner);
            }
        }
    }

    private void saveData() {
        if (databaseType == "mongodb") {
            MongoDatabase database = getDatabase(databasePath, "store");
            for (Class<?> className : classMap.keySet()) {
                try {
                    if (collectionExists(database, classMap.get(className)))
                        database.getCollection(classMap.get(className)).drop();
                    database.createCollection(classMap.get(className));
                    MongoCollection<Document> collection = database.getCollection(classMap.get(className));
                    items.stream().filter(item -> className.isInstance(item))
                            .forEach(item -> item.addToCollection(collection));
                } catch (MongoException e) {
                    System.out.println("Failed to write to mongodb");
                }
            }
            return;
        }
        Writer writer;
        Gson gson = new Gson();
        for (Class<?> className : classMap.keySet()) {
            try {
                writer = Files.newBufferedWriter(Paths.get(databasePath + "/" + classMap.get(className) + ".json"));
                gson.toJson(items.stream().filter(item -> className.isInstance(item)).collect(Collectors.toList()),
                        writer);
                writer.close();
            } catch (Exception e) {
                System.out.println("Failed to write to file " + classMap.get(className) + ".json");
            }
        }
    }

    public int getItem(int uid) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUid() == uid)
                return i;
        }
        return -1;
    }

    public void addItem() {
        int choice = -1;
        choice = chooseFromList(categories);
        switch (choice) {
            case -1:
                return;
            case 0:
                items.add(new ChemicalCompound(scanner));
                return;
            case 1:
                items.add(new Container(scanner));
                return;
            case 2:
                items.add(new Balance(scanner));
                return;
            case 3:
                items.add(new Weight(scanner));
                return;
            case 4:
                items.add(new HeatingEquipment(scanner));
                return;
        }
    }

    public void removeItem(Scanner scan) {
        int uid = 0;
        System.out.println("Remove item:");
        System.out.print("Item ID: ");
        try {
            uid = Integer.parseInt(scan.nextLine());
            if (getItem(uid) < 0)
                throw new ArithmeticException();
            items.remove(getItem(uid));
        } catch (Exception e) {
            System.out.println("Invalid input, expected item ID");
        }
    }

    public void modifyItem(Scanner scan) {
        int uid = 0;
        System.out.println("Modify item:");
        System.out.print("Item ID: ");
        try {
            uid = Integer.parseInt(scan.nextLine());
            if (getItem(uid) < 0)
                throw new ArithmeticException();
            items.get(getItem(uid)).inputInfo(scanner);
        } catch (Exception e) {
            System.out.println("Invalid input, expected item ID");
        }
    }

    public void search(Scanner scan) {
        String keyword;
        System.out.print("Search keyword: ");
        keyword = scan.nextLine();

        items.stream().filter(item -> item.getName().toUpperCase().equals(keyword.toUpperCase()))
                .forEach(System.out::println);

        items.stream()
                .filter(item -> !(item.getName().toUpperCase().equals(keyword.toUpperCase()))
                        && item.getName().toUpperCase().contains(keyword.toUpperCase())
                        || keyword.toUpperCase().contains(item.getName().toUpperCase()))
                .sorted((item1, item2) -> item1.getName().compareTo(item2.getName())).forEach(System.out::println);

        items.stream()
                .filter(item -> !(item.getName().toUpperCase().equals(keyword.toUpperCase())
                        || item.getName().toUpperCase().contains(keyword.toUpperCase())
                        || keyword.toUpperCase().contains(item.getName().toUpperCase()))
                        && LevenshteinDistance.compute_Levenshtein_distance(item.getName().toUpperCase(),
                                keyword.toUpperCase()) < item.getName().length() / 2)
                .sorted((item1, item2) -> LevenshteinDistance
                        .compute_Levenshtein_distance(item1.getName().toUpperCase(), keyword.toUpperCase())
                        .compareTo(LevenshteinDistance.compute_Levenshtein_distance(item2.getName().toUpperCase(),
                                keyword.toUpperCase())))
                .forEach(System.out::println);
    }

    public boolean mainMenu() {
        int choice = -1;
        choice = chooseFromList(menu);
        switch (choice) {
            case -1:
                return true;
            case 0:
                exit(scanner);
                return false;
            case 1:
                addItem();
                return true;
            case 2:
                removeItem(scanner);
                return true;
            case 3:
                modifyItem(scanner);
                return true;
            case 4:
                search(scanner);
                return true;
        }
        return false;
    }

    public void exit(Scanner scan) {
        System.out.print("Do you wish to save your changes? [y/n]: ");
        try {
            String choice = scan.next();
            scan.nextLine();
            if (!(choice.equals("y") || choice.equals("n")))
                throw new ArithmeticException();
            if (choice.equals("n"))
                return;
            saveData();
        } catch (Exception e) {
            System.out.println("Invalid input");
            exit(scan);
        }
    }

    public static void main(String args[]) {
        Store store = new Store();
        store.selectDB(scanner);
        store.loadData();
        while (store.mainMenu()) {
        }
    }
}