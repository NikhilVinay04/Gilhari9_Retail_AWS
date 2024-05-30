package org.PS1;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
public class Consumer
{
    private static final Logger log = LoggerFactory.getLogger(Prod.class);
    public static void main(String[] args)
    {
        log.info("I am a Kafka Consumer");
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "Group1";
        String topic = "Names";

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        final Thread th = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown");
            consumer.wakeup();

            // join the main thread to allow the execution of the code in the main thread
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        try
        {
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }
            }
        }
        catch (WakeupException e)
        {
            log.info("Wakeup exception triggered.");
        }
        catch (Exception e)
        {
            log.error("Exception occurred while consuming records: {}", e.getMessage());
        }
        finally
        {
            consumer.close();
            log.info("Consumer closed.");
        }


    }
}
