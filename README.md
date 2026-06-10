## commerce DDD Skeleton Guide (EN)

### 1. Service Internal Layers

Each business service uses the same internal structure:

```text
com.commerce.<bounded-context>
├── interfaces
│   └── rest
├── application
│   ├── port
│   ├── command (optional)
│   └── service
├── domain
│   ├── model
│   ├── service
│   └── repository
└── infrastructure
    ├── rpc
    └── persistence
```

### 2. Dependency Direction (Must Follow)

- `interfaces -> application`
- `application -> domain + application.port`
- `infrastructure -> application.port / domain.repository (implements interfaces)`
- `domain` does not depend on `interfaces` or `infrastructure`

Do not reverse this direction.

### 3. Layer Responsibilities

- `interfaces`
  - Receives HTTP requests and returns response data.
  - Converts request DTO to application command.
  - No business orchestration.

- `application`
  - Orchestrates a complete use case.
  - Depends on outbound `port` interfaces instead of concrete adapters.
  - Coordinates domain rules and persistence boundaries.

- `domain`
  - Holds core business rules, state transitions, and invariants.
  - Contains entities/value objects/domain services/repository contracts.
  - Does not use HTTP, DB frameworks, or remote-client frameworks directly.

- `infrastructure`
  - Technical adapters only.
  - HTTP clients/controllers, database implementations, external integrations.
  - Implements interfaces defined by `application.port` or `domain.repository`.

### 4. Minimal Template (Per Use Case)

For a new use case, add components in this order:

1. `application.port.<XxxUseCase>`
2. `application.command.<XxxCommand>` (if needed)
3. `domain.model` / `domain.service` rule objects
4. `application.service.<XxxApplicationService>` implements use case
5. `interfaces.rest.<XxxController>` calls use case only
6. `infrastructure.*` adapters implementing ports/repositories

### 5. Example Mapping in Current Project

- `order-service`
  - `OrderController -> OrderUseCase -> OrderApplicationService`
  - `OrderApplicationService` depends on:
    - `UserQueryPort`
    - `ProductQueryPort`
    - `InventoryCommandPort`
    - `PaymentCommandPort`
    - `NotificationCommandPort`
    - `OrderRepository`
  - `infrastructure.rpc.*Client` uses OpenFeign to implement outbound ports
  - `infrastructure.persistence.InMemoryOrderRepository` implements repository

- `inventory-service`, `payment-service`, `user-service`, `product-service`, `notification-service`
  - Expose HTTP endpoints and depend on corresponding `*UseCase`
  - Domain rules kept in `domain.service`

### 6. Coding Rules (Team Convention)

- Controller cannot call remote clients directly; go through `application` use cases.
- Application service cannot reference concrete infrastructure class names.
- Infrastructure cannot contain business decision logic.
- Domain model/state transition should happen in domain layer.
- New cross-service call must be represented as an `application.port` interface first.

### 7. Scaffold-First Policy

This repository currently follows scaffold-first implementation:

- Keep business details minimal and replaceable.
- Prioritize stable boundaries and dependency direction.
- Add real business complexity after ports and layer contracts are stable.

### 8. Gateway Auth with Redis Session

Current gateway authentication no longer calls `user-service` at runtime.

- Login path:
  - `POST /api/auth/login` is handled by `user-service`.
  - After successful login, token session fields are stored by Sa-Token:
    - `userId`
    - `username`
    - `roles`
    - `permissions`
- Request path:
  - Client sends `satoken`.
  - `GatewayAuthFilter` validates token and reads token-session directly from Redis.
  - Gateway checks path-based permission mapping and returns:
    - `401` for invalid/missing token
    - `403` for permission denied
  - If passed, gateway forwards user context headers to downstream services.

Required dependencies and config (already applied):

- Dependencies:
  - `sa-token-redis-jackson`
  - `spring-boot-starter-data-redis`
- Config:
  - `spring.data.redis.host`
  - `spring.data.redis.port`

### 9. Service-to-Service Calls (Spring Cloud)

Inter-service calls use Spring Cloud OpenFeign with Nacos service discovery.

- Consumer side:
  - `order-service` Feign clients call dependent services by service name.
