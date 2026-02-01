#!/bin/bash
set -e

APP_URL="http://localhost:8080"

echo "Building..."
docker-compose -f docker-compose.yaml build

echo "Start docker compose..."
docker-compose -f docker-compose.yaml up -d --no-build

cleanup() {
  echo -e "\nStopping containers..."
  docker-compose -f docker-compose.yaml down -v
  echo "Bye!"
  exit 0
}

trap cleanup INT TERM

echo "Waiting for the app to be ready..."
until http -b GET "$APP_URL/products" >/dev/null 2>&1; do
    echo -n "."
    sleep 1
done
echo "App is ready!"

echo ""
echo "========== ADD TWO PRODUCTS =========="

http -b POST "$APP_URL/products" \
  name=apple \
  description="red apple" \
  price:=1.2 \
  vatRate:=0.1 | jq .

http -b POST "$APP_URL/products" \
  name=banana \
  description="yellow banana" \
  price:=1.5 \
  vatRate:=0.1 | jq .

echo ""
echo "========== GET ALL PRODUCTS =========="
http -b GET "$APP_URL/products" | jq .

echo ""
echo "========== UPDATE PRODUCT (apple) =========="

http -b PUT "$APP_URL/products/apple" \
  price:=2.0 \
  vatRate:=0.22 \
  description="premium red apple" | jq .

echo ""
echo "========== GET UPDATED PRODUCT =========="
http -b GET "$APP_URL/products/apple" | jq .

echo ""
echo "========== CREATE ORDER WITH BOTH PRODUCTS =========="

ORDER_RESPONSE=$(
  http -b POST "$APP_URL/orders" \
    products:='[{"productName":"apple","quantity":2},{"productName":"banana","quantity":3}]'
)

echo "$ORDER_RESPONSE" | jq .

ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.orderId // .[0].orderId')
echo "ORDER_ID: $ORDER_ID"

echo ""
echo "========== GET ALL ORDERS =========="
http -b GET "$APP_URL/orders" | jq .

echo ""
echo "========== UPDATE ORDER =========="

http -b PUT "$APP_URL/orders/$ORDER_ID" \
  products:='[{"productName":"apple","quantity":5},{"productName":"banana","quantity":1}]' | jq .

echo ""
echo "========== GET UPDATED ORDER =========="
http -b GET "$APP_URL/orders/$ORDER_ID" | jq .

echo ""
echo "========== DELETE A PRODUCT (banana) =========="
http -b DELETE "$APP_URL/products/banana" | jq .

echo ""
echo "========== DELETE ORDER =========="
http -b DELETE "$APP_URL/orders/$ORDER_ID" | jq .

echo ""
echo "==============================================="
echo "OPERATIONS COMPLETED – APP STILL RUNNING"
echo "Press 'q' to stop containers and exit"
echo "==============================================="
echo ""

# final wait for user quit
while true; do
  read -rsn1 key
  if [[ "$key" == "q" || "$key" == "Q" ]]; then
    cleanup
  fi
done
