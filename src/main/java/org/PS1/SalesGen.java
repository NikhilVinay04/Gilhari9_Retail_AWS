package org.PS1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Generates data of Sales type and puts it in the sales_data.json file.
public class SalesGen {
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        String salesFile = "src/main/java/org/PS1/sales_data.json";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();

        try {
            // Read inventory data from file
            String inventoryJson = new String(Files.readAllBytes(Paths.get(inventoryFile)), StandardCharsets.UTF_8);
            JSONArray inventoryArray = new JSONArray(inventoryJson);

            // Generate sales data
            JSONArray salesData = new JSONArray();
            try {
                if (Files.exists(Paths.get(salesFile))) {
                    String salesJson = new String(Files.readAllBytes(Paths.get(salesFile)), StandardCharsets.UTF_8);
                    salesData = new JSONArray(salesJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Select 10 random items from inventory
            List<JSONObject> inventoryList = new ArrayList<>();
            for (int i = 0; i < inventoryArray.length(); i++) {
                inventoryList.add(inventoryArray.getJSONObject(i));
            }
            Collections.shuffle(inventoryList);
            List<JSONObject> selectedItems = inventoryList.subList(0, Math.min(10, inventoryList.size()));

            Random random = new Random();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = startId; i < startId + selectedItems.size(); i++) {
                JSONObject item = selectedItems.get(i - startId);
                double salesQuantity = 0;
                if (item.getDouble("quantity") >= 1) {
                    salesQuantity = random.nextInt(1, (int) item.getDouble("quantity") + 1);
                }
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long salesDatemillis = ThreadLocalRandom.current().nextLong(oneYearAgo, now);
                String salesDate = sdf.format(new Date(salesDatemillis));
                JSONObject salesEntry = new JSONObject();
                salesEntry.put("id", String.valueOf(i));
                salesEntry.put("itemID", item.getInt("itemID"));
                salesEntry.put("itemName", item.getString("itemName"));
                salesEntry.put("quantity", salesQuantity);
                salesEntry.put("date", salesDate);

                salesData.put(salesEntry);
            }

            // Write sales data to file
            try (FileWriter writer = new FileWriter(salesFile)) {
                writer.write(salesData.toString(4)); // Pretty print JSON with an indentation of 4 spaces
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
