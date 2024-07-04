package org.PS1;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

// Reads data stored in the Kafka cluster from the topics "Employee", "Inventory", "Sales" and "Shipment".
// Data is then posted onto a local PostgreSQL database using the Gilhari microservice framework's REST API(namely a POST request).
public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    private static final Gson gson = new Gson();
    public static void main(String[] args) {
        log.info("I am a Kafka Consumer");

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "Group2";

        HttpClient client = HttpClient.newHttpClient();

        // Create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // Setup shutdown hook
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown, calling consumer.wakeup()...");
            consumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                log.error("Error while joining main thread: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }));

        try {
            //Listening to messages from the 4 topics
            consumer.subscribe(Arrays.asList("Employees", "Sales", "Inventory","Shipment"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());


                    try {
                        HttpResponse<String> response = null;
                        // POSTing the data to the correct table based on the topic
                        switch (record.topic()) {
                            case "Employees":
                                try {
                                    HttpRequest request = HttpRequest.newBuilder()
                                            .POST(HttpRequest.BodyPublishers.ofString(record.value()))
                                            .uri(URI.create("http://localhost:80/gilhari/v1/Employee/"))
                                            .header("Content-Type", "application/json")
                                            .build();

                                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                    int statusCode = response.statusCode();
                                    // HTTP status codes >=200 and <300 indicate success when creating the request.
                                    if (statusCode >= 200 && statusCode < 300) {
                                        log.info("Response from API: " + response.body());
                                    } else {
                                        log.error("HTTP error code: " + statusCode);
                                    }
                                } catch (Exception e) {
                                    log.error("Exception occurred while sending HTTP request: {}", e.getMessage());
                                }
                                break;

                            case "Sales":
                                try {

                                    HttpRequest request = HttpRequest.newBuilder()
                                            .POST(HttpRequest.BodyPublishers.ofString(record.value()))
                                            .uri(URI.create("http://localhost:80/gilhari/v1/Sales/"))
                                            .header("Content-Type", "application/json")
                                            .build();

                                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                    int statusCode = response.statusCode();
                                    // HTTP status codes >=200 and <300 indicate success when creating the request.
                                    if (statusCode >= 200 && statusCode < 300) {
                                        log.info("Response from API: " + response.body());
                                    } else {
                                        log.error("HTTP error code: " + statusCode);
                                    }

                                } catch (Exception e) {
                                    log.error("Exception occurred while sending HTTP request: {}", e.getMessage());
                                }

                                break;


                            case "Inventory":
                                try {
                                    HttpRequest request = HttpRequest.newBuilder()
                                            .POST(HttpRequest.BodyPublishers.ofString(record.value()))
                                            .uri(URI.create("http://localhost:80/gilhari/v1/Inventory/"))
                                            .header("Content-Type", "application/json")
                                            .build();

                                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                    int statusCode = response.statusCode();
                                    // HTTP status codes >=200 and <300 indicate success when creating the request.
                                    if (statusCode >= 200 && statusCode < 300) {
                                        log.info("Response from API: " + response.body());
                                    } else {
                                        log.error("HTTP error code: " + statusCode);
                                    }

                                } catch (Exception e) {
                                    log.error("Exception occurred while sending HTTP request: {}", e.getMessage());
                                }
                                break;

                            case "Shipment":
                                try {

                                    HttpRequest request = HttpRequest.newBuilder()
                                            .POST(HttpRequest.BodyPublishers.ofString(record.value()))
                                            .uri(URI.create("http://localhost:80/gilhari/v1/Shipment/"))
                                            .header("Content-Type", "application/json")
                                            .build();

                                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                    int statusCode = response.statusCode();
                                    // HTTP status codes >=200 and <300 indicate success when creating the request.
                                    if (statusCode >= 200 && statusCode < 300) {
                                        log.info("Response from API: " + response.body());
                                    } else {
                                        log.error("HTTP error code: " + statusCode);
                                    }

                                } catch (Exception e) {
                                    log.error("Exception occurred while sending HTTP request: {}", e.getMessage());
                                }

                                break;
                        }
                    } catch (Exception e) {
                        log.error("Exception occurred while processing record: {}", e.getMessage());
                    }
                }
            }
        } catch (WakeupException e) {
            log.info("Wakeup exception triggered.");
        } catch (Exception e) {
            log.error("Exception occurred while consuming records: {}", e.getMessage());
        } finally {
            consumer.close();
            log.info("Consumer closed.");
        }
    }
}
