package org.PS1;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringSerializer;
public class Prod_Sales {
    private static final Logger log = LoggerFactory.getLogger(Prod_Sales.class);
    //private static final String BASE_URL = "http://localhost:80/gilhari/v1";
    // Gson is used here to convert a Java object of type Entity into a JSON object
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        log.info("I am a Kafka Producer");
        Scanner sc = new Scanner(System.in);
        int n = 1;
        String[] items = new String[n];
        //Taking names of employees as user input
        System.out.println("Enter 10 items");
        for (int i = 0; i < n; i++) {
            String a = sc.next();
            items[i] = a;
        }
        HttpClient client = HttpClient.newHttpClient();
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        double currentQuantity = 0.0, quantity = 0.0;
        // send data - asynchronous
        String topic = "Sales";
        try
        {
            FileReader fr=new FileReader("src/main/java/org/PS1/sales_data.json");
            Gson gson = new Gson();
            Type salesListType = new TypeToken<List<Sales>>() {}.getType();
            List<Sales> sl = gson.fromJson(fr, salesListType);
            for(Sales s:sl)
            {
                String key = "id_" + s.getId();
                Entity_Sales entity=new Entity_Sales(s);
                String value=gson.toJson(entity);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic,key,value);
                producer.send(producerRecord, new Callback() {
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        // executes every time a record is successfully sent or an exception is thrown
                        if (e == null) {
                            // the record was successfully sent
                            log.info("Received new metadata. \n" +
                                    "Topic:" + recordMetadata.topic() + "\n" +
                                    "Key:" + producerRecord.key() + "\n" +
                                    "Partition: " + recordMetadata.partition() + "\n" +
                                    "Offset: " + recordMetadata.offset() + "\n" +
                                    "Timestamp: " + recordMetadata.timestamp());
                        } else {
                            log.error("Error while producing", e);
                        }
                    }
                });
                try {
                    URI uri = URI.create("http://localhost:80/gilhari/v1/Inventory/getObjectById?filter=itemID=" + s.getItemID());
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(uri)
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    int getStatusCode = response.statusCode();
                    System.out.println(getStatusCode);
                    //System.out.println(response.body());
                    Inventory inv = gson.fromJson(response.body(), Inventory.class);
                    currentQuantity=inv.getQuantity();
                    quantity=s.getQuantity();
                    double currInv = currentQuantity - quantity;
                    JsonObject js = new JsonObject();
                    JsonArray jsa = new JsonArray();
                    jsa.add("quantity");
                    jsa.add(currInv);
                    js.add("newValues", jsa);

                    URI uri2 = URI.create("http://localhost:80/gilhari/v1/Inventory?filter=itemID=" + s.getItemID());
                    HttpRequest req1 = HttpRequest.newBuilder()
                            .uri(uri2)
                            .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(js)))
                            .header("Content-Type", "application/json")
                            .build();

                    HttpResponse<String> resp1 = client.send(req1, HttpResponse.BodyHandlers.ofString());
                    int patchStatusCode = resp1.statusCode();
                    System.out.println(patchStatusCode);

                    if (patchStatusCode >= 200 && patchStatusCode < 300) {
                        log.info("Response from API: " + resp1.body());
                        continue;
                    } else {
                        log.error("HTTP error code: " + patchStatusCode);
                    }


                } catch (Exception e) {
                    log.error("Exception occurred while sending HTTP request: {}", e.getMessage());
                }


            }


        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        // flush data - synchronous
        producer.flush();
        // flush and close producer
        producer.close();

    }
}
