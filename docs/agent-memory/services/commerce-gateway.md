# commerce-gateway 模块记忆

## 定位与依赖

- 端口 `8080`，通过 Nacos 的 `lb://` 路由聚合所有 `/api/**` 服务和各模块 OpenAPI 文档。
- 使用 Redis/Sa-Token 读取 token session；本模块没有业务数据库。
- `GatewayAuthFilter` 是当前唯一核心 Java 组件，优先级为 `-100`。

## 当前认证与授权流程

- 非 `/api/**` 请求直接放行。
- `/api/auth/login`、`/api/auth/register`、`/api/payments/notify` 当前匿名放行；支付回调必须由 payment-service 校验签名。
- 其他 API 要求 `satoken` header，从 token session 读取 userId、username、roles、permissions。
- 按路径前缀映射 `CommercePermissions`；当前规则是路径级，不区分 HTTP 方法。
- `/api/rbac/**` 当前要求登录，但不在权限前缀 Map 中，没有额外的具体 permission 检查。
- 授权成功后写入 `SecurityHeaders` 的 `X-User-Id`、`X-Username`、`X-Roles`、`X-Permissions`，并清空下游 `Authorization`。

## 不变量

- Gateway 只做入口认证、粗粒度权限和路由，不承载订单、用户等业务规则。
- 下游不能把身份头当作来自任意公网客户端的可信数据；部署上应只允许经 Gateway 或受信内部网络访问。
- 新增 `/api/**` 服务时同步添加 route、permission、OpenAPI route 和 Swagger URL。
- 新匿名端点必须最小化精确匹配，不能使用过宽前缀。
- 资源归属仍由下游业务服务校验，Gateway permission 不能替代 Owner 检查。

## 修改检查

- 覆盖未登录 401、缺权限 403、匿名白名单、身份头覆盖/清理和路由匹配测试。
- 权限代码只能引用 `commerce-contracts` 中的常量，并同步 user-service migration。
- 运行 `./mvnw -pl commerce-gateway -am test`。
