# commerce-address-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-address-service.md`。
- 地址读写必须校验 Owner；不能通过请求体 userId 转移或越权访问地址。
- 切换默认地址时，在同一事务清除旧默认并设置新默认，注意并发唯一性。
- 内部订单接口只返回未删除地址，并避免在日志中泄露个人信息。
- 运行 `./mvnw -pl commerce-address-service -am test`。
