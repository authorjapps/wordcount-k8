FROM openjdk:8-alpine
ADD target/wordcount-k8-*-jar-with-dependencies.jar /usr/share
ENTRYPOINT java -jar /usr/share/wordcount-k8-*-jar-with-dependencies.jar