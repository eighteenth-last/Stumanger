<template>
  <div class="dashboard-wrapper">
    <div class="dashboard-container">
      <el-row :gutter="20">
        <!-- 统计卡片 -->
        <el-col :xs="24" :sm="12" :lg="6" v-for="item in statistics" :key="item.title">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" :style="{ background: item.color }">
                <el-icon :size="32"><component :is="item.icon" /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ item.value }}</div>
                <div class="stat-title">{{ item.title }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <!-- 成绩趋势图 -->
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>学生成绩趋势</span>
                <el-select
                  v-model="selectedStudentId"
                  placeholder="选择学生"
                  clearable
                  filterable
                  @change="loadScoreChart"
                  style="width: 200px; margin-left: 20px;"
                >
                  <el-option
                    v-for="student in studentList"
                    :key="student.id"
                    :label="student.realName || student.username"
                    :value="student.id"
                  />
                </el-select>
              </div>
            </template>
            <div ref="chartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <!-- 右侧信息栏 -->
        <el-col :xs="24" :lg="8">
          <!-- 系统信息 -->
          <el-card class="recent-activity-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>系统信息</span>
              </div>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="当前用户">
                {{ userStore.realName || userStore.username }}
              </el-descriptions-item>
              <el-descriptions-item label="用户角色">
                <el-tag :type="roleTagType">{{ roleText }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="登录时间">
                {{ loginTime }}
              </el-descriptions-item>
              <el-descriptions-item label="系统版本">
                v1.0.0
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
          
          <!-- 快捷操作 -->
          <el-card class="quick-actions-card mt-20" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>快捷操作</span>
              </div>
            </template>
            <div class="quick-actions">
              <el-button
                v-for="action in quickActions"
                :key="action.name"
                :type="action.type"
                :icon="action.icon"
                @click="handleQuickAction(action.path)"
                class="action-button"
              >
                {{ action.name }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 欢迎信息 - 固定在底部 -->
    <div class="welcome-footer">
      <el-card class="welcome-card" shadow="hover">
        <div class="welcome-content">
          <h2>欢迎使用学生管理系统</h2>
          <p>这是一个功能完善的学生信息管理平台，支持学生、教师、班级、课程和成绩的全面管理。</p>
          <div class="features">
            <div class="feature-item">
              <el-icon color="#fff"><Check /></el-icon>
              <span>学生信息管理</span>
            </div>
            <div class="feature-item">
              <el-icon color="#fff"><Check /></el-icon>
              <span>成绩统计分析</span>
            </div>
            <div class="feature-item">
              <el-icon color="#fff"><Check /></el-icon>
              <span>Excel导入导出</span>
            </div>
            <div class="feature-item">
              <el-icon color="#fff"><Check /></el-icon>
              <span>权限角色管理</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'
import {
  User,
  UserFilled,
  School,
  Reading,
  Document,
  Check
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 图表相关
const chartRef = ref(null)
let chartInstance = null
const selectedStudentId = ref(null)
const studentList = ref([])
const scoreData = ref([])

const statistics = ref([
  {
    title: '学生总数',
    value: '0',
    icon: markRaw(User),
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '教师总数',
    value: '0',
    icon: markRaw(UserFilled),
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '班级总数',
    value: '0',
    icon: markRaw(School),
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '课程总数',
    value: '0',
    icon: markRaw(Reading),
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

const quickActions = computed(() => {
  const actions = []
  
  if (userStore.isAdmin || userStore.isTeacher) {
    actions.push(
      { name: '添加学生', type: 'primary', icon: User, path: '/students' },
      { name: '录入成绩', type: 'success', icon: Document, path: '/scores' },
      { name: '管理班级', type: 'warning', icon: School, path: '/classes' }
    )
  }
  
  if (userStore.isStudent) {
    actions.push(
      { name: '查看成绩', type: 'primary', icon: Document, path: '/my-scores' }
    )
  }
  
  return actions
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

const loginTime = ref(new Date().toLocaleString())

const handleQuickAction = (path) => {
  router.push(path)
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 确保 token 已经加载
    const token = localStorage.getItem('token')
    if (!token) {
      console.warn('未找到 token，跳过加载统计数据')
      return
    }
    
    // 使用现有的列表接口获取总数
    const [studentRes, teacherRes, classRes, courseRes] = await Promise.all([
      import('@/api/student').then(m => m.getStudentList({ page: 1, size: 1 })),
      import('@/api/teacher').then(m => m.getTeacherList({ page: 1, size: 1 })),
      import('@/api/class').then(m => m.getClassList({ page: 1, size: 1 })),
      import('@/api/course').then(m => m.getCourseList({ page: 1, size: 1 }))
    ])
    
    // 更新统计数据
    statistics.value[0].value = studentRes.data.total.toString()
    statistics.value[1].value = teacherRes.data.total.toString()
    statistics.value[2].value = classRes.data.total.toString()
    statistics.value[3].value = courseRes.data.total.toString()
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 如果是 401 错误，不显示错误消息（已经在拦截器中处理）
    if (error.response?.status !== 401) {
      console.log('统计数据加载失败，将显示默认值')
    }
  }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: [],
      top: 5,
      type: 'scroll',
      pageIconSize: 12,
      pageTextStyle: {
        fontSize: 11
      }
    },
    grid: {
      left: '60px',
      right: '40px',
      bottom: '35px',
      top: '45px',
      containLabel: false
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: [],
      axisLabel: {
        rotate: 30,
        fontSize: 10,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      name: '分数',
      min: 0,
      max: 100,
      nameTextStyle: {
        fontSize: 12
      },
      axisLabel: {
        fontSize: 11
      }
    },
    series: []
  }
  
  chartInstance.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 加载学生列表
const loadStudentList = async () => {
  try {
    const { getStudentList } = await import('@/api/student')
    const res = await getStudentList({ page: 1, size: 100 })
    studentList.value = res.data.records || []
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

// 加载成绩图表数据
const loadScoreChart = async () => {
  try {
    if (!selectedStudentId.value) {
      // 如果没有选择学生，显示所有学生的平均成绩
      await loadAllStudentsScores()
      return
    }
    
    const { getScoreList } = await import('@/api/score')
    const res = await getScoreList({ 
      studentId: selectedStudentId.value,
      page: 1, 
      size: 100 
    })
    
    const scores = res.data.records || []
    
    // 按课程分组
    const courseMap = new Map()
    scores.forEach(score => {
      const courseName = score.courseName || '未知课程'
      if (!courseMap.has(courseName)) {
        courseMap.set(courseName, [])
      }
      courseMap.get(courseName).push({
        date: score.examDate || score.createTime,
        score: score.score
      })
    })
    
    // 准备图表数据
    const allDates = new Set()
    courseMap.forEach(scores => {
      scores.forEach(item => {
        if (item.date) {
          allDates.add(item.date.split(' ')[0])
        }
      })
    })
    
    const sortedDates = Array.from(allDates).sort()
    
    const series = []
    const legendData = []
    
    courseMap.forEach((scores, courseName) => {
      legendData.push(courseName)
      
      const dateScoreMap = new Map()
      scores.forEach(item => {
        const date = item.date ? item.date.split(' ')[0] : ''
        dateScoreMap.set(date, item.score)
      })
      
      const data = sortedDates.map(date => dateScoreMap.get(date) || null)
      
      series.push({
        name: courseName,
        type: 'line',
        data: data,
        smooth: true,
        connectNulls: true
      })
    })
    
    if (chartInstance) {
      chartInstance.setOption({
        legend: {
          data: legendData,
          top: 5,
          type: 'scroll'
        },
        xAxis: {
          data: sortedDates.length > 0 ? sortedDates : ['暂无数据']
        },
        series: series.length > 0 ? series : [{
          name: '暂无数据',
          type: 'line',
          data: []
        }]
      }, true)
    }
  } catch (error) {
    console.error('加载成绩图表失败:', error)
  }
}

// 加载所有学生的平均成绩
const loadAllStudentsScores = async () => {
  try {
    const { getScoreList } = await import('@/api/score')
    const res = await getScoreList({ page: 1, size: 1000 })
    
    const scores = res.data.records || []
    
    if (scores.length === 0) {
      // 没有数据时显示空图表
      if (chartInstance) {
        chartInstance.setOption({
          title: {
            show: true,
            text: '暂无成绩数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#999',
              fontSize: 14
            }
          },
          xAxis: {
            data: ['暂无数据']
          },
          yAxis: {
            type: 'value',
            name: '分数',
            min: 0,
            max: 100
          },
          series: []
        }, true)
      }
      return
    }
    
    // 按课程和日期分组计算平均分
    const courseMap = new Map()
    scores.forEach(score => {
      const courseName = score.courseName || '未知课程'
      const date = score.examDate ? score.examDate.split(' ')[0] : (score.createTime ? score.createTime.split(' ')[0] : '')
      
      if (!date) return
      
      const key = `${courseName}_${date}`
      if (!courseMap.has(key)) {
        courseMap.set(key, { courseName, date, scores: [], count: 0 })
      }
      const item = courseMap.get(key)
      item.scores.push(score.score)
      item.count++
    })
    
    // 计算平均分
    const courseAvgMap = new Map()
    courseMap.forEach((value, key) => {
      const { courseName, date, scores } = value
      const avg = scores.reduce((sum, s) => sum + s, 0) / scores.length
      
      if (!courseAvgMap.has(courseName)) {
        courseAvgMap.set(courseName, [])
      }
      courseAvgMap.get(courseName).push({ date, score: avg })
    })
    
    // 准备图表数据
    const allDates = new Set()
    courseAvgMap.forEach(scores => {
      scores.forEach(item => allDates.add(item.date))
    })
    
    const sortedDates = Array.from(allDates).sort()
    
    const series = []
    const legendData = []
    
    courseAvgMap.forEach((scores, courseName) => {
      legendData.push(courseName)
      
      const dateScoreMap = new Map()
      scores.forEach(item => {
        dateScoreMap.set(item.date, item.score)
      })
      
      const data = sortedDates.map(date => {
        const score = dateScoreMap.get(date)
        return score ? Math.round(score * 10) / 10 : null
      })
      
      series.push({
        name: courseName,
        type: 'line',
        data: data,
        smooth: true,
        connectNulls: true
      })
    })
    
    if (chartInstance) {
      chartInstance.setOption({
        title: {
          show: false
        },
        legend: {
          data: legendData,
          top: 5,
          type: 'scroll'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: sortedDates,
          axisLabel: {
            rotate: 30,
            fontSize: 10,
            interval: 0
          }
        },
        yAxis: {
          type: 'value',
          name: '分数',
          min: 0,
          max: 100,
          nameTextStyle: {
            fontSize: 12
          },
          axisLabel: {
            fontSize: 11
          }
        },
        series: series
      }, true)
    }
  } catch (error) {
    console.error('加载平均成绩图表失败:', error)
  }
}

onMounted(() => {
  // 添加一个小延迟，确保 token 已经保存
  setTimeout(async () => {
    loadStatistics()
    loadStudentList()
    
    await nextTick()
    initChart()
    loadAllStudentsScores()
  }, 100)
})

onUnmounted(() => {
  if (chartInstance) {
    window.removeEventListener('resize', handleResize)
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.dashboard-wrapper {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 60px);
}

.dashboard-container {
  flex: 1;
  padding: 0;
  padding-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.stat-card :deep(.el-card__body) {
  padding: 15px;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.recent-activity-card :deep(.el-card__header),
.quick-actions-card :deep(.el-card__header) {
  padding: 10px 15px;
}

.recent-activity-card :deep(.el-card__body),
.quick-actions-card :deep(.el-card__body) {
  padding: 10px 15px;
}

.recent-activity-card :deep(.el-descriptions) {
  font-size: 13px;
}

.recent-activity-card :deep(.el-descriptions__label) {
  width: 80px;
  font-size: 13px;
  padding: 8px 10px;
}

.recent-activity-card :deep(.el-descriptions__content) {
  font-size: 13px;
  padding: 8px 10px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.action-button {
  flex: 1;
  min-width: 120px;
}

.welcome-footer {
  margin-top: auto;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 0;
}

.welcome-card :deep(.el-card__body) {
  padding: 18px 30px;
}

.welcome-content h2 {
  color: #fff;
  margin-bottom: 8px;
  font-size: 18px;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
  line-height: 1.4;
  margin-bottom: 12px;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 13px;
}

.chart-card :deep(.el-card__header) {
  padding: 12px 20px;
}

.chart-card :deep(.el-card__body) {
  padding: 15px 20px;
}

.chart-card .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-container {
  width: 100%;
  height: 320px;
  min-height: 320px;
}

.mt-20 {
  margin-top: 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .quick-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style>
