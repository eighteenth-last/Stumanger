<template>
  <div class="teacher-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="教师编号">
          <el-input
            v-model="searchForm.teacherNo"
            placeholder="请输入教师编号"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入姓名"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-select
            v-model="searchForm.department"
            placeholder="请选择部门"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in departmentList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select
            v-model="searchForm.title"
            placeholder="请选择职称"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in titleList"
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
            添加教师
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
        <el-table-column prop="teacherNo" label="教师编号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="department" label="部门" width="150" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="380" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'" size="small">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
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
                title="确定要删除该教师吗？"
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
        <el-form-item label="用户名" prop="username" v-if="!isEdit">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="教师编号" prop="teacherNo" v-if="!isEdit">
          <el-input v-model="formData.teacherNo" placeholder="请输入教师编号" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="formData.department" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="formData.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-switch v-model="formData.status" />
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
    <el-dialog v-model="detailVisible" title="教师详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="教师编号">{{ detailData.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detailData.department }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status ? 'success' : 'danger'">
            {{ detailData.status ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
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
  getTeacherList,
  createTeacher,
  updateTeacher,
  deleteTeacher,
  batchDeleteTeachers,
  getAllDepartments,
  getAllTitles
} from '@/api/teacher'

const searchForm = reactive({
  teacherNo: '',
  realName: '',
  department: null,
  title: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const departmentList = ref([])
const titleList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  username: '',
  password: '',
  realName: '',
  teacherNo: '',
  department: '',
  title: '',
  email: '',
  phone: '',
  status: true
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  teacherNo: [
    { required: true, message: '请输入教师编号', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ]
}

const detailVisible = ref(false)
const detailData = ref({})

const loadTeacherList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getTeacherList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载教师列表失败:', error)
    ElMessage.error('加载教师列表失败')
  } finally {
    loading.value = false
  }
}

const loadDepartments = async () => {
  try {
    const res = await getAllDepartments()
    departmentList.value = res.data
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

const loadTitles = async () => {
  try {
    const res = await getAllTitles()
    titleList.value = res.data
  } catch (error) {
    console.error('加载职称列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadTeacherList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    teacherNo: '',
    realName: '',
    department: null,
    title: null
  })
  handleSearch()
}

const handleSizeChange = () => {
  loadTeacherList()
}

const handleCurrentChange = () => {
  loadTeacherList()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加教师'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑教师'
  Object.assign(formData, {
    id: row.id,
    realName: row.realName,
    department: row.department,
    title: row.title,
    email: row.email,
    phone: row.phone,
    status: row.status
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该教师吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeacher(row.id)
      ElMessage.success('删除成功')
      loadTeacherList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个教师吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteTeachers(selectedIds.value)
      ElMessage.success('删除成功')
      loadTeacherList()
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
      await updateTeacher(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createTeacher(formData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadTeacherList()
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
    username: '',
    password: '',
    realName: '',
    teacherNo: '',
    department: '',
    title: '',
    email: '',
    phone: '',
    status: true
  })
}

onMounted(() => {
  loadTeacherList()
  loadDepartments()
  loadTitles()
})
</script>

<style scoped>
.teacher-container {
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
    width: 160px;
  }
  
  /* 输入框宽度 */
  .el-input {
    width: 160px;
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
