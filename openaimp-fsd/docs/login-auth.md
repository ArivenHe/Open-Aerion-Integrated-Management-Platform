# 登录认证开发文档

## 背景
系统登录认证不再访问本地数据库，改为调用外部认证 API 获取 JWT，并在服务端解析 JWT 的关键字段完成登录校验。

## 认证 API 端点
- 方法：POST
- 地址：`${AUTH_API_ENDPOINT}`
- 默认值：`https://auth.vatsim.net/api/fsd-jwt`
- 环境变量：`AUTH_API_ENDPOINT`（可覆盖默认值）

## 请求
- Content-Type: `application/json`
- Body:
```json
{
  "cid": "123456",
  "password": "s3cr3t"
}
```

## 响应
### 成功
- HTTP 200
```json
{
  "success": true,
  "token": "<jwt token>"
}
```

### 失败
- HTTP 4xx / 5xx
```json
{
  "success": false,
  "error_msg": "<error message>"
}
```

## JWT 字段要求
服务端解析 JWT 的以下字段用于校验：
- `sub`：用户 CID（字符串）
- `exp` / `nbf`：时间窗口校验
- `controller_rating` / `pilot_rating`：用于计算最大网络等级

## 服务端校验逻辑
1. `sub` 必须等于登录 CID
2. 取 `controller_rating` 与 `pilot_rating` 的较大值作为最大等级
3. 登录请求中的 `network_rating` 不能高于最大等级
4. `network_rating` 低于 Observer 直接拒绝

## 错误处理建议
- 认证 API 返回非 200、`success=false`、或 `token` 为空均视为认证失败
- 解析 JWT 失败、`sub` 不匹配、时间窗口无效时，返回“Invalid CID/password”
