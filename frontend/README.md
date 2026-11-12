# 游戏商城系统 · 前端

Vue 3 + Vite 5 + Element Plus 实现的商城端与管理后台。

## 运行

```bash
npm install
npm run build
node serve.cjs        # http://127.0.0.1:5173
```

开发模式（热更新）：

```bash
npm run dev           # http://localhost:5173
```

默认把 `/api` 代理到 `http://localhost:8080`（后端）。

## 页面

| 路径 | 页面 |
| --- | --- |
| / | 首页（轮播、公告、推荐） |
| /products | 商城列表（搜索、分类、分页） |
| /products/:id | 商品详情（加购、立即购买） |
| /cart | 购物车 |
| /orders | 我的订单（支付、取消、确认收货） |
| /posts | 社区列表（发帖） |
| /posts/:id | 帖子详情（评论） |
| /login、/register | 登录、注册 |
| /admin/dashboard | 后台·数据概览 |
| /admin/products | 后台·商品管理 |
| /admin/orders | 后台·订单管理 |
| /admin/users | 后台·用户管理 |
| /admin/posts | 后台·帖子审核 |
| /admin/content | 后台·公告与轮播 |

## 说明

- 路由守卫：未登录访问购物车/订单会跳转登录；非管理员访问 `/admin` 会被拦下。
- axios 拦截器统一处理 401（清 token 并跳登录）与其他错误提示。
- `serve.cjs` 是零依赖静态服务器 + API 代理，用于生产构建后的演示（不依赖 vite dev server）。
