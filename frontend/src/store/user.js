import { reactive } from 'vue'
import { authApi } from '../api'

const stored = localStorage.getItem('user')

export const userState = reactive({
  token: localStorage.getItem('token') || '',
  user: stored ? JSON.parse(stored) : null
})

export function isLogin() {
  return !!userState.token
}

export function isAdmin() {
  return userState.user?.role === 'ADMIN'
}

export function setLogin(data) {
  userState.token = data.token
  userState.user = { id: data.id, username: data.username, nickname: data.nickname, role: data.role }
  localStorage.setItem('token', data.token)
  localStorage.setItem('user', JSON.stringify(userState.user))
}

export function logout() {
  userState.token = ''
  userState.user = null
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

export async function refreshMe() {
  if (!userState.token) return
  const res = await authApi.me()
  userState.user = res.data
  localStorage.setItem('user', JSON.stringify(res.data))
}
