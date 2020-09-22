package com.co4gsl.wordcountk8;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class Producer {
    public static final String STRING_TO_PROCESS = "Count the words and return the result";

    public static void main(String[] args) throws InterruptedException {
        // Check arguments length value
        if(args.length == 0){
            System.out.println("Enter topic name, example - words");
            return;
        }
        //Kafka producer configuration settings
        String topicName = args[0].toString();
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<String, String>(topicName, STRING_TO_PROCESS));
            Thread.sleep(1L);
        }
        System.out.println("Message sent successfully");
        producer.close();
    }
}
