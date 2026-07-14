# commerce-notification-service 服务记忆

## 定位与入口

- 端口 `8086`，拥有 `commerce_notification` PostgreSQL 数据库并连接 RabbitMQ。
- `/api/notifications` 与 `/internal/notifications` 支持待支付、已支付、已取消订单通知。
- `OrderCreatedListener` 消费 `ORDER_CREATED_QUEUE`，记录待支付通知。

## 当前实现

- `notification_records` 保存 orderId、userId、templateCode、channel、payload、status、idempotencyKey、发送时间和错误。
- 当前发送通道为 `LOG`，不是实际短信、邮件或站内信供应商。
- 直接调用先以 PENDING 记录，再更新 SENT/FAILED；MQ Listener 直接记录 SENT。
- 幂等键形如 `order-wait-pay:<orderId>`、`order-paid:<orderId>`、`order-cancelled:<orderId>`，数据库有唯一索引。

## 不变量

- 同一业务通知的所有入口必须共享稳定幂等键，REST、Outbox 重试和 MQ 重投不能产生重复通知。
- 消费者先检查/依赖唯一约束去重；并发插入冲突也应视为幂等，而不是无限重试。
- 新真实 Channel 使用适配器隔离供应商，并保存可诊断但不泄露隐私/密钥的结果。
- 通知失败不能回滚订单核心状态；通过状态、重试和死信/人工补偿处理。
- MQ contract 或 topology 变化必须同步 `commerce-contracts` 和所有 Publisher。

## 修改检查

- 覆盖重复 REST、重复 MQ、并发去重、发送失败、重试终态和敏感信息日志。
- 明确 PENDING/SENT/FAILED 的合法迁移和重试次数，而不是覆盖式更新。
- 运行 `./mvnw -pl commerce-notification-service -am test`。
