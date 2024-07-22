package org.PS1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Generates data of Shipment type and puts it in the shipment_data.json file.
public class ShipmentGen {
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        String shipFile = "src/main/java/org/PS1/shipment_data.json";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();

        try {
            // Read inventory data from file
            String inventoryJson = new String(Files.readAllBytes(Paths.get(inventoryFile)), StandardCharsets.UTF_8);
            JSONArray inventoryArray = new JSONArray(inventoryJson);

            // Generate shipment data
            JSONArray shipArray = new JSONArray();
            if (Files.exists(Paths.get(shipFile))) {
                String shipJson = new String(Files.readAllBytes(Paths.get(shipFile)), StandardCharsets.UTF_8);
                shipArray = new JSONArray(shipJson);
            }

            // Select 10 random items from inventory
            List<JSONObject> inventoryList = new ArrayList<>();
            for (int i = 0; i < inventoryArray.length(); i++) {
                inventoryList.add(inventoryArray.getJSONObject(i));
            }
            Collections.shuffle(inventoryList);
            List<JSONObject> selectedItems = inventoryList.subList(0, 10);

            Random random = new Random();
            for (int i = startId; i < startId + selectedItems.size(); i++) {
                JSONObject item = selectedItems.get(i - startId);
                double shipQuantity = random.nextInt(1, 100);
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long salesDate = ThreadLocalRandom.current().nextLong(oneYearAgo, now);
                String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(salesDate));

                JSONObject shipEntry = new JSONObject();
                shipEntry.put("id", String.valueOf(i));
                shipEntry.put("itemID", Integer.toString(item.getInt("itemID")));
                shipEntry.put("itemName", item.getString("itemName"));
                shipEntry.put("quantity", shipQuantity);
                shipEntry.put("date", dateString);

                shipArray.put(shipEntry);
            }

            // Write shipment data to file
            try (FileWriter writer = new FileWriter(shipFile)) {
                writer.write(shipArray.toString(4)); // Indented with 4 spaces for pretty printing
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
