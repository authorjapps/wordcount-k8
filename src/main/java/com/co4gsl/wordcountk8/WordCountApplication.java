package com.co4gsl.wordcountk8;

import com.co4gsl.wordcountk8.kafka.consumer.WordCountConsumer;
import com.co4gsl.wordcountk8.kafka.producer.WordCountProducer;
import com.simple.SimpleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class WordCountApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class.getName());
    public static final String STRING_TO_PROCESS = "Count the words and return the result";

    public static void main(String[] args) {
        if(args.length == 0) {
            LOGGER.info("No arguments found. Use bellow syntax;");
            LOGGER.info("java WordCountApplication <producer/consumer> <topic name>");
            return;
        } else if (args[0].equals("producer")) {
            WordCountProducer producer = new WordCountProducer();
            producer.send(STRING_TO_PROCESS, args[1].toString());
        } else if (args[0].equals("consumer")) {
            final Throwable[] pollException = new Throwable[1];
            WordCountConsumer consumer = new WordCountConsumer(ex -> pollException[0] = ex, new ArrayList<>()::add);
            consumer.startBySubscribing(args[1].toString());
        } else {
            throw new IllegalArgumentException("Don't know what to do " + args[0]);
        }
    }
}
