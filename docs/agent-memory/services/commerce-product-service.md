# commerce-product-service 服务记忆

## 定位与接口

- 端口 `8084`，拥有 `commerce_product` PostgreSQL 数据库和 `products` 表。
- `/api/products` 提供商品 CRUD、条件查询和价格查询。
- `/internal/products/{productId}` 与 `/price` 为订单等内部调用提供启用且未删除的商品快照。
- `ProductResponse` 位于 `commerce-contracts`，包含 id、name、price、skuId、enabled。

## 当前模型与行为

- 商品记录保存名称、价格、skuId、enabled、deleted 和时间字段。
- 删除走 Repository 的删除语义；查询应明确是否包含软删除记录。
- 内部商品详情只读取 `enabled=true`、`deleted=false`。
- `priceOf` 在商品不存在时当前返回 `BigDecimal.ZERO`；调用方不能把 0 自动解释成有效免费商品。
- 商品服务拥有目录与价格快照，不拥有库存数量；库存由 inventory-service 按 skuId 管理。

## 不变量

- 金额使用 `BigDecimal`，新增校验时明确负数、零价和小数精度策略。
- skuId 是商品到库存的连接键；改变含义时检查订单快照、购物车快照和库存查询。
- 订单项保存成交时商品快照，历史订单不得被后续商品改名或改价回写。
- 对外写接口按 create/update/view 权限区分，而不是只依赖统一的 product:view 路径权限。

## 修改检查

- 覆盖启用/禁用、软删除、内部查询过滤、缺失商品和价格边界。
- 共享 `ProductResponse` 变化时编译 order-service 和 agent-service。
- 运行 `./mvnw -pl commerce-product-service -am test`。
