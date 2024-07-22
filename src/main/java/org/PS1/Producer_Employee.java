package org.PS1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// This class serves as a Kafka producer for Employee type data.
// It reads data from the employee_data.json file and sends it to the Kafka cluster.
public class Producer_Employee {
    private static final Logger log = LoggerFactory.getLogger(Producer_Employee.class);

    public static void main(String[] args) {
        log.info("I am a Kafka Producer");
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // send data - asynchronous
        String topic = "Employees";

        try {
            // Step 1: Read JSON file into a String
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/java/org/PS1/employee_data.json")), StandardCharsets.UTF_8);

            // Step 2: Parse the JSON String into a JSONArray
            JSONArray jsonArray = new JSONArray(jsonData);

            // Step 3: Iterate through the JSONArray and create JSONObjects directly
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                boolean exempt = jsonObject.getBoolean("exempt");
                double compensation = jsonObject.getDouble("compensation");
                String dob = jsonObject.getString("dob");

                // Create the final JSON object in the required format
                JSONObject entityJson = new JSONObject();
                entityJson.put("id", id);
                entityJson.put("name", name);
                entityJson.put("exempt", exempt);
                entityJson.put("compensation", compensation);
                entityJson.put("dob", dob);

                JSONObject finalJson = new JSONObject();
                finalJson.put("entity", entityJson);

                String value = finalJson.toString();
                String key = "id_" + id;

                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

                // Sending data to the cluster
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // flush data - synchronous
        producer.flush();
        // flush and close producer
        producer.close();
    }
}

