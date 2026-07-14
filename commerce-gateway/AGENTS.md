# commerce-gateway 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-gateway.md`。
- Gateway 只负责路由、入口认证、粗粒度权限和可信身份头，不承载领域业务。
- 新 API 服务同步维护 route、permission、OpenAPI route 和 Swagger URL；匿名白名单使用精确匹配。
- 身份头必须由 Gateway 覆盖生成，下游资源归属仍由业务服务校验。
- 运行 `./mvnw -pl commerce-gateway -am test`。
