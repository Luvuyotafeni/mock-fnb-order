#!/bin/bash
set -euo pipefail

NAMESPACE="java-microservices"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

USER_SVC_DIR="${SCRIPT_DIR}/orderUsermanagementService"
ORDER_SVC_DIR="${SCRIPT_DIR}/orderManagementService"
INVENTORY_SVC_DIR="${SCRIPT_DIR}/orderInventoryManagementService"
PAYMENT_SVC_DIR="${SCRIPT_DIR}/paymentService"

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║     Java Microservices — Clean K8s Deployment           ║"
echo "║     Runtime: containerd (nerdctl)                       ║"
echo "╚══════════════════════════════════════════════════════════╝"

# ── CHECK TOOLS ─────────────────────────────────────────────
echo ""
echo "[ CHECK ] Verifying tools..."

command -v nerdctl >/dev/null 2>&1 || { echo "✗ nerdctl not found"; exit 1; }
command -v kubectl  >/dev/null 2>&1 || { echo "✗ kubectl not found"; exit 1; }

echo "✓ nerdctl found"
echo "✓ kubectl found"

# ── CHECK SERVICE FOLDERS ───────────────────────────────────
echo ""
echo "[ CHECK ] Verifying service folders..."

for dir in "$USER_SVC_DIR" "$ORDER_SVC_DIR" "$INVENTORY_SVC_DIR" "$PAYMENT_SVC_DIR"; do
  if [ ! -d "$dir" ]; then
    echo "✗ Missing folder: $dir"
    exit 1
  fi

  if [ ! -f "$dir/Dockerfile" ]; then
    echo "✗ Missing Dockerfile in: $dir"
    exit 1
  fi

  echo "✓ $(basename "$dir")"
done

# ── STEP 1: BUILD IMAGES ────────────────────────────────────
echo ""
echo "[ STEP 1 ] Building images with nerdctl..."

nerdctl --namespace k8s.io build -t java-user-service:latest "$USER_SVC_DIR"
echo "✓ user-service built"

nerdctl --namespace k8s.io build -t java-order-service:latest "$ORDER_SVC_DIR"
echo "✓ order-service built"

nerdctl --namespace k8s.io build -t java-inventory-service:latest "$INVENTORY_SVC_DIR"
echo "✓ inventory-service built"

nerdctl --namespace k8s.io build -t java-payment-service:latest "$PAYMENT_SVC_DIR"
echo "✓ payment-service built"

# ── STEP 2: APPLY K8S YAML ──────────────────────────────────
echo ""
echo "[ STEP 2 ] Applying k8s.yaml..."

kubectl apply -f "${SCRIPT_DIR}/k8s.yaml"

echo "✓ Resources applied"

# ── STEP 3: WAIT FOR MYSQL ──────────────────────────────────
echo ""
echo "[ STEP 3 ] Waiting for MySQL to be ready..."

kubectl rollout status deployment/mysql -n ${NAMESPACE} --timeout=180s

echo "✓ MySQL is ready"

# ── STEP 4: WAIT FOR MICROSERVICES ──────────────────────────
echo ""
echo "[ STEP 4 ] Waiting for microservices..."

kubectl rollout status deployment/user-service      -n ${NAMESPACE} --timeout=180s
kubectl rollout status deployment/order-service     -n ${NAMESPACE} --timeout=180s
kubectl rollout status deployment/inventory-service -n ${NAMESPACE} --timeout=180s
kubectl rollout status deployment/payment-service   -n ${NAMESPACE} --timeout=180s

echo "✓ All services are running"

# ── DONE ────────────────────────────────────────────────────
echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║          ✅ DEPLOYMENT SUCCESSFUL                        ║"
echo "╠══════════════════════════════════════════════════════════╣"
echo "║                                                          ║"
echo "║  USER SERVICE      → http://localhost:30001              ║"
echo "║  ORDER SERVICE     → http://localhost:30002              ║"
echo "║  INVENTORY SERVICE → http://localhost:30003              ║"
echo "║  PAYMENT SERVICE   → http://localhost:30004              ║"
echo "║                                                          ║"
echo "╠══════════════════════════════════════════════════════════╣"
echo "║  Useful Commands                                         ║"
echo "║                                                          ║"
echo "║  kubectl get pods -n ${NAMESPACE}                        ║"
echo "║  kubectl logs <pod> -n ${NAMESPACE}                      ║"
echo "║  ./teardown.sh                                           ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo "╚══════════════════════════════════════════════════════════╝"