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

// Generates data of Employee type and puts it in the employee_data.json file.
public class EmployeeGen {
    public static void main(String[] args) throws IOException {
        String employeeFile = "src/main/java/org/PS1/employee_data.json";
        Scanner sc = new Scanner(System.in);
        // Starting id is entered to avoid trying to create new records with already existing id's as this helps avoid
        // errors when posting data with already posted id's
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();

        // Generate employee data
        List<JSONObject> empData = new ArrayList<>();
        Random random = new Random();

        // Check if the file exists and load existing data
        if (Files.exists(Paths.get(employeeFile))) {
            String jsonData = new String(Files.readAllBytes(Paths.get(employeeFile)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                empData.add(jsonArray.getJSONObject(i));
            }
        }

        // Creating 20 new Employee entries
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long now = System.currentTimeMillis();
        long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);

        for (int i = startId; i < startId + 20; i++) {
            double salary = random.nextInt(1, 1000001);
            long dobMillis = ThreadLocalRandom.current().nextLong(oneYearAgo, now);
            String dob = sdf.format(new Date(dobMillis));
            boolean exempt = random.nextInt() % 2 == 0;

            JSONObject empEntry = new JSONObject();
            empEntry.put("id", Integer.toString(i));
            empEntry.put("name", "Item" + i);
            empEntry.put("exempt", exempt);
            empEntry.put("compensation", salary);
            empEntry.put("dob", dob);

            empData.add(empEntry);
        }

        // Write employee data to file
        try (FileWriter writer = new FileWriter(employeeFile)) {
            JSONArray jsonArray = new JSONArray(empData);
            writer.write(jsonArray.toString(4));  // Indent with 4 spaces for pretty printing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
