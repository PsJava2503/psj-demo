# commerce-notification-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-notification-service.md`。
- REST、Outbox 重试和 MQ 重投必须共享稳定幂等键，数据库唯一约束是最后防线。
- 通知失败不回滚订单核心状态；通过状态、重试和补偿处理。
- 当前仅有 LOG 通道；新增真实供应商时隔离适配器并保护隐私和密钥。
- 运行 `./mvnw -pl commerce-notification-service -am test`。
