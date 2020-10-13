package com.simple.integration;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_test_config.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class WordCountIntegrationTest {

    @Test
    @Scenario("integration/word_count_test.json")
    public void testWordCount() throws Exception {
    }

}
