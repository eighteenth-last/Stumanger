<template>
  <div class="class-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="班级名称">
          <el-input
            v-model="searchForm.className"
            placeholder="请输入班级名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="专业">
          <el-select
            v-model="searchForm.major"
            placeholder="请选择专业"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in majorList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select
            v-model="searchForm.grade"
            placeholder="请选择年级"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in gradeList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入学年份">
          <el-date-picker
            v-model="searchForm.enrollYear"
            type="year"
            placeholder="请选择入学年份"
            value-format="YYYY"
            clearable
            @clear="handleSearch"
          />
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
            添加班级
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
        <el-table-column prop="className" label="班级名称" width="150" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="enrollYear" label="入学年份" width="120" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="classTeacherName" label="班主任" width="120" />
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
                title="确定要删除该班级吗？"
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
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="formData.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="formData.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="formData.grade" placeholder="请输入年级，如：大一" />
        </el-form-item>
        <el-form-item label="入学年份" prop="enrollYear">
          <el-date-picker
            v-model="formData.enrollYear"
            type="year"
            placeholder="请选择入学年份"
            value-format="YYYY"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="班主任" prop="classTeacherId">
          <el-select v-model="formData.classTeacherId" placeholder="请选择班主任" clearable filterable style="width: 100%">
            <el-option
              v-for="item in teacherList"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
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
    <el-dialog v-model="detailVisible" title="班级详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="班级名称">{{ detailData.className }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detailData.major }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ detailData.grade }}</el-descriptions-item>
        <el-descriptions-item label="入学年份">{{ detailData.enrollYear }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ detailData.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="班主任">{{ detailData.classTeacherName }}</el-descriptions-item>
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
  getClassList,
  createClass,
  updateClass,
  deleteClass,
  batchDeleteClasses,
  getAllMajors,
  getAllGrades
} from '@/api/class'
import { getTeacherList } from '@/api/teacher'

const searchForm = reactive({
  className: '',
  major: null,
  grade: null,
  enrollYear: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const majorList = ref([])
const gradeList = ref([])
const teacherList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  className: '',
  major: '',
  grade: '',
  enrollYear: '',
  classTeacherId: null
})

const formRules = {
  className: [
    { required: true, message: '请输入班级名称', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  enrollYear: [
    { required: true, message: '请选择入学年份', trigger: 'change' }
  ]
}

const detailVisible = ref(false)
const detailData = ref({})

const loadClassList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getClassList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载班级列表失败:', error)
    ElMessage.error('加载班级列表失败')
  } finally {
    loading.value = false
  }
}

const loadMajors = async () => {
  try {
    const res = await getAllMajors()
    majorList.value = res.data
  } catch (error) {
    console.error('加载专业列表失败:', error)
  }
}

const loadGrades = async () => {
  try {
    const res = await getAllGrades()
    gradeList.value = res.data
  } catch (error) {
    console.error('加载年级列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadClassList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    className: '',
    major: null,
    grade: null,
    enrollYear: null
  })
  handleSearch()
}

const handleSizeChange = () => {
  loadClassList()
}

const handleCurrentChange = () => {
  loadClassList()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加班级'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑班级'
  Object.assign(formData, {
    id: row.id,
    className: row.className,
    major: row.major,
    grade: row.grade,
    enrollYear: row.enrollYear,
    classTeacherId: row.classTeacherId
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该班级吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteClass(row.id)
      ElMessage.success('删除成功')
      loadClassList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个班级吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteClasses(selectedIds.value)
      ElMessage.success('删除成功')
      loadClassList()
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
      await updateClass(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createClass(formData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadClassList()
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
    className: '',
    major: '',
    grade: '',
    enrollYear: '',
    classTeacherId: null
  })
}

const loadTeachers = async () => {
  try {
    const res = await getTeacherList({ page: 1, size: 1000 })
    teacherList.value = res.data.records
  } catch (error) {
    console.error('加载教师列表失败:', error)
  }
}

onMounted(() => {
  loadClassList()
  loadMajors()
  loadGrades()
  loadTeachers()
})
</script>

<style scoped>
.class-container {
  padding: 0;
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
    width: 180px;
  }
  
  /* 输入框宽度 */
  .el-input {
    width: 180px;
  }
  
  /* 日期选择器宽度 */
  .el-date-editor {
    width: 180px;
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
</style>
