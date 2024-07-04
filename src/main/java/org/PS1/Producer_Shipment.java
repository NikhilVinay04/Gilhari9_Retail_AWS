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

import java.util.Properties;

import org.apache.kafka.common.serialization.StringSerializer;

// This class serves as a Kafka producer for Shipment type data.
// It reads data from the shipment_data.json file and sends it to the Kafka cluster.
// It also updates the Inventory quantity of the item being shipped into storage.

public class Producer_Shipment
{
    private static final Logger log = LoggerFactory.getLogger(Producer_Sales.class);

    // Gson is used here to convert a Java object of type Entity into a JSON object
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        log.info("I am a Kafka Producer");
        HttpClient client = HttpClient.newHttpClient();
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the first id not yet posted");
        int startID=sc.nextInt();
        double currentQuantity = 0.0, quantity = 0.0;
        // send data - asynchronous
        String topic = "Shipment";
        try
        {
            FileReader fr=new FileReader("src/main/java/org/PS1/shipment_data.json");
            Gson gson = new Gson();
            Type shipmentListType = new TypeToken<List<Shipment>>() {}.getType();
            List<Shipment> sl = gson.fromJson(fr, shipmentListType);
            for(Shipment s:sl) {
                if (Integer.parseInt(s.getId()) >= startID)
                {
                    String key = "id_" + s.getId();
                    Entity_Shipment entity = new Entity_Shipment(s);
                    String value = gson.toJson(entity);
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
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
                        //System.out.println(response.body());
                        Inventory inv = gson.fromJson(response.body(), Inventory.class);
                        currentQuantity = inv.getQuantity();
                        quantity = s.getQuantity();
                        double currInv = currentQuantity + quantity;
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

