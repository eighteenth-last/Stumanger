<template>
  <div class="student-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="学号">
          <el-input
            v-model="searchForm.studentNo"
            placeholder="请输入学号"
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
        <el-form-item label="班级">
          <el-select
            v-model="searchForm.classId"
            placeholder="请选择班级"
            clearable
            @clear="handleSearch"
          >
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select
            v-model="searchForm.gender"
            placeholder="请选择性别"
            clearable
            @clear="handleSearch"
          >
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
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
            添加学生
          </el-button>
          <el-button
            type="success"
            :icon="Upload"
            @click="handleImport"
          >
            导入
          </el-button>
          <el-button
            type="warning"
            :icon="Download"
            @click="handleExport"
          >
            导出
          </el-button>
          <el-button
            type="info"
            :icon="Download"
            @click="handleDownloadTemplate"
          >
            下载模板
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
        border
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'MALE' ? 'primary' : 'danger'" size="small">
              {{ row.gender === 'MALE' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="250" />
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
                title="确定要删除该学生吗？"
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
        <el-form-item label="学号" prop="studentNo" v-if="!isEdit">
          <el-input v-model="formData.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="MALE">男</el-radio>
            <el-radio label="FEMALE">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="formData.classId" placeholder="请选择班级" clearable>
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="formData.birthDate"
            type="date"
            placeholder="请选择出生日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            placeholder="请输入地址"
          />
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
    <el-dialog v-model="detailVisible" title="学生详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ detailData.gender === 'MALE' ? '男' : '女' }}
        </el-descriptions-item>
        <el-descriptions-item label="班级">{{ detailData.className }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailData.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="2">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detailData.address }}</el-descriptions-item>
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

    <!-- 导入对话框 -->
    <el-dialog v-model="importVisible" title="导入学生数据" width="500px">
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
  View,
  Upload,
  Download,
  UploadFilled
} from '@element-plus/icons-vue'
import {
  getStudentList,
  createStudent,
  updateStudent,
  deleteStudent,
  batchDeleteStudents,
  exportStudents,
  downloadStudentTemplate
} from '@/api/student'
import { getClassList } from '@/api/class'

// 搜索表单
const searchForm = reactive({
  studentNo: '',
  realName: '',
  classId: null,
  gender: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])

// 班级列表
const classList = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  realName: '',
  studentNo: '',
  classId: null,
  gender: 'MALE',
  birthDate: '',
  email: '',
  phone: '',
  address: '',
  status: true
})

// 表单验证规则
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
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 导入对话框
const importVisible = ref(false)
const importLoading = ref(false)
const uploadRef = ref()

// 加载学生列表
const loadStudentList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getStudentList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载学生列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载班级列表
const loadClassList = async () => {
  try {
    const res = await getClassList({ page: 1, size: 100 })
    classList.value = res.data.records
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadStudentList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    studentNo: '',
    realName: '',
    classId: null,
    gender: null
  })
  handleSearch()
}

// 分页
const handleSizeChange = () => {
  loadStudentList()
}

const handleCurrentChange = () => {
  loadStudentList()
}

// 选择
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加学生'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑学生'
  Object.assign(formData, {
    id: row.id,
    realName: row.realName,
    classId: row.classId,
    gender: row.gender,
    birthDate: row.birthDate,
    email: row.email,
    phone: row.phone,
    address: row.address,
    status: row.status
  })
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadStudentList()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个学生吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteStudents(selectedIds.value)
      ElMessage.success('删除成功')
      loadStudentList()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateStudent(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createStudent(formData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadStudentList()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    username: '',
    password: '',
    realName: '',
    studentNo: '',
    classId: null,
    gender: 'MALE',
    birthDate: '',
    email: '',
    phone: '',
    address: '',
    status: true
  })
}

// 导入
const handleImport = () => {
  importVisible.value = true
}

// 导入提交
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
    loadStudentList()
  } catch (error) {
    console.error('导入失败:', error)
  } finally {
    importLoading.value = false
  }
}

// 导出
const handleExport = async () => {
  try {
    // 过滤掉空值参数
    const params = {}
    if (searchForm.classId) {
      params.classId = searchForm.classId
    }
    if (searchForm.studentNo && searchForm.studentNo.trim()) {
      params.studentNo = searchForm.studentNo.trim()
    }
    if (searchForm.realName && searchForm.realName.trim()) {
      params.realName = searchForm.realName.trim()
    }
    
    const res = await exportStudents(params)
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `学生数据_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败: ' + (error.message || '未知错误'))
  }
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const res = await downloadStudentTemplate()
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '学生导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadStudentList()
  loadClassList()
})
</script>

<style scoped>
.student-container {
  padding: 0;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

:deep(.search-card .el-card__body) {
  padding: 16px;
}

:deep(.toolbar-card .el-card__body) {
  padding: 12px 16px;
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

/* 搜索表单优化 */
:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 0;
}

/* 按钮组优化 */
.toolbar-left .el-button,
.toolbar-right .el-button {
  font-weight: 500;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
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

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  .el-button.is-link {
    opacity: 1;
  }
}

/* 状态标签样式 */
:deep(.el-tag) {
  font-weight: 500;
}
</style>
