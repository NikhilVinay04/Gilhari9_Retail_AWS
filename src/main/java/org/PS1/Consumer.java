package org.PS1;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    private static final String BASE_URL = "http://localhost:80/gilhari/v1";

    public static void main(String[] args) {
        log.info("I am a Kafka Consumer");

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "Group1";
        String topic = "tpcs3";

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
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());

                    String rec = record.value();
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(rec))
                                .uri(URI.create("http://localhost:80/gilhari/v1/Employee/"))
                                .header("Content-Type", "application/json")
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
