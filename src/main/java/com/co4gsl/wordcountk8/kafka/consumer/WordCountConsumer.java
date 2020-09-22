package com.co4gsl.wordcountk8.kafka.consumer;

import com.co4gsl.wordcountk8.entity.WordCount;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WordCountConsumer {

    private Consumer<String, String> consumer;
    private java.util.function.Consumer<Throwable> exceptionConsumer;
    private java.util.function.Consumer<WordCount> wordCountConsumer;

    public WordCountConsumer(
            Consumer<String, String> consumer, java.util.function.Consumer<Throwable> exceptionConsumer,
            java.util.function.Consumer<WordCount> wordCountConsumer) {
        this.consumer = consumer;
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
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                List<String> list = StreamSupport.stream(records.spliterator(), false)
                        .map(rec -> rec.value())
                        .map(str -> str.split("\\s+"))
                        .flatMap(Arrays::stream)
                        .collect(Collectors.toList());

                Map<String, Integer > wordCounter = list.stream()
                        .collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));
                System.out.println("WordCount result - " +wordCounter);

                //.map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));

                consumer.commitSync();
            }
        } catch (WakeupException e) {
            System.out.println("Shutting down...");
        } catch (RuntimeException ex) {
            exceptionConsumer.accept(ex);
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        consumer.wakeup();
    }
}