- Provider side:
  - `user/product/inventory/payment/notification/address` expose HTTP endpoints.
- Discovery:
  - `spring.cloud.nacos.discovery.server-addr=localhost:8848`

### 10. Local Integration Runbook

#### Prerequisites

- JDK 17
- Maven Wrapper (`./mvnw`)
- Docker / Docker Compose

#### Recommended startup order

1. `commerce-user-service`
2. `commerce-gateway`
3. Dependent business services:
   - `commerce-product-service`
   - `commerce-inventory-service`
   - `commerce-payment-service`
   - `commerce-notification-service`
   - `commerce-address-service`
4. `commerce-order-service`

#### Build and run

- Build runnable jars locally (required before Docker startup):
  - `./mvnw -DskipTests package`
- One-command startup (Docker, `dev` by default):
  - `bash scripts/deploy/up-all.sh dev`
- One-command stop:
  - `bash scripts/deploy/down-all.sh dev`
- Switch environment:
  - `bash scripts/deploy/up-all.sh test`
  - `bash scripts/deploy/up-all.sh prod`
- Run one module directly (non-Docker, optional):
  - `./mvnw -pl commerce-user-service spring-boot:run`

#### Minimal verification flow

1. Login to get token:
   - `POST http://localhost:8080/api/auth/login`
   - Body:
     - `{"username":"customer","password":"123456"}`
2. Call protected API with header `satoken: <token>`:
   - `GET http://localhost:8080/api/products/1/price`
   - `GET http://localhost:8080/api/addresses/2/default`
3. Verify permission behavior:
   - Missing token -> `401`
   - Invalid/expired token -> `401`
   - Insufficient permission -> `403`

#### Inter-service call notes

- `order-service` uses OpenFeign for remote calls.
- Ensure all dependent services are registered in Nacos before testing create-order flow.
- Flyway migrations run automatically at service startup.

---

## commerce DDD 骨架指南（中文）

### 1. 服务内部分层

每个业务服务统一采用以下结构：

```text
com.commerce.<bounded-context>
├── interfaces
│   └── rest
├── application
│   ├── port
│   ├── command（可选）
│   └── service
├── domain
│   ├── model
│   ├── service
│   └── repository
└── infrastructure
    ├── rpc
    └── persistence
```

### 2. 依赖方向（必须遵守）

- `interfaces -> application`
- `application -> domain + application.port`
- `infrastructure -> application.port / domain.repository（实现接口）`
- `domain` 不依赖 `interfaces` 或 `infrastructure`

不要反向依赖。

### 3. 各层职责

- `interfaces`
  - 接收 HTTP 请求并返回响应。
  - 将请求 DTO 转为应用层命令对象。
  - 不编排业务流程。

- `application`
  - 编排完整用例流程。
  - 通过 `port` 接口依赖外部能力，不直接依赖具体适配器。
  - 协调领域规则与持久化边界。

- `domain`
  - 承载核心业务规则、状态流转和不变量。
  - 包含实体/值对象/领域服务/仓储契约。
  - 不直接使用 HTTP、数据库框架或远程调用框架。

- `infrastructure`
  - 只放技术适配器。
  - 包含 HTTP 客户端/控制器、数据库实现和外部系统接入。
  - 实现 `application.port` 或 `domain.repository` 定义的接口。

### 4. 每个用例的最小模板

新增一个用例时，按以下顺序落地：

1. `application.port.<XxxUseCase>`
2. `application.command.<XxxCommand>`（如需要）
3. `domain.model` / `domain.service` 规则对象
4. `application.service.<XxxApplicationService>` 实现用例
5. `interfaces.rest.<XxxController>` 仅调用用例接口
6. `infrastructure.*` 实现端口/仓储

### 5. 当前项目映射示例

- `order-service`
  - `OrderController -> OrderUseCase -> OrderApplicationService`
  - `OrderApplicationService` 依赖：
    - `UserQueryPort`
    - `ProductQueryPort`
    - `InventoryCommandPort`
    - `PaymentCommandPort`
    - `NotificationCommandPort`
    - `OrderRepository`
  - `infrastructure.rpc.*Client` 使用 OpenFeign 实现外部调用端口
  - `infrastructure.persistence.InMemoryOrderRepository` 实现仓储接口

