package org.PS1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
// Generates data of Inventory type and puts it in the inventory_data.json file.
public class InventoryGen
{
    public static void main(String[] args) throws IOException {
        String inventoryFile = "src/main/java/org/PS1/inventory_data.json";
        Scanner sc=new Scanner(System.in);
        // Starting id is entered to avoid trying to create new records with already existing id's as this helps avoid
        // errors when posting data with already posted id's
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            // Generate inventory data and creating a List of Inventory objects
            Type inventoryListType = new TypeToken<ArrayList<Inventory>>() {}.getType();
            List<Inventory> invdata = new ArrayList<>();
            Random random = new Random();
            try {
                if (Files.exists(Paths.get(inventoryFile))) {
                    FileReader reader = new FileReader(inventoryFile);
                    invdata = gson.fromJson(reader, inventoryListType);
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Generating 20 data points of Inventory data
            for (int i = startId; i < startId+20; i++)
            {

                double salesQuantity=random.nextInt(1,101);
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long salesDate = ThreadLocalRandom.current().nextLong(oneYearAgo, now);

                Inventory invEntry = new Inventory(
                        i,
                        "Item"+i,
                        salesQuantity,
                        salesDate
                );
                invdata.add(invEntry);
            }

            // Write inventory data to file

            try (FileWriter writer = new FileWriter(inventoryFile))
            {
                gson.toJson(invdata, writer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }



        }
        catch (JsonIOException e) {
            throw new RuntimeException(e);
        }

    }
}
