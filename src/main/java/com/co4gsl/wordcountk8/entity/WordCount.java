package com.co4gsl.wordcountk8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordCount {

    private String word;
    private long count;
    private Date start;
    private Date end;
}

