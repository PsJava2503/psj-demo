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
