package org.PS1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.common.serialization.StringSerializer;

import java.lang.reflect.Type;
import java.util.List;

// This class serves as a Kafka producer for Inventory type data.
// It reads data from the inventory_data.json file and sends it to the Kafka cluster.

public class Producer_Inventory
{
    private static final Logger log = LoggerFactory.getLogger(Producer_Inventory.class);

    public static void main(String[] args)
    {
        log.info("I am a Kafka Producer");
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        Scanner sc=new Scanner(System.in);
        // This is done to prevent overlap of id's
        System.out.println("Enter the first id not yet posted");
        int startID=sc.nextInt();
        // send data - asynchronous
        String topic = "Inventory";
        // Gson is used here to convert a Java object of type Entity into a JSON object
        try
        {
            FileReader fr=new FileReader("src/main/java/org/PS1/inventory_data.json");
            Gson gson = new Gson();
            Type inventoryListType = new TypeToken<List<Inventory>>() {}.getType();
            List<Inventory> inv = gson.fromJson(fr, inventoryListType);
            // Iterating through each Inventory object read from the JSON file
            for(Inventory i:inv)
            {
                if(i.getItemID()>=startID) // Done to prevent resending already sent data to the Kafka cluster
                {
                    String key = "id_" + i.getItemID();
                    Entity_Inventory entity = new Entity_Inventory(i);
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
