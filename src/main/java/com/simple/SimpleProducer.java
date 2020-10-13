package com.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

import static com.simple.configs.ProducerProperties.DEFAULT_MSG;
import static com.simple.configs.ProducerProperties.producerProperties;

/**
 * Produces to: counts
 */
public class SimpleProducer {
    static Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class.getName());
    public static String WORDS_TOPIC = "words";

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("No arguments found. Use bellow syntax: \n java SimpleProducer <topic> <message>");
            LOGGER.info("\n=> Sending record to Default topic topic:{}, message:{}", WORDS_TOPIC, DEFAULT_MSG);
            produceDefault();
        } else if (args[0] != null && args[1] != null) {
            produce(args[0], args[1]);
        } else {
            throw new IllegalArgumentException("Don't know what to do " + args[0] + " - " + args[1]);
        }
    }

    public static void produce(String topic, String message) {
        Properties properties = producerProperties();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);
        LOGGER.info("\n=> Sending record to topic:{}, message:{}", topic, message);
        Future<RecordMetadata> producedDetails = producer.send(record);
        LOGGER.info("\n=> Produced status: " + producedDetails);
        producer.flush();
        producer.close();
    }

    private static void produceDefault() {
        produce(WORDS_TOPIC, DEFAULT_MSG);
    }

}
