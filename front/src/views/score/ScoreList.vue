<template>
  <div class="score-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="学生">
          <el-select
            v-model="searchForm.studentId"
            placeholder="请选择学生"
            clearable
            filterable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in studentList"
              :key="item.id"
              :label="`${item.studentNo} - ${item.realName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select
            v-model="searchForm.courseId"
            placeholder="请选择课程"
            clearable
            filterable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in courseList"
              :key="item.id"
              :label="item.courseName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select
            v-model="searchForm.semester"
            placeholder="请选择学期"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in semesterList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            录入成绩
          </el-button>
          <el-button type="success" :icon="Upload" @click="handleImport">
            导入成绩
          </el-button>
          <el-button type="warning" :icon="Download" @click="handleExport">
            导出成绩
          </el-button>
          <el-button type="info" :icon="Download" @click="handleDownloadTemplate">
            下载模板
          </el-button>
          <el-button type="primary" :icon="DataAnalysis" @click="showStatistics">
            统计分析
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="semester" label="学期" width="150" />
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.score)">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examType" label="考试类型" width="100" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                link
                :icon="View"
                @click="handleView(row)"
              >
                查看
              </el-button>
              <el-button
                type="warning"
                size="small"
                link
                :icon="Edit"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-popconfirm
                title="确定要删除该成绩吗？"
                @confirm="handleDelete(row)"
              >
                <template #reference>
                  <el-button
                    type="danger"
                    size="small"
                    link
                    :icon="Delete"
                  >
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="formData.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option
              v-for="item in studentList"
              :key="item.id"
              :label="`${item.studentNo} - ${item.realName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="formData.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option
              v-for="item in courseList"
              :key="item.id"
              :label="item.courseName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="formData.semester" placeholder="如：2024-2025学年第一学期" />
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="formData.score" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="formData.examType" placeholder="请选择考试类型" style="width: 100%">
            <el-option label="期中考试" value="期中考试" />
            <el-option label="期末考试" value="期末考试" />
            <el-option label="平时成绩" value="平时成绩" />
            <el-option label="补考" value="补考" />
            <el-option label="重修" value="重修" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
            v-model="formData.examDate"
            type="date"
            placeholder="请选择考试日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="formData.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="成绩详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ detailData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="课程名称" :span="2">{{ detailData.courseName }}</el-descriptions-item>
        <el-descriptions-item label="学期" :span="2">{{ detailData.semester }}</el-descriptions-item>
        <el-descriptions-item label="成绩">
          <el-tag :type="getScoreType(detailData.score)" size="large">{{ detailData.score }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="考试类型">{{ detailData.examType }}</el-descriptions-item>
        <el-descriptions-item label="考试日期" :span="2">{{ detailData.examDate }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remarks || '无' }}</el-descriptions-item>
        <el-descriptions-item label="录入时间" :span="2">
          {{ detailData.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importVisible" title="导入成绩数据" width="500px">
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">
          确定导入
        </el-button>
      </template>
    </el-dialog>

    <!-- 统计分析对话框 -->
    <el-dialog v-model="statisticsVisible" title="成绩统计分析" width="900px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="课程统计" name="course">
          <el-form :inline="true">
            <el-form-item label="选择课程">
              <el-select v-model="statsCourseId" placeholder="请选择课程" filterable style="width: 300px">
                <el-option
                  v-for="item in courseList"
                  :key="item.id"
                  :label="item.courseName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCourseStatistics">查询</el-button>
            </el-form-item>
          </el-form>
          <div v-if="courseStats" class="stats-container">
            <div ref="courseChartRef1" class="stats-chart"></div>
            <div ref="courseChartRef2" class="stats-chart"></div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="学生统计" name="student">
          <el-form :inline="true">
            <el-form-item label="选择学生">
              <el-select v-model="statsStudentId" placeholder="请选择学生" filterable style="width: 300px">
                <el-option
                  v-for="item in studentList"
                  :key="item.id"
                  :label="`${item.studentNo} - ${item.realName}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadStudentStatistics">查询</el-button>
            </el-form-item>
          </el-form>
          <div v-if="studentStats" class="stats-container">
            <div ref="studentChartRef1" class="stats-chart"></div>
            <div ref="studentChartRef2" class="stats-chart"></div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  View,
  Upload,
  Download,
  UploadFilled,
  DataAnalysis
} from '@element-plus/icons-vue'
import {
  getScoreList,
  createScore,
  updateScore,
  deleteScore,
  batchDeleteScores,
  getAllSemesters,
  getCourseScoreStatistics,
  getStudentScoreStatistics,
  exportScores,
  downloadScoreTemplate
} from '@/api/score'
import { getStudentList } from '@/api/student'
import { getCourseList } from '@/api/course'

// 图表实例
const courseChartRef1 = ref(null)
const courseChartRef2 = ref(null)
const studentChartRef1 = ref(null)
const studentChartRef2 = ref(null)
let courseChartInstance1 = null
let courseChartInstance2 = null
let studentChartInstance1 = null
let studentChartInstance2 = null

const searchForm = reactive({
  studentId: null,
  courseId: null,
  semester: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const studentList = ref([])
const courseList = ref([])
const semesterList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  studentId: null,
  courseId: null,
  semester: '',
  score: 0,
  examType: '',
  examDate: '',
  remarks: ''
})

const formRules = {
  studentId: [
    { required: true, message: '请选择学生', trigger: 'change' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  semester: [
    { required: true, message: '请输入学期', trigger: 'blur' }
  ],
  score: [
    { required: true, message: '请输入成绩', trigger: 'blur' }
  ],
  examType: [
    { required: true, message: '请选择考试类型', trigger: 'change' }
  ]
}

const detailVisible = ref(false)
const detailData = ref({})
const importVisible = ref(false)
const importLoading = ref(false)
const uploadRef = ref()

const statisticsVisible = ref(false)
const activeTab = ref('course')
const statsCourseId = ref(null)
const statsStudentId = ref(null)
const courseStats = ref(null)
const studentStats = ref(null)

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

const loadScoreList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getScoreList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载成绩列表失败:', error)
    ElMessage.error('加载成绩列表失败')
  } finally {
    loading.value = false
  }
}

const loadStudents = async () => {
  try {
    const res = await getStudentList({ page: 1, size: 1000 })
    studentList.value = res.data.records
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

const loadCourses = async () => {
  try {
    const res = await getCourseList({ page: 1, size: 1000 })
    courseList.value = res.data.records
  } catch (error) {
    console.error('加载课程列表失败:', error)
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

const handleSearch = () => {
  pagination.page = 1
  loadScoreList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentId: null,
    courseId: null,
    semester: null
  })
  handleSearch()
}

const handleSizeChange = () => {
  loadScoreList()
}

const handleCurrentChange = () => {
  loadScoreList()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '录入成绩'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑成绩'
  Object.assign(formData, {
    id: row.id,
    studentId: row.studentId,
    courseId: row.courseId,
    semester: row.semester,
    score: row.score,
    examType: row.examType,
    examDate: row.examDate,
    remarks: row.remarks
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该成绩记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteScore(row.id)
      ElMessage.success('删除成功')
      loadScoreList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条成绩记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteScores(selectedIds.value)
      ElMessage.success('删除成功')
      loadScoreList()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateScore(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createScore(formData)
      ElMessage.success('录入成功')
    }
    
    dialogVisible.value = false
    loadScoreList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

const resetForm = () => {
  Object.assign(formData, {
    studentId: null,
    courseId: null,
    semester: '',
    score: 0,
    examType: '',
    examDate: '',
    remarks: ''
  })
}

const handleImport = () => {
  importVisible.value = true
}

const handleImportSubmit = async () => {
  const files = uploadRef.value?.uploadFiles
  if (!files || files.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', files[0].raw)
    
    // TODO: 调用导入API
    ElMessage.success('导入成功')
    importVisible.value = false
    loadScoreList()
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

const handleExport = async () => {
  try {
    const res = await exportScores(searchForm)
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `成绩数据_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const handleDownloadTemplate = async () => {
  try {
    const res = await downloadScoreTemplate()
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '成绩导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
  }
}

const showStatistics = () => {
  statisticsVisible.value = true
}

// 初始化课程统计图表1 - 柱状图
const initCourseChart1 = (stats) => {
  if (!courseChartRef1.value) return
  
  if (courseChartInstance1) {
    courseChartInstance1.dispose()
  }
  
  courseChartInstance1 = echarts.init(courseChartRef1.value)
  
  const option = {
    title: {
      text: '成绩分布',
      left: 'center',
      top: 5,
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '15%',
      right: '10%',
      bottom: '15%',
      top: '20%',
      containLabel: false
    },
    xAxis: {
      type: 'category',
      data: ['平均分', '最高分', '最低分'],
      axisLabel: {
        fontSize: 11
      }
    },
    yAxis: {
      type: 'value',
      name: '分数',
      max: 100,
      nameTextStyle: {
        fontSize: 11
      },
      axisLabel: {
        fontSize: 10
      }
    },
    series: [{
      data: [
        { value: stats.averageScore, itemStyle: { color: '#5470c6' } },
        { value: stats.maxScore, itemStyle: { color: '#91cc75' } },
        { value: stats.minScore, itemStyle: { color: '#fac858' } }
      ],
      type: 'bar',
      label: {
        show: true,
        position: 'top',
        formatter: '{c}',
        fontSize: 11
      },
      barWidth: '40%'
    }]
  }
  
  courseChartInstance1.setOption(option)
}

// 初始化课程统计图表2 - 饼图
const initCourseChart2 = (stats) => {
  if (!courseChartRef2.value) {
    console.error('courseChartRef2 不存在')
    return
  }
  
  if (courseChartInstance2) {
    courseChartInstance2.dispose()
  }
  
  courseChartInstance2 = echarts.init(courseChartRef2.value)
  
  const totalCount = stats.totalCount || 0
  const passRate = stats.passRate || 0
  const excellentRate = stats.excellentRate || 0
  
  const passCount = Math.round(totalCount * passRate / 100)
  const excellentCount = Math.round(totalCount * excellentRate / 100)
  const failCount = totalCount - passCount
  const goodCount = passCount - excellentCount
  
  console.log('饼图数据计算:', { totalCount, passRate, excellentRate, passCount, excellentCount, failCount, goodCount })
  
  const option = {
    title: {
      text: '成绩等级分布',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 10,
      textStyle: {
        fontSize: 11
      }
    },
    series: [{
      type: 'pie',
      radius: ['35%', '60%'],
      center: ['50%', '55%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        fontSize: 10,
        formatter: '{b}\n{c}人'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 12,
          fontWeight: 'bold'
        }
      },
      data: [
        { value: excellentCount, name: '优秀', itemStyle: { color: '#91cc75' } },
        { value: goodCount, name: '良好', itemStyle: { color: '#5470c6' } },
        { value: failCount, name: '不及格', itemStyle: { color: '#ee6666' } }
      ]
    }]
  }
  
  courseChartInstance2.setOption(option)
  
  console.log('课程饼图数据:', { excellentCount, goodCount, failCount, totalCount: stats.totalCount })
}

// 初始化学生统计图表1 - 柱状图
const initStudentChart1 = (stats) => {
  if (!studentChartRef1.value) return
  
  if (studentChartInstance1) {
    studentChartInstance1.dispose()
  }
  
  studentChartInstance1 = echarts.init(studentChartRef1.value)
  
  const option = {
    title: {
      text: '成绩分布',
      left: 'center',
      top: 5,
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '15%',
      right: '10%',
      bottom: '15%',
      top: '20%',
      containLabel: false
    },
    xAxis: {
      type: 'category',
      data: ['平均分', '最高分', '最低分'],
      axisLabel: {
        fontSize: 11
      }
    },
    yAxis: {
      type: 'value',
      name: '分数',
      max: 100,
      nameTextStyle: {
        fontSize: 11
      },
      axisLabel: {
        fontSize: 10
      }
    },
    series: [{
      data: [
        { value: stats.averageScore, itemStyle: { color: '#5470c6' } },
        { value: stats.maxScore, itemStyle: { color: '#91cc75' } },
        { value: stats.minScore, itemStyle: { color: '#fac858' } }
      ],
      type: 'bar',
      label: {
        show: true,
        position: 'top',
        formatter: '{c}',
        fontSize: 11
      },
      barWidth: '40%'
    }]
  }
  
  studentChartInstance1.setOption(option)
}

// 初始化学生统计图表2 - 雷达图
const initStudentChart2 = (stats) => {
  if (!studentChartRef2.value) return
  
  if (studentChartInstance2) {
    studentChartInstance2.dispose()
  }
  
  studentChartInstance2 = echarts.init(studentChartRef2.value)
  
  const option = {
    title: {
      text: '综合评估',
      left: 'center',
      top: 5,
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'item'
    },
    radar: {
      indicator: [
        { name: '平均分', max: 100 },
        { name: '最高分', max: 100 },
        { name: '已修课程', max: Math.max(stats.courseCount * 1.5, 10) },
        { name: '及格率', max: 100 },
        { name: '学习状态', max: 100 }
      ],
      radius: '60%',
      center: ['50%', '55%'],
      nameGap: 5,
      axisName: {
        fontSize: 11
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          stats.averageScore,
          stats.maxScore,
          stats.courseCount,
          stats.failedCount === 0 ? 100 : ((stats.courseCount - stats.failedCount) / stats.courseCount * 100).toFixed(1),
          stats.failedCount === 0 ? 100 : Math.max(100 - stats.failedCount * 10, 0)
        ],
        name: '学生表现',
        areaStyle: {
          color: 'rgba(84, 112, 198, 0.3)'
        },
        lineStyle: {
          color: '#5470c6'
        },
        itemStyle: {
          color: '#5470c6'
        }
      }]
    }]
  }
  
  studentChartInstance2.setOption(option)
}

const loadCourseStatistics = async () => {
  if (!statsCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  try {
    const res = await getCourseScoreStatistics(statsCourseId.value)
    courseStats.value = res.data
    console.log('课程统计数据:', res.data)
    await nextTick()
    setTimeout(() => {
      initCourseChart1(res.data)
      initCourseChart2(res.data)
    }, 100)
  } catch (error) {
    console.error('加载课程统计失败:', error)
    ElMessage.error('加载课程统计失败')
  }
}

const loadStudentStatistics = async () => {
  if (!statsStudentId.value) {
    ElMessage.warning('请选择学生')
    return
  }
  try {
    const res = await getStudentScoreStatistics(statsStudentId.value)
    studentStats.value = res.data
    await nextTick()
    initStudentChart1(res.data)
    initStudentChart2(res.data)
  } catch (error) {
    console.error('加载学生统计失败:', error)
    ElMessage.error('加载学生统计失败')
  }
}

const handleTabChange = () => {
  // Tab切换时重新调整图表大小
  setTimeout(() => {
    if (activeTab.value === 'course') {
      if (courseChartInstance1) courseChartInstance1.resize()
      if (courseChartInstance2) courseChartInstance2.resize()
    }
    if (activeTab.value === 'student') {
      if (studentChartInstance1) studentChartInstance1.resize()
      if (studentChartInstance2) studentChartInstance2.resize()
    }
  }, 100)
}

onMounted(() => {
  loadScoreList()
  loadStudents()
  loadCourses()
  loadSemesters()
})

onUnmounted(() => {
  if (courseChartInstance1) courseChartInstance1.dispose()
  if (courseChartInstance2) courseChartInstance2.dispose()
  if (studentChartInstance1) studentChartInstance1.dispose()
  if (studentChartInstance2) studentChartInstance2.dispose()
})
</script>

<style scoped>
.score-container {
  padding: 0;
}

.stats-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
  gap: 20px;
}

.stats-chart {
  width: 400px;
  height: 300px;
  flex-shrink: 0;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

/* 搜索表单优化 */
:deep(.search-card .el-form--inline) {
  .el-form-item {
    margin-right: 16px;
    margin-bottom: 0;
  }
  
  /* 统一选择框宽度 */
  .el-select {
    width: 200px;
  }
  
  /* 输入框宽度 */
  .el-input {
    width: 200px;
  }
  
  /* 日期选择器宽度 */
  .el-date-editor {
    width: 200px;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

/* 操作按钮容器 - 水平平铺 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

/* 操作列按钮样式优化 */
:deep(.el-table__body-wrapper) {
  .el-button.is-link {
    padding: 4px 8px;
    margin: 0;
  }
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>
