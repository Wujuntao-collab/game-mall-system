import { createRouter, createWebHashHistory } from 'vue-router'
import { isLogin, isAdmin } from '../store/user'

const routes = [
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/products', component: () => import('../views/Products.vue') },
  { path: '/products/:id', component: () => import('../views/ProductDetail.vue') },
  { path: '/posts', component: () => import('../views/Posts.vue') },
  { path: '/posts/:id', component: () => import('../views/PostDetail.vue') },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  { path: '/cart', component: () => import('../views/Cart.vue'), meta: { auth: true } },
  { path: '/orders', component: () => import('../views/Orders.vue'), meta: { auth: true } },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { admin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'products', component: () => import('../views/admin/Products.vue') },
      { path: 'orders', component: () => import('../views/admin/Orders.vue') },
      { path: 'users', component: () => import('../views/admin/Users.vue') },
      { path: 'posts', component: () => import('../views/admin/Posts.vue') },
      { path: 'content', component: () => import('../views/admin/Content.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to) => {
  if (to.meta.auth && !isLogin()) return '/login'
  if (to.meta.admin && !isAdmin()) return '/login'
  return true
})

export default router
