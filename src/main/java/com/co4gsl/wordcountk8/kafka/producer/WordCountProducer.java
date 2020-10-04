package com.co4gsl.wordcountk8.kafka.producer;

import com.simple.SimpleProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.simple.configs.ProducerProperties.producerProperties;

public class WordCountProducer {

    static Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class.getName());

    private final Producer<String, String> producer;

    public WordCountProducer() {
        this.producer = getProducer();
    }

    public Future<RecordMetadata> send(String strToFindWordCount, String topicName) {
        LOGGER.info("=> Producing record to topic:{}, message:{}", topicName, strToFindWordCount);
        Future<RecordMetadata> sendFuture = producer.send(new ProducerRecord<String, String>(topicName, strToFindWordCount));
        LOGGER.info("=> Message produced successfully");
        return sendFuture;
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }

    public void beginTransaction() {
        producer.beginTransaction();
    }

    public void initTransaction() {
        producer.initTransactions();
    }

    public void commitTransaction() {
        producer.commitTransaction();
    }

    private static Producer<String, String> getProducer() {
        return new KafkaProducer<String, String>(producerProperties());
    }
}
