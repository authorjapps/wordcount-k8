package com.simple;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.simple.SimpleProducer.WORDS_TOPIC;
import static com.simple.SimpleProducer.produce;
import static com.simple.configs.ConsumerProperties.consumerProperties;

/**
 * Consumes from: words
 * Produces to: counts
 */
public class SimpleConsumer {
    static Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class.getName());
    public static final String COUNTS_TOPIC = "counts";

    public static void main(String[] args) {
        String topic = args.length == 0 ? WORDS_TOPIC : args[0];
        int wordCount = consume(topic);
        // Now produce to the next topic e.g. "counts"
        produce(COUNTS_TOPIC, "{\"count\":" + wordCount + "}"); // Put as a JSON : {"count": 5}
    }

    private static int consume(String topic) {
        Properties properties = consumerProperties();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList(WORDS_TOPIC));
        LOGGER.debug("\n=> Consumer subscribed, polling... topic=" + WORDS_TOPIC);

        // poll for new data
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
        for (int i = 0; i < 5; i++) {
            if (records.count() > 0) {
                break;
            }
            records = consumer.poll(Duration.ofMillis(5000));
            LOGGER.info("{}) polling for messages... consumed: {}", i, records.count());
        }

        LOGGER.info("Record count={}. \n   -----------------------------     ", records.count());
        for (ConsumerRecord<String, String> record : records) {
            LOGGER.info("\n=======> Key: " + record.key() + ", Value: " + record.value());
            LOGGER.debug("Partition: " + record.partition() + ", Offset:" + record.offset());
        }

        return records.count();
    }

}

