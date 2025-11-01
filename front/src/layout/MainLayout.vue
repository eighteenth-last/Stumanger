<template>
  <div class="main-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="sidebar-container">
        <div class="logo-container">
          <h1 v-if="!appStore.sidebarCollapsed">学生管理系统</h1>
          <h1 v-else>SMS</h1>
        </div>
        <el-scrollbar class="sidebar-scrollbar">
          <el-menu
            :default-active="activeMenu"
            :collapse="appStore.sidebarCollapsed"
            :unique-opened="true"
            router
            class="sidebar-menu"
          >
            <!-- 调试信息 -->
            <div v-if="visibleMenuRoutes.length === 0" style="color: #fff; padding: 20px; text-align: center;">
              <p>没有可显示的菜单</p>
              <p style="font-size: 12px;">角色: {{ userStore.role }}</p>
              <p style="font-size: 12px;">路由数: {{ menuRoutes.length }}</p>
            </div>
            
            <template v-for="route in visibleMenuRoutes" :key="route.path">
              <el-menu-item :index="route.path">
                <el-icon>
                  <component :is="getIconComponent(route.meta.icon)" />
                </el-icon>
                <template #title>{{ route.meta.title }}</template>
              </el-menu-item>
            </template>
          </el-menu>
        </el-scrollbar>
        <div v-if="!appStore.sidebarCollapsed" class="sidebar-footer">
          <div class="version-info">v1.0.0</div>
        </div>
      </el-aside>

      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="header-container">
          <div class="header-left">
            <el-icon class="toggle-icon" @click="toggleSidebar">
              <Fold v-if="!appStore.sidebarCollapsed" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">
                <el-icon><HomeFilled /></el-icon>
                首页
              </el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentRoute.meta.title">
                {{ currentRoute.meta.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-tooltip content="刷新页面" placement="bottom">
              <el-icon class="header-icon" @click="handleRefresh">
                <Refresh />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="全屏" placement="bottom">
              <el-icon class="header-icon" @click="handleFullscreen">
                <FullScreen />
              </el-icon>
            </el-tooltip>
            <el-divider direction="vertical" />
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :icon="UserFilled" />
                <span class="username">{{ userStore.realName || userStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item disabled>
                    <el-tag :type="roleTagType" size="small">{{ roleText }}</el-tag>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="changePassword">
                    <el-icon><Lock /></el-icon>
                    修改密码
                  </el-dropdown-item>
                  <el-dropdown-item command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入旧密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  UserFilled,
  ArrowDown,
  Lock,
  SwitchButton,
  DataAnalysis,
  User,
  School,
  Reading,
  Document,
  HomeFilled,
  Refresh,
  FullScreen
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { changePassword } from '@/api/auth'

// 图标映射
const iconMap = {
  'DataAnalysis': DataAnalysis,
  'User': User,
  'UserFilled': UserFilled,
  'School': School,
  'Reading': Reading,
  'Document': Document
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const passwordDialogVisible = ref(false)
const passwordFormRef = ref()
const passwordLoading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 计算属性
const sidebarWidth = computed(() => appStore.sidebarCollapsed ? '64px' : '200px')
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

const menuRoutes = computed(() => {
  // 找到包含 MainLayout 的路由（有 children 的那个）
  const routes = router.options.routes
    .filter(r => r.path === '/')
    .find(r => r.children && r.children.length > 0)
    ?.children || []
  console.log('Menu routes:', routes)
  console.log('User role:', userStore.role)
  return routes
})

const visibleMenuRoutes = computed(() => {
  const filtered = menuRoutes.value.filter(route => {
    // 必须有 title 才显示
    if (!route.meta?.title) {
      console.log('Route without title:', route.path)
      return false
    }
    
    // 检查权限
    const hasAccess = hasPermission(route)
    console.log(`Route ${route.path} - has access:`, hasAccess, 'roles:', route.meta?.roles)
    return hasAccess
  })
  
  console.log('Visible routes:', filtered)
  return filtered
})

const roleText = computed(() => {
  const roleMap = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleMap[userStore.role] || userStore.role
})

const roleTagType = computed(() => {
  const typeMap = {
    'ADMIN': 'danger',
    'TEACHER': 'success',
    'STUDENT': 'primary'
  }
  return typeMap[userStore.role] || 'info'
})

// 方法
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const getIconComponent = (iconName) => {
  return iconMap[iconName] || DataAnalysis
}

const hasPermission = (route) => {
  if (!route.meta?.roles || route.meta.roles.length === 0) {
    return true
  }
  return route.meta.roles.includes(userStore.role)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'changePassword') {
    passwordDialogVisible.value = true
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
  }).catch(() => {})
}

const handleRefresh = () => {
  window.location.reload()
}

const handleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    
    // 清空表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    
    // 退出登录
    setTimeout(() => {
      userStore.logout()
    }, 1000)
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
  width: 100%;
}

.el-container {
  height: 100%;
}

/* 侧边栏样式 */
.sidebar-container {
  background: linear-gradient(180deg, #304156 0%, #2b3a4b 100%);
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-container h1 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
  letter-spacing: 1px;
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 10px 0;
}

:deep(.el-menu-item) {
  color: #bfcbd9;
  margin: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #fff;
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%) !important;
  color: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

:deep(.el-menu--collapse .el-menu-item) {
  margin: 4px 4px;
}

.sidebar-scrollbar {
  height: calc(100vh - 120px);
  flex: 1;
}

:deep(.sidebar-scrollbar .el-scrollbar__wrap) {
  overflow-x: hidden;
}

.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.version-info {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  text-align: center;
}

/* 顶部导航栏样式 */
.header-container {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toggle-icon {
  font-size: 45px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 3px;
  border-radius: 4px;
}

.toggle-icon:hover {
  color: var(--el-color-primary);
  background-color: var(--el-fill-color-light);
}

.breadcrumb {
  font-size: 14px;
}

:deep(.breadcrumb .el-breadcrumb__item) {
  display: flex;
  align-items: center;
}

:deep(.breadcrumb .el-icon) {
  margin-right: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-icon {
  font-size: 18px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.header-icon:hover {
  color: var(--el-color-primary);
  background-color: var(--el-fill-color-light);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 12px;
  border-radius: 6px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: var(--el-fill-color-light);
}

.username {
  font-size: 14px;
  color: var(--el-text-color-primary);
  font-weight: 500;
}

/* 主内容区样式 */
.main-content {
  background: #f0f2f5;
  padding: 0;
  overflow-y: auto;
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
  position: relative;
}

/* 内容区内部容器 */
:deep(.main-content > div) {
  padding: 20px;
}

/* 固定搜索栏 - 粘性定位 */
:deep(.search-card) {
  position: sticky;
  top: 20px;
  z-index: 10;
  background: #fff;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s;
}

/* 固定工具栏 - 粘性定位，在搜索栏下方 */
:deep(.toolbar-card) {
  position: sticky;
  top: 126px; /* 20px (top padding) + 90px (搜索栏高度) + 16px (间距) */
  z-index: 9;
  background: #fff;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s;
}

/* 表格卡片样式 */
:deep(.table-card) {
  margin-bottom: 16px;
  border-radius: 8px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  transition: background 0.3s;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-left {
    gap: 10px;
  }
  
  .username {
    display: none;
  }
}
</style>
