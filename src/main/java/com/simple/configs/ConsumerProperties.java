package com.simple.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.LocalDateTime;
import java.util.Properties;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class ConsumerProperties {
    public static String KAFKA_HOST_DEFAULT = "localhost:9092";
    public static final String KAFKA_HOST = "KAFKA_HOST";
    public static final String KAFKA_PORT = "KAFKA_PORT";

    public static Properties consumerProperties() {
        System.out.println("++++++++ Kafka Host +++++++++ => " + getKafkaHost());
        String groupId = "word-count-app-01-" + LocalDateTime.now();
        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaHost());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public static String getKafkaHost() {
        String host = getEnvValueString(KAFKA_HOST);
        String port = getEnvValueString(KAFKA_PORT);

        if(host != null && port != null){
            KAFKA_HOST_DEFAULT = String.format("%s:%s", host, port);
        }
        return KAFKA_HOST_DEFAULT;
    }

    public static String getEnvValueString(String envKey) {
        return getProperty(envKey) == null ? getenv(envKey) : getProperty(envKey);
    }

}
