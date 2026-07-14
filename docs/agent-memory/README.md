# Agent Memory Index

这些文件是本仓库编码 Agent 的持久记忆，不是面向商城用户的聊天知识库，也不会自动上传到 `commerce-agent-service` 的 RAG。

## 按任务读取

| 任务 | 必读文档 |
| --- | --- |
| 第一次进入仓库、跨模块改动 | [PROJECT_ARCHITECTURE.md](PROJECT_ARCHITECTURE.md) |
| Java/Spring/API/数据库/消息开发 | [ENGINEERING_CONVENTIONS.md](ENGINEERING_CONVENTIONS.md) |
| 订单、支付、库存、退款或权限改动 | [BUSINESS_INVARIANTS.md](BUSINESS_INVARIANTS.md) |
| Agent、Tool、Session、SSE、RAG 改动 | [AGENT_HARNESS.md](AGENT_HARNESS.md) |

## 服务级记忆

修改模块前读取对应文件：

| 模块 | 服务记忆 |
| --- | --- |
| Gateway | [commerce-gateway.md](services/commerce-gateway.md) |
| 共享 Contract | [commerce-contracts.md](services/commerce-contracts.md) |
| 用户与 RBAC | [commerce-user-service.md](services/commerce-user-service.md) |
| 商品 | [commerce-product-service.md](services/commerce-product-service.md) |
| 地址 | [commerce-address-service.md](services/commerce-address-service.md) |
| 购物车 | [commerce-cart-service.md](services/commerce-cart-service.md) |
| 订单 | [commerce-order-service.md](services/commerce-order-service.md) |
| 支付 | [commerce-payment-service.md](services/commerce-payment-service.md) |
| 库存 | [commerce-inventory-service.md](services/commerce-inventory-service.md) |
| 通知 | [commerce-notification-service.md](services/commerce-notification-service.md) |
| Agent | [commerce-agent-service.md](services/commerce-agent-service.md) |

## 维护原则

1. 源码、测试、配置和 Flyway migration 是当前实现的最终依据。
2. 记录稳定事实和容易被破坏的不变量，不记录临时任务进度、猜测或密钥。
3. 当前能力与规划能力必须分开写；未落地的设计使用“规划”或“候选方案”标识。
4. 架构或业务约束变化时，在同一个改动中更新对应记忆文档。
5. `AGENTS.md` 只保留高频硬规则；细节留在这里，由任务按需加载。
