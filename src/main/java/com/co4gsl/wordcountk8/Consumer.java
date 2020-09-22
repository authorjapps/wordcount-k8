package com.co4gsl.wordcountk8;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Enter topic name, example - words");
            return;
        }
        //Kafka consumer configuration settings
        String topicName = args[0].toString();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        KafkaConsumer consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topicName));
        System.out.println("Subscribed to topic " + topicName);

        int counter = 0;
        while (counter <= 1000) {
            ConsumerRecords<String, String> recs = consumer.poll(10);
            if (recs.count() == 0) {
            } else {
                for (ConsumerRecord<String, String> rec : recs) {
                    System.out.printf("Received %s: %s", rec.key(), rec.value());
                }
            }
            counter++;
        }
    }
}
