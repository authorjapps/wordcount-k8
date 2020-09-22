package com.co4gsl.wordcountk8.kafka.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public class WordCountProducer {

    private final Producer<String, String> producer;

    public WordCountProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public Future<RecordMetadata> send(String strToFindWordCount, String topicName) {
        Future<RecordMetadata> sendFuture = producer.send(new ProducerRecord<String, String>(topicName, strToFindWordCount));
        System.out.println("Message sent successfully");
        return sendFuture;
    }

    public void flush() {
        producer.flush();
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
}
