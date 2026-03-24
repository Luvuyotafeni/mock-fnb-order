#!/bin/bash

NAMESPACE="java-microservices"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║         Tearing down Java Microservices                  ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

echo "  Removing microservices..."
kubectl delete -f "${SCRIPT_DIR}/k8s/05-user-service.yaml"      --ignore-not-found=true
kubectl delete -f "${SCRIPT_DIR}/k8s/06-order-service.yaml"     --ignore-not-found=true
kubectl delete -f "${SCRIPT_DIR}/k8s/07-inventory-service.yaml" --ignore-not-found=true
kubectl delete -f "${SCRIPT_DIR}/k8s/08-payment-service.yaml"   --ignore-not-found=true

echo "  Removing Kafka..."
kubectl delete -f "${SCRIPT_DIR}/k8s/03-kafka.yaml" --ignore-not-found=true

echo "  Removing Zookeeper..."
kubectl delete -f "${SCRIPT_DIR}/k8s/02-zookeeper.yaml" --ignore-not-found=true

echo "  Removing MySQL..."
kubectl delete -f "${SCRIPT_DIR}/k8s/01-mysql.yaml" --ignore-not-found=true

echo "  Removing namespace..."
kubectl delete namespace ${NAMESPACE} --ignore-not-found=true

echo ""
echo "  ✓ Everything removed"
echo ""
