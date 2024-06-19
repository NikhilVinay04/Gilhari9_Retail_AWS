package org.PS1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ShipGen
{
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        String shipFile = "src/main/java/org/PS1/shipment_data.json";
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            // Read inventory data from file
            String inventoryJson = new String(Files.readAllBytes(Paths.get(inventoryFile)));
            Type inventoryListType = new TypeToken<ArrayList<Inventory>>(){}.getType();
            List<Inventory> inventory = new Gson().fromJson(inventoryJson, inventoryListType);

            // Generate sales data
            Type shipListType = new TypeToken<ArrayList<Shipment>>(){}.getType();
            List<Shipment> shipData = new ArrayList<>();
            Random random = new Random();
            try {
                if (Files.exists(Paths.get(shipFile))) {
                    FileReader reader = new FileReader(shipFile);
                    shipData = gson.fromJson(reader, shipListType);
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Select 10 random items from inventory
            Collections.shuffle(inventory);
            List<Inventory> selectedItems = inventory.subList(0, 10);

            for (int i = startId; i < startId+selectedItems.size(); i++) {
                Inventory item = selectedItems.get(i-startId);
                double salesQuantity = (random.nextInt(1,100));
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long salesDate = ThreadLocalRandom.current().nextLong(oneYearAgo, now);

                Shipment shipEntry = new Shipment(
                        String.valueOf(i),
                        String.valueOf(item.getItemID()),
                        item.getItemName(),
                        salesQuantity,
                        salesDate
                );
                shipData.add(shipEntry);
            }

            // Write sales data to file

            try (FileWriter writer = new FileWriter(shipFile))
            {
                gson.toJson(shipData, writer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
