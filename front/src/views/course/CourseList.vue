<template>
  <div class="course-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="课程代码">
          <el-input
            v-model="searchForm.courseCode"
            placeholder="请输入课程代码"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="授课教师">
          <el-select
            v-model="searchForm.teacherId"
            placeholder="请选择教师"
            clearable
            filterable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in teacherList"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
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
            添加课程
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
        <el-table-column type="selection" width="70" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="hours" label="学时" width="80" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="classTime" label="上课时间" width="200" />
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
                title="确定要删除该课程吗？"
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
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="formData.courseCode" placeholder="请输入课程代码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="formData.teacherId" placeholder="请选择教师" filterable style="width: 100%">
            <el-option
              v-for="item in teacherList"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="formData.credit" :min="0" :max="10" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学时" prop="hours">
          <el-input-number v-model="formData.hours" :min="0" :max="200" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="formData.semester" placeholder="如：2024-2025-1" />
        </el-form-item>
        <el-form-item label="教室" prop="classroom">
          <el-input v-model="formData.classroom" placeholder="请输入教室位置" />
        </el-form-item>
        <el-form-item label="上课时间" prop="classTime">
          <el-input v-model="formData.classTime" placeholder="如：周一 1-2节, 周三 3-4节" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
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
    <el-dialog v-model="detailVisible" title="课程详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程代码">{{ detailData.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ detailData.courseName }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ detailData.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ detailData.credit }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ detailData.hours }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ detailData.semester }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ detailData.classroom }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ detailData.classTime }}</el-descriptions-item>
        <el-descriptions-item label="课程描述" :span="2">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ detailData.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  View
} from '@element-plus/icons-vue'
import {
  getCourseList,
  createCourse,
  updateCourse,
  deleteCourse,
  batchDeleteCourses
} from '@/api/course'
import { getTeacherList } from '@/api/teacher'

const searchForm = reactive({
  courseName: '',
  courseCode: '',
  teacherId: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const teacherList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  courseCode: '',
  courseName: '',
  teacherId: null,
  credit: 0,
  hours: 0,
  semester: '',
  classroom: '',
  classTime: '',
  description: ''
})

const formRules = {
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' }
  ],
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  credit: [
    { required: true, message: '请输入学分', trigger: 'blur' }
  ]
}

const detailVisible = ref(false)
const detailData = ref({})

const loadCourseList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getCourseList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const loadTeachers = async () => {
  try {
    const res = await getTeacherList({ page: 1, size: 1000 })
    teacherList.value = res.data.records
  } catch (error) {
    console.error('加载教师列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCourseList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    courseName: '',
    courseCode: '',
    teacherId: null
  })
  handleSearch()
}

const handleSizeChange = () => {
  loadCourseList()
}

const handleCurrentChange = () => {
  loadCourseList()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加课程'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑课程'
  Object.assign(formData, {
    id: row.id,
    courseCode: row.courseCode,
    courseName: row.courseName,
    teacherId: row.teacherId,
    credit: row.credit,
    hours: row.hours,
    semester: row.semester,
    classroom: row.classroom,
    classTime: row.classTime,
    description: row.description
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(row.id)
      ElMessage.success('删除成功')
      loadCourseList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个课程吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteCourses(selectedIds.value)
      ElMessage.success('删除成功')
      loadCourseList()
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
      await updateCourse(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createCourse(formData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadCourseList()
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
    courseCode: '',
    courseName: '',
    teacherId: null,
    credit: 0,
    hours: 0,
    semester: '',
    classroom: '',
    classTime: '',
    description: ''
  })
}

onMounted(() => {
  loadCourseList()
  loadTeachers()
})
</script>

<style scoped>
.course-container {
  padding: 0;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 16px;
  border-radius: 8px;
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
</style>
