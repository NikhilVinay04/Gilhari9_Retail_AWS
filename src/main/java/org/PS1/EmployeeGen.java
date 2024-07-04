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

// Generates data of Employee type and puts it in the employee_data.json file.
public class EmployeeGen
{
    public static void main(String[] args) throws IOException {
        String employeeFile = "src/main/java/org/PS1/employee_data.json";
        Scanner sc=new Scanner(System.in);
        // Starting id is entered to avoid trying to create new records with already existing id's as this helps avoid
        // errors when posting data with already posted id's
        System.out.println("Enter the starting id");
        int startId = sc.nextInt();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            // Generate employee data
            Type employeeListType = new TypeToken<ArrayList<Employee>>() {}.getType();
            // Creating a list of Employees to store Employee data which is later written to the file
            List<Employee> empdata = new ArrayList<>();
            Random random = new Random();
            try {
                if (Files.exists(Paths.get(employeeFile))) {
                    FileReader reader = new FileReader(employeeFile);
                    empdata = gson.fromJson(reader, employeeListType);
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Creating 20 new Employee entries
            for (int i = startId; i < startId+20; i++)
            {
                // Instantiating a User object which represents an Employee
                double salary=random.nextInt(1,1000001);
                long now = System.currentTimeMillis();
                long oneYearAgo = now - (365L * 24 * 60 * 60 * 1000);
                long dob = ThreadLocalRandom.current().nextLong(oneYearAgo, now);
                int rando=random.nextInt();
                boolean exempt=rando%2==0;
                Employee empEntry = new Employee(
                        Integer.toString(i),
                        "Item"+i,
                        exempt,
                        salary,
                        dob
                );
                empdata.add(empEntry);
            }

            // Write employee data to file

            try (FileWriter writer = new FileWriter(employeeFile))
            {
                gson.toJson(empdata, writer);
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
