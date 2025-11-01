import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表板', icon: 'DataAnalysis' }
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('@/views/student/StudentList.vue'),
        meta: { title: '学生管理', icon: 'User', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'teachers',
        name: 'Teachers',
        component: () => import('@/views/teacher/TeacherList.vue'),
        meta: { title: '教师管理', icon: 'UserFilled', roles: ['ADMIN'] }
      },
      {
        path: 'classes',
        name: 'Classes',
        component: () => import('@/views/class/ClassList.vue'),
        meta: { title: '班级管理', icon: 'School', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/course/CourseList.vue'),
        meta: { title: '课程管理', icon: 'Reading', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'scores',
        name: 'Scores',
        component: () => import('@/views/score/ScoreList.vue'),
        meta: { title: '成绩管理', icon: 'Document', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'my-scores',
        name: 'MyScores',
        component: () => import('@/views/score/MyScores.vue'),
        meta: { title: '我的成绩', icon: 'Document', roles: ['STUDENT'] }
      },
      {
        path: 'api-doc',
        name: 'ApiDoc',
        component: () => import('@/views/api-doc/ApiDocView.vue'),
        meta: { title: 'API文档', icon: 'Document', roles: ['ADMIN', 'TEACHER'] }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生管理系统` : '学生管理系统'

  if (requiresAuth) {
    // 需要登录
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 检查角色权限
      if (to.meta.roles && to.meta.roles.length > 0) {
        if (to.meta.roles.includes(userStore.role)) {
          next()
        } else {
          ElMessage.error('您没有权限访问该页面')
          next('/403')
        }
      } else {
        next()
      }
    }
  } else {
    // 不需要登录
    if (to.path === '/login' && userStore.isLoggedIn) {
      // 已登录用户访问登录页，重定向到首页
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router