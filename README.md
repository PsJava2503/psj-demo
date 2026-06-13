# Commerce Demo

A Spring Boot microservice demo for a commerce domain. The project includes user authentication, RBAC, product and address persistence, order creation, inventory reservation, payment, notification, fulfillment, and gateway services.

## Modules

| Module | Description |
| --- | --- |
| `commerce-contracts` | Shared DTOs, security constants, headers, and messaging contracts. |
| `commerce-gateway` | Spring Cloud Gateway entry point, token validation, permission checks, and context propagation. |
| `commerce-user-service` | User profile, credentials, registration, login, RBAC roles and permissions. |
| `commerce-order-service` | Order creation, status transitions, order outbox, inventory reservation orchestration, payment event handling, and fulfillment APIs. |
| `commerce-product-service` | Product CRUD, price lookup, and SKU mapping APIs. |
| `commerce-inventory-service` | Inventory APIs, stock reservation, release, and confirmation. |
| `commerce-payment-service` | Payment APIs. |
| `commerce-notification-service` | Notification APIs, idempotent notification records, and message handling. |
| `commerce-address-service` | Address CRUD APIs and default address support. |

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Nacos service discovery
- PostgreSQL
- Redis
- RabbitMQ
- Flyway
- MyBatis Dynamic SQL
- Sa-Token
- Maven Wrapper
- Docker Compose

## Local Requirements

Install or start the following before running the project:

- JDK 17
- Docker Desktop or Docker Engine
- Docker Compose plugin

The repository uses Maven Wrapper, so a local Maven installation is not required.

## Quick Start

Build all runnable jars first:

```bash
./mvnw -DskipTests package
```

Start the full local `dev` environment:

```bash
bash scripts/deploy/up-all.sh dev
```

Stop the full local `dev` environment:

```bash
bash scripts/deploy/down-all.sh dev
```

The startup script launches infrastructure and all business services. It also creates per-service PostgreSQL containers through each module's Docker Compose file.

## Infrastructure

The shared infrastructure stack is defined in `deploy/infra/docker-compose.yml`.

Default `dev` ports:

| Component | Port |
| --- | --- |
| Nacos | `8848` |
| Redis | `6379` |
| RabbitMQ | `5672` |
| RabbitMQ Management | `15672` |

The `up-all.sh` script also starts module-level databases. In `dev`, database ports start from `15431`.

## Service Ports

Default `dev` application ports:

| Service | Port |
| --- | --- |
| Gateway | `8080` |
| Order | `8081` |
| Inventory | `8082` |
| User | `8083` |
| Product | `8084` |
| Payment | `8085` |
| Notification | `8086` |
| Address | `8087` |

Other environments use an offset:

- `test`: app ports add `10000`
- `prod`: app ports add `20000`

Examples:

```bash
bash scripts/deploy/up-all.sh test
bash scripts/deploy/down-all.sh test
```

## Commerce Flow

The core order flow is eventually consistent. The order service performs the local order transaction, reserves stock synchronously, then uses outbox events to drive downstream side effects.

Normal flow:

```text
Create order
  -> load user, product, and address
  -> persist order, order item, and status log
  -> reserve inventory by skuId
  -> persist reservation ids
  -> move order to WAIT_PAY
  -> pre-create payment
  -> write ORDER_WAIT_PAY and notification outbox events
  -> payment-service emits payment.paid through its outbox
  -> order-service marks order PAID
  -> order outbox confirms inventory and sends notification
  -> order moves to WAIT_SHIP
  -> ship, receive, complete through fulfillment APIs
```

Main order states:

```text
CREATED
-> STOCK_RESERVED
-> WAIT_PAY
-> PAID
-> INVENTORY_CONFIRMING
-> WAIT_SHIP
-> SHIPPED
-> RECEIVED
-> COMPLETED
```

Failure and compensation states:

```text
WAIT_PAY -> CANCELLED
WAIT_PAY -> FAILED
PAID -> INVENTORY_CONFIRMING -> INVENTORY_CONFIRM_FAILED
CANCELLED/FAILED + payment.paid -> REFUND_REQUIRED -> REFUNDED
```

Inventory semantics:

- Order creation reserves stock: available quantity is moved to locked quantity.
- Payment success confirms stock: locked quantity is consumed through outbound inventory.
- Cancelled or failed orders release stock through order outbox events.

