<template>
  <div class="my-scores-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.courseCount || 0 }}</div>
              <div class="stat-title">已修课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageScore || 0 }}</div>
              <div class="stat-title">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon :size="32"><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.maxScore || 0 }}</div>
              <div class="stat-title">最高分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              <el-icon :size="32"><Medal /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalCredits || 0 }}</div>
              <div class="stat-title">总学分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" :inline="true">
        <el-form-item label="学期">
          <el-select
            v-model="filterForm.semester"
            placeholder="请选择学期"
            clearable
            @change="loadMyScores"
          >
            <el-option
              v-for="item in semesterList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-input
            v-model="filterForm.courseName"
            placeholder="请输入课程名称"
            clearable
            @clear="loadMyScores"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadMyScores">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 成绩列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的成绩</span>
          <el-button type="primary" :icon="Download" @click="handleExport">
            导出成绩单
          </el-button>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
      >
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="180" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.score)" size="large">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examType" label="考试类型" width="120" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column label="绩点" width="80">
          <template #default="{ row }">
            {{ calculateGPA(row.score) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Document,
  TrendCharts,
  Trophy,
  Medal
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getScoresByStudent,
  getStudentScoreStatistics,
  getAllSemesters,
  exportScores
} from '@/api/score'

const userStore = useUserStore()

const filterForm = reactive({
  semester: null,
  courseName: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)
const semesterList = ref([])
const statistics = ref({})

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

const calculateGPA = (score) => {
  if (score >= 90) return '4.0'
  if (score >= 85) return '3.7'
  if (score >= 82) return '3.3'
  if (score >= 78) return '3.0'
  if (score >= 75) return '2.7'
  if (score >= 72) return '2.3'
  if (score >= 68) return '2.0'
  if (score >= 64) return '1.5'
  if (score >= 60) return '1.0'
  return '0.0'
}

const loadMyScores = async () => {
  loading.value = true
  try {
    // 获取当前登录用户的学生ID
    const studentId = userStore.userInfo?.userId
    if (!studentId) {
      ElMessage.error('无法获取学生信息')
      return
    }

    const res = await getScoresByStudent(studentId)
    let scores = res.data || []

    // 前端筛选
    if (filterForm.semester) {
      scores = scores.filter(item => item.semester === filterForm.semester)
    }
    if (filterForm.courseName) {
      scores = scores.filter(item => 
        item.courseName.includes(filterForm.courseName)
      )
    }

    // 分页
    pagination.total = scores.length
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = scores.slice(start, end)
  } catch (error) {
    console.error('加载成绩失败:', error)
    ElMessage.error('加载成绩失败')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const studentId = userStore.userInfo?.userId
    if (!studentId) return

    const res = await getStudentScoreStatistics(studentId)
    statistics.value = res.data || {}
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadSemesters = async () => {
  try {
    const res = await getAllSemesters()
    semesterList.value = res.data
  } catch (error) {
    console.error('加载学期列表失败:', error)
  }
}

const handleReset = () => {
  Object.assign(filterForm, {
    semester: null,
    courseName: ''
  })
  pagination.page = 1
  loadMyScores()
}

const handleSizeChange = () => {
  loadMyScores()
}

const handleCurrentChange = () => {
  loadMyScores()
}

const handleExport = async () => {
  try {
    const studentId = userStore.userInfo?.userId
    const res = await exportScores({ studentId })
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `我的成绩单_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadMyScores()
  loadStatistics()
  loadSemesters()
})
</script>

<style scoped>
.my-scores-container {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
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

.filter-card,
.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
