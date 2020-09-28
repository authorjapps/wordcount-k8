#!/bin/sh

# Uninstall Kafka


# Delete namespaces for applications
kubectl delete ns wordapp-ns
kubectl delete ns kafka-ns