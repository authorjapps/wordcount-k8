package com.simple.configs;


import org.junit.jupiter.api.Test;

import static com.simple.configs.ConsumerProperties.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ConsumerPropertiesTest {

    @Test
    public void test_envProps() {
        System.setProperty(KAFKA_HOST, "123.0.0.1");
        System.setProperty(KAFKA_PORT, "1234");
        String kafkaHostEnv = getKafkaHost();
        assertThat(kafkaHostEnv, is("123.0.0.1:1234"));
    }

    @Test
    public void test_envProps_k8() {
        System.setProperty(KAFKA_HOST, "kafka-service-k8");
        System.setProperty(KAFKA_PORT, "1010");
        String kafkaHostEnv = getKafkaHost();
        assertThat(kafkaHostEnv, is("kafka-service-k8:1010"));
    }

    @Test
    public void envProps_default() {
        String kafkaHostEnv = getKafkaHost();
        assertThat(kafkaHostEnv, is("localhost:9092"));
    }
}