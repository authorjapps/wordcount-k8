package com.co4gsl.wordcountk8.kafka.consumer;

import com.co4gsl.wordcountk8.entity.WordCount;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordCountConsumerTest {

    private static final String TOPIC = "words";
    private static final int PARTITION = 0;

    private WordCountConsumer wordCountConsumer;
    private MockConsumer<String, String> consumer;

    private List<WordCount> updates;
    private Throwable pollException;

    @BeforeEach
    void setUp() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        updates = new ArrayList<>();
        wordCountConsumer = new WordCountConsumer(consumer, ex -> this.pollException = ex, updates::add);
    }

    @Test
    void whenStartingByAssigningTopicPartition_thenExpectUpdatesAreConsumedCorrectly() {
        // GIVEN
        consumer.schedulePollTask(() -> consumer.addRecord(record(TOPIC, PARTITION, "key", "check this out")));
        consumer.schedulePollTask(() -> wordCountConsumer.stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        // WHEN
        wordCountConsumer.startByAssigning(TOPIC, PARTITION);

        // THEN
        assertEquals(3, updates.size());
        assertTrue(consumer.closed());
    }

    @Test
    void whenStartingBySubscribingToTopic_thenExpectUpdatesAreConsumedCorrectly() {

        // GIVEN
        consumer.schedulePollTask(() -> {
            consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
            consumer.addRecord(record(TOPIC, PARTITION, "key", "A brown fox jumped over a dog"));
        });
        consumer.schedulePollTask(() -> wordCountConsumer.stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        // WHEN
        wordCountConsumer.startBySubscribing(TOPIC);

        // THEN
        assertEquals(6, updates.size());
        assertEquals(2, updates.get(1).getCount());
        assertTrue(consumer.closed());
    }

    private ConsumerRecord<String, String> record(String topic, int partition, String key, String sentenceValue) {
        return new ConsumerRecord<>(topic, partition, 0, key, sentenceValue);
    }
}