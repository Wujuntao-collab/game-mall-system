# 游戏商城系统

基于 **Spring Boot + Vue 3** 的前后端分离游戏商城，把"看游戏、买游戏、聊游戏、管账号"串成一条链路。

个人毕业设计项目，独立完成需求分析、数据库设计、后端接口、前端页面与测试。

---

## 一、技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.2、MyBatis 3（注解 SQL）、Spring MVC |
| 数据库 | MySQL 8（生产配置）/ H2 内存库（开箱即用） |
| 鉴权 | JWT 无状态鉴权（HS256，手写实现）、BCrypt 密码加密 |
| 前端 | Vue 3、Vite 5、Element Plus、Vue Router、Axios |
| 构建 | Maven、npm |

## 二、功能模块（10 个，全部完成）

**用户端**

| 模块 | 功能 |
| --- | --- |
| 首页 | 轮播图、公告、热门商品推荐 |
| 游戏社区 | 帖子列表/详情、发帖（需审核）、评论、帖子关联商品 |
| 游戏商场中心 | 分类浏览、关键词搜索、分页、商品详情 |
| 购物车与订单 | 加购、改数量、结算下单、支付、取消、确认收货 |
| 个人中心 | 登录注册、我的订单、订单明细 |

**管理端**

| 模块 | 功能 |
| --- | --- |
| 用户管理 | 用户列表、禁用/启用 |
| 商品管理 | 新增、编辑、上下架、删除、搜索 |
| 订单管理 | 全部订单、发货 |
| 社区管理 | 帖子审核（通过/驳回）、状态筛选 |
| 系统管理 | 公告增删改、轮播图增删改、数据概览 |

## 三、关键设计（答辩可讲）

1. **JWT 鉴权**：`JwtUtil` 手写 HS256 签名/验签，不依赖第三方库；token 携带 `uid/role/exp`，拦截器统一校验并按路径放行公开只读接口。
2. **防超卖**：库存扣减用 `UPDATE product SET stock = stock - ? WHERE id = ? AND stock >= ?`，靠数据库条件更新保证并发安全，影响行数为 0 即库存不足。
3. **订单快照**：`order_item` 保存下单时的商品名与单价，商品后续改价、下架都不影响历史订单展示。
4. **社区与商城联动**：帖子可挂 `product_id`，列表与详情带出关联商品；帖子默认待审核，管理端通过后才在前台可见。
5. **统一异常出口**：业务异常映射为真实 HTTP 状态码（400/401/403/404），前端拦截器据此跳登录或提示。

## 四、运行方式

### 后端（默认 H2，无需装数据库）

```bash
cd game-mall-system
mvn spring-boot:run
```

- 服务地址：http://localhost:8080
- H2 控制台：http://localhost:8080/h2-console （JDBC URL `jdbc:h2:mem:mall`，用户 `sa`，密码空）
- 首次启动自动建表并写入演示数据，管理员：`admin / admin123`

切换 MySQL：

```bash
mysql -u root -p < sql/init.sql
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

（账号密码在 `src/main/resources/application-mysql.yml`）

### 前端

```bash
cd game-mall-web
npm install
npm run build
node serve.cjs        # 访问 http://127.0.0.1:5173
```

开发模式（热更新）：`npm run dev`，访问 http://localhost:5173

前端通过 `/api` 代理到后端 8080。

## 五、接口一览

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | /api/auth/register | 注册 | 公开 |
| POST | /api/auth/login | 登录，返回 token | 公开 |
| GET | /api/user/me | 当前用户 | 登录 |
| GET | /api/categories | 分类 | 公开 |
| GET | /api/products | 商品分页/搜索 | 公开 |
| GET | /api/products/{id} | 商品详情 | 公开 |
| GET | /api/notices | 公告 | 公开 |
| GET | /api/banners | 轮播图 | 公开 |
| GET | /api/posts | 帖子列表（已审核） | 公开 |
| GET | /api/posts/{id} | 帖子详情（含评论，浏览量+1） | 公开 |
| POST | /api/posts | 发帖（待审核） | 登录 |
| POST | /api/posts/{id}/comments | 评论 | 登录 |
| DELETE | /api/posts/{id} | 删自己的帖子 | 登录 |
| GET/POST/PUT/DELETE | /api/cart | 购物车 | 登录 |
| POST | /api/orders | 下单（购物车或直接购买） | 登录 |
| GET | /api/orders | 我的订单 | 登录 |
| POST | /api/orders/{id}/pay | 支付（模拟） | 登录 |
| POST | /api/orders/{id}/cancel | 取消 | 登录 |
| POST | /api/orders/{id}/confirm | 确认收货 | 登录 |
| GET | /api/admin/** | 用户/商品/订单/帖子/公告/轮播/统计 | 管理员 |

## 六、测试

- 后端接口：`smoke.js` —— 认证、权限、商品、购物车、下单、支付、发货、取消回滚、下架可见性等 **32 项**
- 社区与内容：`smoke_phase2.js` —— 发帖、审核、评论、公告轮播 CRUD **21 项**
- 前端服务与代理：`game-mall-web/check.js` —— **9 项**

## 七、目录结构

```
game-mall-system/                     后端
├── src/main/java/com/wujuntao/mall
│   ├── common/      统一响应、异常处理、JWT 工具
│   ├── config/      拦截器、跨域、演示数据初始化
│   ├── controller/  接口层
│   ├── dto/         请求响应对象
│   ├── entity/      实体
│   ├── mapper/      MyBatis 数据访问
│   └── service/     业务逻辑
├── src/main/resources
│   ├── application.yml / application-mysql.yml
│   └── schema.sql   建表脚本（H2）
└── sql/init.sql     MySQL 建库脚本

game-mall-web/                        前端
├── src/api/         接口封装与 axios 拦截器
├── src/router/      路由与登录/权限守卫
├── src/store/       登录态
├── src/views/       用户端页面
├── src/views/admin/ 管理后台页面
└── serve.cjs        零依赖静态服务器 + /api 代理
```

## 八、已知限制（后续可扩展）

- 支付为模拟实现，未接真实支付渠道；
- Redis 缓存规划中，当前 token 靠签名与过期时间校验；
- 图片上传未实现，商品封面临时用文字占位；
- 商品评价未做（评论目前用于社区帖子）。
