import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    roles: []
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.realName || '',
    role: (state) => state.userInfo?.role || '',
    isAdmin: (state) => state.userInfo?.role === 'ADMIN',
    isTeacher: (state) => state.userInfo?.role === 'TEACHER',
    isStudent: (state) => state.userInfo?.role === 'STUDENT'
  },
  
  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        const { token, username, realName, role, userId } = res.data
        
        this.token = token
        this.userInfo = { username, realName, role, userId }
        
        // 保存到 localStorage
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        
        return res
      } catch (error) {
        throw error
      }
    },
    
    // 登出
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return res.data
      } catch (error) {
        throw error
      }
    },
    
    // 清除用户信息
    clearUserInfo() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
