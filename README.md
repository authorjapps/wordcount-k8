# wordcount-k8
Realtime Word Count Using Kubernetes, Kafka and EFK stack

Bringing Up Kafka
===
+ Change dir to `wordcount-k8/docker/kafka`
+ Issue command `docker-compose up`

+ Create a topic called `sentence` like below:
+ Confirm that topic exists by listing the topic
```shell script
$docker exec -it kafka_kafka_1 bash

root@62bcdce9da55:/bin# 
kafka-topics --create --topic quickstart-events --bootstrap-server localhost:9092
kafka-topics --describe --topic quickstart-events --bootstrap-server localhost:9092
```

+ Write somethings to the topic
```shell script
root@62bcdce9da55:/bin# kafka-console-producer --topic quickstart-events --bootstrap-server localhost:9092
>A quick brown fox
>another brown fox
>^C
>root@62bcdce9da55:/bin#
```
+ Read from the topic
```shell script
root@62bcdce9da55:/bin# 
kafka-console-consumer --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```shell script
A quick brown fox
another brown fox
```

More
===

+ List all the topics
```shell script
root@62bcdce9da55:/bin# 
kafka-topics --list --bootstrap-server localhost:9092

output:
------
__confluent.support.metrics
__consumer_offsets
demo-c1
quickstart-events
root@62bcdce9da55:/bin# 
```
+ Create Topic With More Options
```shell script
> bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-plaintext-input

or

> bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-wordcount-output \
    --config cleanup.policy=compact
Created topic "streams-wordcount-output".

Refeerence: https://kafka.apache.org/25/documentation/streams/quickstart

```

CLI Commands Quick Start
===
+ https://kafka.apache.org/quickstart
+ Streams: https://kafka.apache.org/25/documentation/streams/quickstart
+ Code: https://github.com/apache/kafka/blob/2.5/streams/examples/src/main/java/org/apache/kafka/streams/examples/wordcount/WordCountDemo.java

