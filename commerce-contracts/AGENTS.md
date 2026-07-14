# commerce-contracts 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-contracts.md` 和 `../docs/agent-memory/ENGINEERING_CONVENTIONS.md`。
- 这里只放跨服务稳定协议和通用基础类型，不放单服务业务逻辑或持久化模型。
- 变更 DTO、事件、安全常量或 MQ 拓扑时，全仓检查所有生产者和消费者并保持独立部署兼容。
- 运行 `./mvnw -pl commerce-contracts -am test`，并编译所有受影响模块。