- `inventory-service`、`payment-service`、`user-service`、`product-service`、`notification-service`
  - 通过 HTTP 暴露能力并统一依赖对应 `*UseCase`
  - 领域规则放在 `domain.service`

### 6. 编码约束（团队规范）

- Controller 不能直接调用远程客户端，应通过 `application` 用例编排。
- Application Service 不能引用具体基础设施类名。
- Infrastructure 层不能承载业务决策逻辑。
- 领域模型与状态流转必须放在 Domain 层。
- 新增跨服务调用时，先定义 `application.port` 接口，再做基础设施实现。

### 7. 骨架优先策略

当前仓库采用骨架优先实现方式：

- 业务细节保持最小且可替换。
- 优先保证边界稳定和依赖方向正确。
- 在端口与分层契约稳定后，再逐步增加真实业务复杂度。

### 8. 基于 Redis 会话的网关鉴权

当前网关鉴权在运行时不再回调 `user-service`。

- 登录路径：
  - `POST /api/auth/login` 由 `user-service` 处理。
  - 登录成功后，Sa-Token 会把以下 token-session 字段写入 Redis：
    - `userId`
    - `username`
    - `roles`
    - `permissions`
- 请求路径：
  - 客户端携带 `satoken` 访问业务接口。
  - `GatewayAuthFilter` 直接从 Redis 会话读取并校验。
  - 网关按路径映射做权限检查，返回：
    - token 缺失或无效 -> `401`
    - 权限不足 -> `403`
  - 校验通过后，透传用户上下文 header 给下游服务。

已接入的依赖和配置：

- 依赖：
  - `sa-token-redis-jackson`
  - `spring-boot-starter-data-redis`
- 配置：
  - `spring.data.redis.host`
  - `spring.data.redis.port`

### 9. Spring Cloud 服务间调用

当前服务间调用采用 Spring Cloud OpenFeign + Nacos 服务发现。

- 调用方：
  - `order-service` 中的 Feign Client 按服务名调用下游服务。
- 提供方：
  - `user/product/inventory/payment/notification/address` 通过 HTTP 接口提供能力。
- 服务发现：
  - 统一使用 `spring.cloud.nacos.discovery.server-addr=localhost:8848`

### 10. 本地联调步骤

#### 前置条件

- JDK 17
- Maven Wrapper（`./mvnw`）
- Docker / Docker Compose

#### 推荐启动顺序

1. `commerce-user-service`
2. `commerce-gateway`
3. 下游依赖服务：
   - `commerce-product-service`
   - `commerce-inventory-service`
   - `commerce-payment-service`
   - `commerce-notification-service`
   - `commerce-address-service`
4. `commerce-order-service`

#### 构建与运行

- 先在本地打可运行 jar（Docker 启动前必须）：
  - `./mvnw -DskipTests package`
- 一键启动（Docker，默认 `dev`）：
  - `bash scripts/deploy/up-all.sh dev`
- 一键停止：
  - `bash scripts/deploy/down-all.sh dev`
- 切换环境：
  - `bash scripts/deploy/up-all.sh test`
  - `bash scripts/deploy/up-all.sh prod`
- 单模块本地直启（非 Docker，可选）：
  - `./mvnw -pl commerce-user-service spring-boot:run`

#### 最小验证流程

1. 登录获取 token：
   - `POST http://localhost:8080/api/auth/login`
   - 请求体：
     - `{"username":"customer","password":"123456"}`
2. 携带 `satoken: <token>` 调用受保护接口：
   - `GET http://localhost:8080/api/products/1/price`
   - `GET http://localhost:8080/api/addresses/2/default`
3. 验证权限行为：
   - 不带 token -> `401`
   - token 无效/过期 -> `401`
   - 权限不足 -> `403`

#### 服务间调用注意事项

- `order-service` 通过 OpenFeign 调用下游服务。
- 联调前请先确保下游依赖服务都已在 Nacos 注册成功。
- 服务启动时会自动执行 Flyway 迁移脚本。
