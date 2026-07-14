# commerce-product-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-product-service.md`。
- 商品服务拥有目录和价格，不拥有库存；skuId 是与库存连接的稳定键。
- 内部订单查询只返回启用且未删除商品，历史订单继续使用成交快照。
- 金额使用 `BigDecimal`，并测试禁用、软删除、缺失商品和价格边界。
- 运行 `./mvnw -pl commerce-product-service -am test`。
