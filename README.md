# wordcount-k8
Realtime Word Count Using Kubernetes, Kafka and EFK stack

Bringing Up Kafka
===
+ Change dir to `wordcount-k8/docker/kafka`
+ Issue command `docker-compose up`

+ Create a topic called `sentence` like below:
+ Confirm that topic exists by listing the topic
```shell script
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

CLI Command Help
===
+ https://kafka.apache.org/quickstart
