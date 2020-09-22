package com.co4gsl.wordcountk8;

public class WordCountApplication {
    public static void main(String[] args) throws InterruptedException {
        if (args[0].equals("producer")) {
            Producer.main(args);
        } else if (args[0].equals("consumer")) {
            Consumer.main(args);
        } else {
            throw new IllegalArgumentException("Don't know what to do " + args[0]);
        }
    }
}
