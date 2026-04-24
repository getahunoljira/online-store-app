# Online Shopping App (Spring Boot)

A Spring Boot online shopping application!

- Spring Boot 4.0.5 / Java 17 / Maven
- H2 in-memory database, schema managed by Flyway
- JPA (Hibernate) for persistence
- JUnit 5 + MockMvc for tests
- Jackson with `SNAKE_CASE` for JSON bodies (parity with Python)

## Endpoints

| Method | Path                                                  | Purpose                             |
|--------|-------------------------------------------------------|-------------------------------------|
| POST   | `/customers/`                                         | Register customer + account         |
| GET    | `/customers/`                                         | List customers                      |
| GET    | `/customers/{id}`                                     | Get customer                        |
| PUT    | `/customers/{id}/shipping-address`                    | Update shipping address             |
| DELETE | `/customers/{id}`                                     | Delete customer (cascades)          |
| POST   | `/login`                                              | Username/password login             |
| POST   | `/products/`                                          | Create product (with stock)         |
| GET    | `/products/`                                          | List (filter `?category=`/`?name=`) |
| GET    | `/products/{id}`                                      | Get product                         |
| DELETE | `/products/{id}`                                      | Delete product                      |
| GET    | `/products/{id}/stock`                                | Stock level                         |
| POST   | `/products/{id}/stock`                                | Add stock                           |
| GET    | `/customers/{id}/cart/`                               | Get cart                            |
| POST   | `/customers/{id}/cart/items`                          | Add cart item                       |
| DELETE | `/customers/{id}/cart/items/{productId}`              | Remove cart item                    |
| DELETE | `/customers/{id}/cart/`                               | Clear cart                          |
| POST   | `/customers/{id}/cart/checkout`                       | Checkout → order + payment          |
| GET    | `/customers/{id}/cart/payments`                       | Payment history                     |
| POST   | `/customers/{id}/cart/payments/{paymentId}/refund`    | Refund a payment                    |
| GET    | `/orders/`                                            | List orders (filter `?customer_id=`) |
| GET    | `/orders/{id}`                                        | Get order                           |
| PATCH  | `/orders/{id}/status`                                 | Transition order status             |

Actuator is enabled at `/actuator` (health only by default).

## Design patterns

Mirrors the Python original, class-for-class:

- **Strategy** — `PaymentStrategy` (`CreditCardPaymentStrategy`, `UpiPaymentStrategy`)
- **State** — `OrderState` (`PlacedState`, `ShippedState`, `DeliveredState`, `CancelledState`)
- **Observer** — `OrderObserver` hooks triggered on state transitions
- **Decorator** — `ProductDecorator` / `GiftWrapDecorator`

The payment-gateway abstraction (`PaymentGatewayClient` with `StripeGatewayClient` and
`MockGatewayClient` implementations) lives in `payment/`.

## Running

From the project root:

```bash
./mvnw spring-boot:run
```

The app listens on `http://localhost:8080`. Flyway applies `V1__init.sql` on startup and
Hibernate validates entities against the resulting schema (`spring.jpa.hibernate.ddl-auto=validate`).

The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL:
`jdbc:h2:mem:shoppingdb`, user `sa`, empty password).

### Smoke test

End-to-end flow that exercises every endpoint. Requires `curl` and `jq`. Run with the
app already booted on `:8080`.

