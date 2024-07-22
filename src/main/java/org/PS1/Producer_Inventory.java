package org.PS1;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.List;

// This class serves as a Kafka producer for Inventory type data.
// It reads data from the inventory_data.json file and sends it to the Kafka cluster.

public class Producer_Inventory {
    private static final Logger log = LoggerFactory.getLogger(Producer_Inventory.class);

    public static void main(String[] args) {
        log.info("I am a Kafka Producer");
        String bootstrapServers = "127.0.0.1:9092";

        // Create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        Scanner sc = new Scanner(System.in);
        // This is done to prevent overlap of id's
        System.out.println("Enter the first id not yet posted");
        int startID = sc.nextInt();
        // Send data - asynchronous
        String topic = "Inventory";

        try {
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/java/org/PS1/inventory_data.json")), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonData);

            // Iterating through each Inventory object read from the JSON file
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject i = jsonArray.getJSONObject(j);
                int itemID = i.getInt("itemID");

                if (itemID >= startID) { // Done to prevent resending already sent data to the Kafka cluster
                    String key = "id_" + itemID;
                    JSONObject entity = new JSONObject();
                    entity.put("entity", i);

                    String value = entity.toString();
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
                    producer.send(producerRecord, new Callback() {
                        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                            // Executes every time a record is successfully sent or an exception is thrown
                            if (e == null) {
                                // The record was successfully sent
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Flush data - synchronous
        producer.flush();
        // Flush and close producer
        producer.close();
    }
}
