package org.PS1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringSerializer;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
public class Prod_Inventory
{
    private static final Logger log = LoggerFactory.getLogger(Prod_Inventory.class);

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


        // send data - asynchronous
        String topic = "Inventory";
        // Gson is used here to convert a Java object of type Entity into a JSON object
        try
        {
            FileReader fr=new FileReader("src/main/java/org/PS1/inventory_data.json");
            Gson gson = new Gson();
            Type inventoryListType = new TypeToken<List<Inventory>>() {}.getType();
            List<Inventory> inv = gson.fromJson(fr, inventoryListType);
            for(Inventory i:inv)
            {
                String key = "id_" + i.getItemID();
                Entity_Inv entity=new Entity_Inv(i);
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
