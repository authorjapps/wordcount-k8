package com.co4gsl.wordcountk8.kafka.producer;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.*;

class WordCountProducerTest {
    private final String TOPIC_NAME = "words";

    private WordCountProducer wordCountProducer;
    private MockProducer<String, String> mockProducer;

    private void buildMockProducer(boolean autoComplete) {
        this.mockProducer = new MockProducer<>(autoComplete, new StringSerializer(), new StringSerializer());
    }

    @Test
    void givenStringToFind_whenSend_thenVerifyHistory() throws ExecutionException, InterruptedException {

        buildMockProducer(true);

        wordCountProducer = new WordCountProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = wordCountProducer.send("A brown fox jumped over a dog",
                "words");

        assertTrue(mockProducer.history().size() == 1);
        assertTrue(recordMetadataFuture.get().partition() == 0);
    }

    @Test
    void givenStringToFind_whenSend_thenSendOnlyAfterFlush() {

        buildMockProducer(false);
        //when
        wordCountProducer = new WordCountProducer(mockProducer);
        Future<RecordMetadata> record = wordCountProducer.send("A brown fox jumped over a dog",
                "words");
        assertFalse(record.isDone());

        //then
        wordCountProducer.flush();
        assertTrue(record.isDone());
    }

    @Test
    void givenStringToFind_whenSend_thenReturnException() {

        buildMockProducer(false);
        //when
        wordCountProducer = new WordCountProducer(mockProducer);
        Future<RecordMetadata> record = wordCountProducer.send("A brown fox jumped over a dog",
                "words");
        RuntimeException e = new RuntimeException();
        mockProducer.errorNext(e);
        //then
        try {
            record.get();
        } catch (ExecutionException | InterruptedException ex) {
            assertEquals(e, ex.getCause());
        }
        assertTrue(record.isDone());
    }

    @Test
    void givenStringToFind_whenSendWithTxn_thenSendOnlyOnTxnCommit() {

        buildMockProducer(true);
        //when
        wordCountProducer = new WordCountProducer(mockProducer);
        wordCountProducer.initTransaction();
        wordCountProducer.beginTransaction();
        Future<RecordMetadata> record = wordCountProducer.send("A brown fox jumped over a dog", "words");

        //then
        assertTrue(mockProducer.history().isEmpty());
        wordCountProducer.commitTransaction();
        assertTrue(mockProducer.history().size() == 1);
    }

    @Test
    @Disabled
    void givenStringToFind_whenSendWithPartitioning_thenVerifyPartitionNumber() throws ExecutionException, InterruptedException {
        PartitionInfo partitionInfo0 = new PartitionInfo(TOPIC_NAME, 0, null, null, null);
        PartitionInfo partitionInfo1 = new PartitionInfo(TOPIC_NAME, 1, null, null, null);
        List<PartitionInfo> list = new ArrayList<>();
        list.add(partitionInfo0);
        list.add(partitionInfo1);
        Cluster cluster = new Cluster("kafkab", new ArrayList<Node>(), list, emptySet(), emptySet());
        this.mockProducer = new MockProducer<>(cluster, true, new DefaultPartitioner(), new StringSerializer(), new StringSerializer());
        //when
        wordCountProducer = new WordCountProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = wordCountProducer.send("A brown fox jumped over a dog", "words");

        //then
        assertTrue(recordMetadataFuture.get().partition() == 1);
    }
}