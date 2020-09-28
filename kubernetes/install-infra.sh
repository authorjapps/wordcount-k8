#!/bin/sh

# Create namespaces
kubectl create ns kafka-ns
kubectl create ns wordapp-ns

# Install Kafka + Zookeeper
# kubectl create -f zookeeper.yml -n kafka-ns
# kubectl create -f kafka.yml  -n kafka-ns
kubectl create -f zookeeper_o.yml -n kafka-ns
kubectl create -f kafka_o.yml  -n kafka-ns

# Deploy application pod and service
kubectl create -f app-service.yml -n wordapp-ns

# Post installation steps
kubectl get pods -n kafka-ns -o wide
kubectl get services -n kafka-ns -o wide
kubectl get pods -n app-ns -o wide
kubectl get services -n app-ns -o wide