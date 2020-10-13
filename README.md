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
kafka-topics --create --topic words --bootstrap-server localhost:9092
kafka-topics --create --topic quickstart-events --bootstrap-server localhost:9092
kafka-topics --describe --topic quickstart-events --bootstrap-server localhost:9092
```

+ Write somethings to the topic
```shell script
kafka-console-producer --topic words --bootstrap-server localhost:9092
root@62bcdce9da55:/bin# kafka-console-producer --topic quickstart-events --bootstrap-server localhost:9092
>A quick brown fox
>another brown fox
>^C
>root@62bcdce9da55:/bin#
```
+ Read from the topic
```shell script
root@62bcdce9da55:/bin# 
kafka-console-consumer --topic words --from-beginning --bootstrap-server localhost:9092
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

Running The Tests
===
+ First bring up Kafka via Docker
  + > docker-compose up
+ Run the word counter main() to listen to "words" topic and write the count to "counts" topic 
  + > Run via IDE => com.simple.SimpleConsumer
+ Run
  + > mvn clean test
  + or 
  + > mvn clean install

Docker Build And Publish
===
```shell script
docker login
docker login -u authorjapps -p <actual password>
docker build -t wordcounter .
docker tag wordcounter authorjapps/wordcounter:latest
docker push authorjapps/wordcounter:latest

e.g.
CLI logs
---------
$ docker login registry.hub.docker.com
Username: authorjapps
Password: 
Login Succeeded
$ docker tag authorjapps/wordcounter:1.0.1 registry.hub.docker.com/authorjapps/wordcounter:1.0.1
$ docker push registry.hub.docker.com/authorjapps/wordcounter:1.0.1                            
The push refers to repository [registry.hub.docker.com/authorjapps/wordcounter]
f09492e2ea8a: Pushed 
ceaf9e1ebef5: Pushed 
9b9b7f3d56a0: Pushed 
f1b5933fe4b5: Pushed 
1.0.1: digest: sha256:9b20d38f771874853cd12fc34c55b16ae1f18fe2de7561bc4365bad14346af4a size: 1158
$ 

Latest:
-------
$ docker tag authorjapps/wordcounter:latest registry.hub.docker.com/authorjapps/wordcounter:latest
$ docker push registry.hub.docker.com/authorjapps/wordcounter:latest                         

```

Docker run
===
```shell script
docker run --rm \
--env KAFKA_HOST=kafka-k8 \
--env KAFKA_PORT=9096 \
authorjapps/wordcounter:latest
```

CLI Commands Quick Start
===
+ https://kafka.apache.org/quickstart
+ Streams: https://kafka.apache.org/25/documentation/streams/quickstart
+ Code: https://github.com/apache/kafka/blob/2.5/streams/examples/src/main/java/org/apache/kafka/streams/examples/wordcount/WordCountDemo.java


```
Create topic using zookeeper
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Topic-Name

Starting producer
kafka-console-producer.sh --topic kafka-on-kubernetes --broker-list localhost:9092 --topic Topic-Name 

Starting consumer using zookeeper (The --from-beginning command lists messages chronologically.)
kafka-console-consumer.sh --topic Topic-Name --from-beginning --zookeeper localhost:2181 
```

Infrastructure as code using Terraform
===
```aidl
Create a container in your Azure storage account
$ az storage container create -n tfstate --account-name k8swctfstate --account-key <Storage account access key>
```
```aidl
Initialize Terraform. Replace the placeholders with appropriate values for your environment.
$ terraform init -backend-config=storage_account_name=k8swctfstate -backend-config=container_name=tfstate -backend-config=access_key=<Storage account access key> -backend-config=key=codelab.microsoft.tfstate
```
```aidl
Create service principal with AKS (https://docs.microsoft.com/en-gb/azure/aks/kubernetes-service-principal)
$ az ad sp create-for-rbac --skip-assignment --name K8RGAKSClusterServicePrincipal

Output
{
  "appId": "yyyyyy",
  "displayName": "K8RGAKSClusterServicePrincipal",
  "name": "http://K8RGAKSClusterServicePrincipal",
  "password": "xxxx",
  "tenant": "05de7198-3db8-4537-9105-640818b4b423"
}
```
```aidl
# Environment variable - Export your service principal credentials
$ export TF_VAR_client_id=<appId> 
$ export TF_VAR_client_secret=<password>

```
```aidl
Command to create the Terraform plan that defines the infrastructure elements. displays the resources that will be created when you run the terraform apply command.
$ terraform plan -out out.plan
```
```aidl
Command to apply the plan to create the Kubernetes cluster.
$ terraform apply out.plan
```
```aidl
Get the Kubernetes configuration from the Terraform state and store it in a file that kubectl can read.
$ echo "$(terraform output kube_config)" > ./azurek8s
```
```aidl
Set an environment variable so that kubectl picks up the correct config.
$ export KUBECONFIG=./azurek8s

Verify the health of the cluster.
$ kubectl get nodes
```

```
Command to delete all the resourses.
$ terraform destroy
```