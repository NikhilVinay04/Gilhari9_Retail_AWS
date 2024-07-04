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

// Generates data of Sales type and puts it in the sales_data.json file.
public class SalesGen
{
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        String salesFile = "src/main/java/org/PS1/sales_data.json";
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
            Type salesListType = new TypeToken<ArrayList<Sales>>(){}.getType();
            List<Sales> salesData = new ArrayList<>();
            Random random = new Random();
            try {
                if (Files.exists(Paths.get(salesFile))) {
                    FileReader reader = new FileReader(salesFile);
                    salesData = gson.fromJson(reader, salesListType);
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
                double salesQuantity = 0;
                if(item.getQuantity()>=1) {
                    salesQuantity=(random.nextInt(1, (int) item.getQuantity() + 1));
                }
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long salesDate = ThreadLocalRandom.current().nextLong(oneYearAgo, now);

                Sales salesEntry = new Sales(
                        String.valueOf(i),
                        String.valueOf(item.getItemID()),
                        item.getItemName(),
                        salesQuantity,
                        salesDate
                );
                salesData.add(salesEntry);
            }

            // Write sales data to file

            try (FileWriter writer = new FileWriter(salesFile)) {
                gson.toJson(salesData, writer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
