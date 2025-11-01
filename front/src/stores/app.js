import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    // 侧边栏是否折叠
    sidebarCollapsed: false,
    // 设备类型
    device: 'desktop',
    // 加载状态
    loading: false
  }),
  
  getters: {
    isMobile: (state) => state.device === 'mobile'
  },
  
  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    
    closeSidebar() {
      this.sidebarCollapsed = true
    },
    
    openSidebar() {
      this.sidebarCollapsed = false
    },
    
    setDevice(device) {
      this.device = device
    },
    
    setLoading(loading) {
      this.loading = loading
    }
  }
})
