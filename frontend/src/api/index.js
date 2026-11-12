import http from './http'

export const authApi = {
  login: (data) => http.post('/api/auth/login', data),
  register: (data) => http.post('/api/auth/register', data),
  me: () => http.get('/api/user/me')
}

export const shopApi = {
  categories: () => http.get('/api/categories'),
  products: (params) => http.get('/api/products', { params }),
  product: (id) => http.get('/api/products/' + id),
  notices: () => http.get('/api/notices'),
  banners: () => http.get('/api/banners')
}

export const cartApi = {
  list: () => http.get('/api/cart'),
  add: (data) => http.post('/api/cart', data),
  update: (id, quantity) => http.put('/api/cart/' + id, { quantity }),
  remove: (id) => http.delete('/api/cart/' + id)
}

export const orderApi = {
  create: (data) => http.post('/api/orders', data),
  list: (params) => http.get('/api/orders', { params }),
  detail: (id) => http.get('/api/orders/' + id),
  pay: (id) => http.post('/api/orders/' + id + '/pay'),
  cancel: (id) => http.post('/api/orders/' + id + '/cancel'),
  confirm: (id) => http.post('/api/orders/' + id + '/confirm')
}

export const postApi = {
  list: (params) => http.get('/api/posts', { params }),
  detail: (id) => http.get('/api/posts/' + id),
  create: (data) => http.post('/api/posts', data),
  comment: (id, content) => http.post('/api/posts/' + id + '/comments', { content }),
  remove: (id) => http.delete('/api/posts/' + id)
}

export const adminApi = {
  stats: () => http.get('/api/admin/stats'),
  users: (params) => http.get('/api/admin/users', { params }),
  userStatus: (id, status) => http.put('/api/admin/users/' + id + '/status', { status }),
  products: (params) => http.get('/api/admin/products', { params }),
  createProduct: (data) => http.post('/api/admin/products', data),
  updateProduct: (id, data) => http.put('/api/admin/products/' + id, data),
  productStatus: (id, status) => http.put('/api/admin/products/' + id + '/status', { status }),
  deleteProduct: (id) => http.delete('/api/admin/products/' + id),
  orders: (params) => http.get('/api/admin/orders', { params }),
  ship: (id) => http.put('/api/admin/orders/' + id + '/ship'),
  posts: (params) => http.get('/api/admin/posts', { params }),
  audit: (id, status) => http.put('/api/admin/posts/' + id + '/audit', { status }),
  noticeCreate: (data) => http.post('/api/admin/notices', data),
  noticeUpdate: (id, data) => http.put('/api/admin/notices/' + id, data),
  noticeDelete: (id) => http.delete('/api/admin/notices/' + id),
  bannerCreate: (data) => http.post('/api/admin/banners', data),
  bannerUpdate: (id, data) => http.put('/api/admin/banners/' + id, data),
  bannerDelete: (id) => http.delete('/api/admin/banners/' + id)
}
