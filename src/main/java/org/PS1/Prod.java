package org.PS1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringSerializer;
public class Prod
{
    private static final Logger log = LoggerFactory.getLogger(Prod.class);

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
        String topic = "Employees";


        try
        {
            // Reading the contents of the employee_data.json file
            FileReader fr=new FileReader("src/main/java/org/PS1/employee_data.json");
            // Gson is used here to convert a Java object of type Entity into a JSON object
            Gson gson = new Gson();
            Type employeeListType = new TypeToken<List<User>>() {}.getType();
            //Converting the contents of the file read into a List of User Objects which we send to the Kafka Cluster
            List<User> inv = gson.fromJson(fr, employeeListType);
            for(User i:inv)
            {
                String key = "id_" + i.getId();
                Entity entity=new Entity(i);
                String value=gson.toJson(entity);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic,key,value);
                //Sending data to the cluster
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


