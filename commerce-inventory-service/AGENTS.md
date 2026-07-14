# commerce-inventory-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-inventory-service.md` 和库存业务不变量。
- 仓级/库位级余额、锁定量、Reservation、Transaction 和 Ledger 在同一事务保持一致。
- 保持预占、绑定、确认、释放和未绑定 TTL 回收的幂等/冲突语义。
- 不允许负可用库存；库存写 API 使用 update 权限，内部订单接口保持受信边界。
- 运行 `./mvnw -pl commerce-inventory-service -am test`。
