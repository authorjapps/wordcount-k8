package com.co4gsl.wordcountk8.kafka.consumer;

import com.co4gsl.wordcountk8.entity.WordCount;
import com.co4gsl.wordcountk8.kafka.producer.WordCountProducer;
import com.simple.SimpleConsumer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.simple.SimpleProducer.produce;
import static com.simple.configs.ConsumerProperties.consumerProperties;

public class WordCountConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class.getName());
    public static final String TOPIC_TO_PRODUCE = "counts";

    private Consumer<String, String> consumer;
    private java.util.function.Consumer<Throwable> exceptionConsumer;
    private java.util.function.Consumer<WordCount> wordCountConsumer;

    public WordCountConsumer(
            java.util.function.Consumer<Throwable> exceptionConsumer,
            java.util.function.Consumer<WordCount> wordCountConsumer) {
        this.consumer = getConsumer();
        this.exceptionConsumer = exceptionConsumer;
        this.wordCountConsumer = wordCountConsumer;
    }

    public void startBySubscribing(String topic) {
        System.out.println("Subscribed to topic " + topic);
        consume(() -> consumer.subscribe(Collections.singleton(topic)));
    }

    public void startByAssigning(String topic, int partition) {
        consume(() -> consumer.assign(Collections.singleton(new TopicPartition(topic, partition))));
    }

    private void consume(Runnable beforePollingTask) {
        try {
            beforePollingTask.run();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
                printRecordsConsumed(records);

                List<String> list = StreamSupport.stream(records.spliterator(), false)
                        .map(rec -> rec.value())
                        .map(str -> str.split("\\s+"))
                        .flatMap(Arrays::stream)
                        .collect(Collectors.toList());

                Map<String, Integer > wordCounter = list.stream()
                        .collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));
                LOGGER.info("=> WordCount result - " +wordCounter);
                produce(TOPIC_TO_PRODUCE, "{\"consumerResult\":" + wordCounter + "}");

                StreamSupport.stream(wordCounter.entrySet().spliterator(), false)
                        .map(entry -> new WordCount(entry.getKey(), entry.getValue(), new Date(), new Date()))
                        .forEach(wordCountConsumer);

                consumer.commitSync();
            }
        } catch (WakeupException e) {
            LOGGER.info("=> Shutting down...");
        } catch (RuntimeException ex) {
            exceptionConsumer.accept(ex);
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        consumer.wakeup();
    }

    private static Consumer<String, String> getConsumer() {
        return new KafkaConsumer(consumerProperties());
    }

    private static void printRecordsConsumed(ConsumerRecords<String, String> records) {
        LOGGER.info("=> Consumer record count={}. \n   --------------------------", records.count());
        for (ConsumerRecord<String, String> record : records) {
            LOGGER.info("\n===> Key: " + record.key() + ", Value: " + record.value());
            LOGGER.debug("===> Partition: " + record.partition() + ", Offset:" + record.offset());
        }
    }

    private void produceTo(String topicName, String message) {
        LOGGER.info("\n=> Sending record to topic:{}, message:{}", topicName, message);
        WordCountProducer producer = new WordCountProducer();
        producer.send(message, topicName);
        LOGGER.info("\n=> Consumer successfully produced message to topic {}", topicName);
        producer.flush();
        producer.close();
    }

    public void setConsumer(Consumer<String, String> mockConsumer) {
        consumer = mockConsumer;
    }
}
