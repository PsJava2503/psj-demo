## psj-commerce DDD Skeleton Guide (EN)

### 1. Service Internal Layers

Each business service uses the same internal structure:

```text
com.psj.commerce.<bounded-context>
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
  - Does not use Dubbo, HTTP, DB frameworks directly.

- `infrastructure`
  - Technical adapters only.
  - Dubbo providers/consumers, database implementations, external integrations.
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
  - `infrastructure.rpc.*Client` implements outbound ports
  - `infrastructure.persistence.InMemoryOrderRepository` implements repository

- `inventory-service`, `payment-service`, `user-service`, `product-service`, `notification-service`
  - Controller and Dubbo implementation both depend on corresponding `*UseCase`
  - Domain rules kept in `domain.service`

### 6. Coding Rules (Team Convention)

- Controller cannot call Dubbo API directly.
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

### 9. Dubbo Internal Signature Skeleton

Internal Dubbo calls are now protected by a signature skeleton (without nonce replay cache).

- Consumer filter: `internalSignConsumer`
  - Adds invocation attachments:
    - `x-internal-service`
    - `x-internal-ts`
    - `x-internal-nonce`
    - `x-internal-sign`
- Provider filter: `internalVerifyProvider`
  - Verifies attachment completeness.
  - Verifies timestamp window (`5 minutes` default).
  - Verifies HMAC-SHA256 signature.
- SPI registration:
  - `META-INF/dubbo/org.apache.dubbo.rpc.Filter`

Secret resolution order:

1. JVM property: `-Dinternal.auth.secret=...`
2. Environment variable: `INTERNAL_AUTH_SECRET`
3. Fallback placeholder value (for demo only; replace in real env)

### 10. Local Integration Runbook

#### Prerequisites

- JDK 17
- Maven Wrapper (`./mvnw`)
- Nacos running at `localhost:8848`
- Redis running at `localhost:6379`

#### Recommended startup order

1. `psj-commerce-user-service`
2. `psj-commerce-gateway`
3. RPC provider services:
   - `psj-commerce-product-service`
   - `psj-commerce-inventory-service`
   - `psj-commerce-payment-service`
   - `psj-commerce-notification-service`
   - `psj-commerce-address-service`
4. `psj-commerce-order-service`

#### Build and run

- Full build:
  - `./mvnw test`
- Run one module (example):
  - `./mvnw -pl psj-commerce-user-service spring-boot:run`

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

#### Internal signature notes

- Set shared secret consistently for all Dubbo services:
  - JVM property: `-Dinternal.auth.secret=<your-secret>`
  - or env: `INTERNAL_AUTH_SECRET=<your-secret>`
- If secrets differ between caller/provider, Dubbo call will fail with signature error.

---

## psj-commerce DDD 骨架指南（中文）

### 1. 服务内部分层

每个业务服务统一采用以下结构：

```text
com.psj.commerce.<bounded-context>
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
  - 不直接使用 Dubbo、HTTP、数据库框架。

- `infrastructure`
  - 只放技术适配器。
  - 包含 Dubbo 提供者/消费者、数据库实现和外部系统接入。
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
  - `infrastructure.rpc.*Client` 实现外部调用端口
  - `infrastructure.persistence.InMemoryOrderRepository` 实现仓储接口

- `inventory-service`、`payment-service`、`user-service`、`product-service`、`notification-service`
  - Controller 和 Dubbo 实现统一依赖对应 `*UseCase`
  - 领域规则放在 `domain.service`

### 6. 编码约束（团队规范）

- Controller 不能直接调用 Dubbo API。
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

### 9. Dubbo 服务间签名骨架

当前 Dubbo 内部调用已接入签名骨架（不包含 Redis nonce 防重放）。

- Consumer 过滤器：`internalSignConsumer`
  - 自动附加调用附件：
    - `x-internal-service`
    - `x-internal-ts`
    - `x-internal-nonce`
    - `x-internal-sign`
- Provider 过滤器：`internalVerifyProvider`
  - 校验附件完整性
  - 校验时间窗（默认 5 分钟）
  - 校验 HMAC-SHA256 签名
- SPI 注册文件：
  - `META-INF/dubbo/org.apache.dubbo.rpc.Filter`

签名密钥读取顺序：

1. JVM 启动参数：`-Dinternal.auth.secret=...`
2. 环境变量：`INTERNAL_AUTH_SECRET`
3. 默认占位值（仅用于 demo，生产必须替换）

### 10. 本地联调步骤

#### 前置条件

- JDK 17
- Maven Wrapper（`./mvnw`）
- Nacos 已在 `localhost:8848` 运行
- Redis 已在 `localhost:6379` 运行

#### 推荐启动顺序

1. `psj-commerce-user-service`
2. `psj-commerce-gateway`
3. 各 Dubbo Provider 服务：
   - `psj-commerce-product-service`
   - `psj-commerce-inventory-service`
   - `psj-commerce-payment-service`
   - `psj-commerce-notification-service`
   - `psj-commerce-address-service`
4. `psj-commerce-order-service`

#### 构建与运行

- 全量构建：
  - `./mvnw test`
- 单模块启动（示例）：
  - `./mvnw -pl psj-commerce-user-service spring-boot:run`

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

#### 服务间签名注意事项

- 所有 Dubbo 服务必须使用同一套共享密钥：
  - JVM 参数：`-Dinternal.auth.secret=<your-secret>`
  - 或环境变量：`INTERNAL_AUTH_SECRET=<your-secret>`
- 调用方和被调方密钥不一致时，会出现签名校验失败。
