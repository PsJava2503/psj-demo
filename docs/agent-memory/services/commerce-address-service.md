# commerce-address-service 服务记忆

## 定位与接口

- 端口 `8087`，拥有 `commerce_address` PostgreSQL 数据库和 `addresses` 表。
- `/api/addresses` 提供创建、更新、删除、条件查询、详情和默认地址查询。
- `/internal/addresses/{addressId}` 为订单服务返回未删除的 `AddressResponse`。

## 当前行为

- 地址包含 userId、收件人、电话、省市区、详细地址、defaultAddress、deleted 和时间字段。
- 创建或更新为默认地址前，Domain Service 会先清除该用户其他未删除地址的默认标记。
- 默认地址唯一性目前由应用流程保证，数据库没有“每用户唯一默认地址”的唯一约束。
- 内部详情不存在时抛出错误，订单创建使用返回的地址快照。

## 不变量

- 地址只能由 Owner 或明确授权的管理员访问和修改；不得只信任请求体/查询参数 userId。
- 设置默认地址与清除旧默认地址应在同一事务内，避免并发产生多个默认地址。
- 更新地址时必须验证 path addressId、记录 Owner 与当前用户一致，不能用请求体 userId 转移归属。
- 删除后内部订单查询不能再把地址当作可选新地址，但历史订单中的地址快照保持不变。
- 电话和详细地址属于个人信息，日志和 Agent Tool 输出要最小化。

## 修改检查

- 覆盖首次默认地址、切换默认地址、并发切换、删除默认地址和跨用户访问。
- `AddressResponse` 变化时编译 order-service。
- 运行 `./mvnw -pl commerce-address-service -am test`。