Outbox semantics:

- `payment-service` uses payment outbox to publish `payment.paid`.
- `order-service` uses order outbox for `ORDER_WAIT_PAY`, inventory confirm/release, notification requests, and refund requests.
- Order outbox events are claimed before processing, so multiple service instances do not process the same event at the same time.
- Order status updates use optimistic locking through `orders.version`.

## Product, Address, and Order APIs

Product APIs:

```text
POST   /api/products
PUT    /api/products/{productId}
DELETE /api/products/{productId}
GET    /api/products
GET    /api/products/{productId}
GET    /api/products/{productId}/price
```

Address APIs:

```text
POST   /api/addresses
PUT    /api/addresses/{addressId}
DELETE /api/addresses/{addressId}
GET    /api/addresses
GET    /api/addresses/{addressId}
GET    /api/addresses/{userId}/default
```

Create an order:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H 'satoken: <token>' \
  -d '{
    "userId": 1,
    "productId": 1,
    "addressId": 1,
    "quantity": 2
  }'
```

Order APIs:

```text
POST   /api/orders
GET    /api/orders
GET    /api/orders/{orderId}
DELETE /api/orders/{orderId}
POST   /api/orders/{orderId}/ship
POST   /api/orders/{orderId}/receive
POST   /api/orders/{orderId}/complete
```

Payment and refund notes:

- Payment pre-create is called by the order service after stock is reserved.
- If payment pre-create fails, the order becomes `FAILED` and inventory release is requested through order outbox.
- If `payment.paid` is received for a `CANCELLED` or `FAILED` order, the order becomes `REFUND_REQUIRED`; order outbox calls payment refund and then moves the order to `REFUNDED`.

## Run One Service Locally

You can run a single module from the IDE or terminal after starting its dependencies.

Example:

```bash
./mvnw -pl commerce-user-service -am spring-boot:run
```

For local non-Docker runs, services expect the default local dependency addresses:

- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Nacos: `localhost:8848`

## Authentication Flow

The user service exposes authentication APIs under `/api/auth`.

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Demo",
    "secondName": "User",
    "phone": "13800009999",
    "email": "demo.user@example.com",
    "username": "demo_user",
    "password": "123456"
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"customer","password":"123456"}'
```

The response contains a Sa-Token token. Send it in later requests with the `satoken` header:

```bash
curl http://localhost:8080/api/users/1 \
  -H 'satoken: <token>'
```

Login state is stored by Sa-Token in Redis. The token session stores:

- `userId`
- `username`
- `roles`
- `permissions`

The gateway validates the token, checks permissions, and forwards user context to downstream services with:

- `X-User-Id`
- `X-Username`
- `X-Roles`
- `X-Permissions`

## RBAC

RBAC data is managed by the user service:

- `roles`
- `permissions`
- `user_roles`
- `role_permissions`
- `user_credentials`

Management APIs are exposed under:

- `/api/rbac/roles`
- `/api/rbac/permissions`
- `/api/rbac/user-roles`
- `/api/rbac/role-permissions`

Changing user roles, role permissions, roles, permissions, passwords, or login status logs out affected users so their next login reloads fresh permissions.

## Common Commands

Compile all modules:

```bash
./mvnw -DskipTests compile
```

Run tests:

```bash
./mvnw test
```

Package all modules:

```bash
./mvnw -DskipTests package
```

Apply formatting:

```bash
./mvnw spotless:apply
```

Install Git hooks:

```bash
bash scripts/install-git-hooks.sh
```

## Notes

- Flyway migrations run automatically when services start.
- Product, address, order, payment, inventory, and notification data are persisted with PostgreSQL and MyBatis Dynamic SQL.
- Order IDs are internal numeric identifiers. `order_no` is the external business order number.
- Orders store product and address snapshots so historical orders are not affected by later product price or address changes.
- Order state changes are guarded by optimistic locking with the `orders.version` column.
- Gateway routes `/api/auth/**`, `/api/users/**`, and `/api/rbac/**` to `commerce-user-service`.
- `/api/auth/login` and `/api/auth/register` are public. Other `/api/**` requests require a valid `satoken` header.
- Current gateway permission checks are path-based. Service-level method authorization can be added later with annotations if finer authorization is needed.
- JDK 17 must be available through a valid `JAVA_HOME` before running Maven Wrapper commands.
