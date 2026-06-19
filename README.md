# Order Saga Demo — Choreography-based Saga with Kafka

## Services & Ports

| Service           | Port |
|-------------------|------|
| order-service     | 8081 |
| inventory-service | 8082 |
| payment-service   | 8083 |
| shipping-service  | 8084 |

## Saga Flow (Happy Path)

```
POST /orders  →  order-service
    → publishes [order-created]
        → inventory-service reserves stock
            → publishes [inventory-reserved]
                → payment-service charges card
                    → publishes [payment-charged]
                        → shipping-service ships order
                            → publishes [order-shipped]
```

## Compensating Flows (Sad Paths)

- **Inventory fails** → publishes [inventory-failed] → order-service cancels order
- **Payment fails**  → publishes [payment-failed]  → inventory-service releases stock → order-service cancels order
- **Shipping fails** → publishes [ship-failed]     → payment-service refunds → inventory-service releases stock → order-service cancels order

## Kafka Topics

| Topic                | Producer           | Consumers                                 |
|----------------------|--------------------|-------------------------------------------|
| order-created        | order-service      | inventory-service                         |
| inventory-reserved   | inventory-service  | payment-service, order-service            |
| inventory-failed     | inventory-service  | order-service                             |
| payment-charged      | payment-service    | shipping-service, order-service           |
| payment-failed       | payment-service    | inventory-service, order-service          |
| order-shipped        | shipping-service   | order-service                             |
| ship-failed          | shipping-service   | payment-service, inventory-service, order-service |
| order-cancelled      | order-service      | (for downstream consumers if needed)      |

## Running

```bash
# Start Kafka
docker-compose up -d

# Run each service in separate terminals
cd order-service     && ./gradlew bootRun
cd inventory-service && ./gradlew bootRun
cd payment-service   && ./gradlew bootRun
cd shipping-service  && ./gradlew bootRun
```

## Testing

```bash
# Happy path — laptop is in stock (10 units)
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"item":"laptop","quantity":2}'

# Inventory failure — tablet has 0 stock
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"item":"tablet","quantity":1}'

# Simulate payment failure
curl -X POST http://localhost:8083/payment/fail/true
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"item":"laptop","quantity":1}'
curl -X POST http://localhost:8083/payment/fail/false

# Simulate shipping failure
curl -X POST http://localhost:8084/shipping/fail/true
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"item":"laptop","quantity":1}'
curl -X POST http://localhost:8084/shipping/fail/false

# Check order state
curl http://localhost:8081/orders
```