```bash
# ---- products ----
PRODUCT_ID=$(curl -sX POST http://localhost:8080/products/ -H 'Content-Type: application/json' \
  -d '{"name":"Shirt","price":19.99,"description":"Cotton","category":"CLOTHING","initial_stock":10}' \
  | jq -r .id)
echo "product: $PRODUCT_ID"

curl -s http://localhost:8080/products/ | jq '.[].name'                    # list all
curl -s "http://localhost:8080/products/?category=CLOTHING" | jq length    # filter by category
curl -s "http://localhost:8080/products/?name=Shirt"        | jq length    # filter by name
curl -s http://localhost:8080/products/$PRODUCT_ID          | jq .name     # get one
curl -s http://localhost:8080/products/$PRODUCT_ID/stock    | jq .stock    # stock level
curl -sX POST http://localhost:8080/products/$PRODUCT_ID/stock -H 'Content-Type: application/json' \
  -d '{"quantity":5}'                                                      # add stock (204)

# ---- customers + auth ----
CUSTOMER_ID=$(curl -sX POST http://localhost:8080/customers/ -H 'Content-Type: application/json' -d '{
  "name":"Ada Lovelace","email":"ada@example.com",
  "username":"ada","password":"hunter2!",
  "shipping_address":{"street":"10 Downing","city":"London","state":"GL","zip_code":"SW1A"}
}' | jq -r .id)
echo "customer: $CUSTOMER_ID"

curl -s http://localhost:8080/customers/              | jq length         # list
curl -s http://localhost:8080/customers/$CUSTOMER_ID  | jq .email         # get one

curl -sX PUT http://localhost:8080/customers/$CUSTOMER_ID/shipping-address -H 'Content-Type: application/json' -d '{
  "shipping_address":{"street":"1 Infinite Loop","city":"Cupertino","state":"CA","zip_code":"95014"}
}' | jq .shipping_address.city

curl -sX POST http://localhost:8080/login -H 'Content-Type: application/json' \
  -d '{"username":"ada","password":"hunter2!"}' | jq .name

# ---- cart ----
curl -sX POST http://localhost:8080/customers/$CUSTOMER_ID/cart/items -H 'Content-Type: application/json' \
  -d "{\"product_id\":\"$PRODUCT_ID\",\"quantity\":2}"                    # add (204)
curl -s  http://localhost:8080/customers/$CUSTOMER_ID/cart/ | jq .items   # view

# ---- checkout (creates order + payment) ----
CHECKOUT=$(curl -sX POST http://localhost:8080/customers/$CUSTOMER_ID/cart/checkout -H 'Content-Type: application/json' -d '{
  "payment_method":"credit_card","currency":"USD",
  "card_number":"4242424242424242","card_expiry_month":12,
  "card_expiry_year":2030,"card_cvv":"123","card_holder_name":"Ada Lovelace"
}')
ORDER_ID=$(echo "$CHECKOUT"   | jq -r .order_id)
PAYMENT_ID=$(echo "$CHECKOUT" | jq -r .payment.payment_id)
echo "order: $ORDER_ID"
echo "payment: $PAYMENT_ID"

# ---- orders ----
curl -s  http://localhost:8080/orders/                                    | jq length
curl -s "http://localhost:8080/orders/?customer_id=$CUSTOMER_ID"          | jq length
curl -s  http://localhost:8080/orders/$ORDER_ID                           | jq .status

curl -sX PATCH http://localhost:8080/orders/$ORDER_ID/status -H 'Content-Type: application/json' \
  -d '{"action":"ship"}'    | jq .status                                  # PLACED → SHIPPED
curl -sX PATCH http://localhost:8080/orders/$ORDER_ID/status -H 'Content-Type: application/json' \
  -d '{"action":"deliver"}' | jq .status                                  # SHIPPED → DELIVERED

# ---- payments history + refund ----
curl -s http://localhost:8080/customers/$CUSTOMER_ID/cart/payments | jq '.[].payment_id'
curl -sX POST http://localhost:8080/customers/$CUSTOMER_ID/cart/payments/$PAYMENT_ID/refund \
  | jq .status                                                            # REFUNDED

# ---- cleanup ----
curl -sX POST http://localhost:8080/customers/$CUSTOMER_ID/cart/items -H 'Content-Type: application/json' \
  -d "{\"product_id\":\"$PRODUCT_ID\",\"quantity\":1}"
curl -sX DELETE http://localhost:8080/customers/$CUSTOMER_ID/cart/items/$PRODUCT_ID   # remove one item (204)
curl -sX DELETE http://localhost:8080/customers/$CUSTOMER_ID/cart/                    # clear cart (204)
curl -sX DELETE http://localhost:8080/products/$PRODUCT_ID                            # delete product (204)
curl -sX DELETE http://localhost:8080/customers/$CUSTOMER_ID                          # delete customer (204)
```

Every `DELETE` and the stock/cart-mutation endpoints return HTTP 204 with no body.

## Tests

```bash
./mvnw test
```

129 tests across controllers, repositories, services, and pattern classes.

## Database migrations

Flyway-managed. Add a new migration as `src/main/resources/db/migration/V{n}__{description}.sql`.
On next boot Flyway applies unseen migrations in order and Hibernate re-validates.

## License

See `LICENSE.txt`.
