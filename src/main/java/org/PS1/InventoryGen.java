package org.PS1;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Generates data of Inventory type and puts it in the inventory_data.json file.
public class InventoryGen {
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        Scanner sc = new Scanner(System.in);
        // Starting id is entered to avoid trying to create new records with already existing id's as this helps avoid
        // errors when posting data with already posted id's
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();

        // Generate inventory data and creating a List of Inventory objects
        List<JSONObject> invData = new ArrayList<>();
        Random random = new Random();

        // Check if the file exists and load existing data
        if (Files.exists(Paths.get(inventoryFile))) {
            String jsonData = new String(Files.readAllBytes(Paths.get(inventoryFile)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                invData.add(jsonArray.getJSONObject(i));
            }
        }

        // Generating 20 data points of Inventory data
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long now = System.currentTimeMillis();
        long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);

        for (int i = startId; i < startId + 20; i++) {
            double inventoryQuantity = random.nextInt(1, 101);
            long inventoryDateMillis = ThreadLocalRandom.current().nextLong(oneYearAgo, now);
            String inventoryDate = sdf.format(new Date(inventoryDateMillis));

            JSONObject invEntry = new JSONObject();
            invEntry.put("itemID", i);
            invEntry.put("itemName", "Item" + i);
            invEntry.put("quantity", inventoryQuantity);
            invEntry.put("date", inventoryDate);

            invData.add(invEntry);
        }

        // Write inventory data to file
        try (FileWriter writer = new FileWriter(inventoryFile)) {
            JSONArray jsonArray = new JSONArray(invData);
            writer.write(jsonArray.toString(4));  // Indent with 4 spaces for pretty printing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
