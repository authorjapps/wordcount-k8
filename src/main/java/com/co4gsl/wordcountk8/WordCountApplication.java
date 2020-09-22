package com.co4gsl.wordcountk8;

import com.co4gsl.wordcountk8.kafka.consumer.WordCountConsumer;
import com.co4gsl.wordcountk8.kafka.producer.WordCountProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;

import java.util.ArrayList;
import java.util.Properties;

public class WordCountApplication {
    public static final String STRING_TO_PROCESS = "Count the words and return the result";

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("No arguments found. Use bellow syntax;");
            System.out.println("java WordCountApplication <producer/consumer> <topic name>");
            return;
        } else if (args[0].equals("producer")) {
            WordCountProducer producer = new WordCountProducer(getProducer());
            producer.send(STRING_TO_PROCESS, args[1].toString());
        } else if (args[0].equals("consumer")) {
            final Throwable[] pollException = new Throwable[1];
            WordCountConsumer consumer = new WordCountConsumer(getConsumer(), ex -> pollException[0] = ex, new ArrayList<>()::add);
            consumer.startBySubscribing(args[1].toString());
        } else {
            throw new IllegalArgumentException("Don't know what to do " + args[0]);
        }
    }

    private static Consumer<String, String> getConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        return new KafkaConsumer(props);
    }

    private static Producer<String, String> getProducer() {
        /* Kafka producer configuration settings */
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>(props);
    }
}
