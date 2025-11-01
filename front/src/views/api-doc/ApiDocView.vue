<template>
  <div class="api-doc-container">
    <div v-if="error" class="error-container">
      <el-icon :size="60" color="#F56C6C">
        <CircleClose />
      </el-icon>
      <p class="error-message">{{ error }}</p>
      <el-button type="primary" @click="reloadDoc">重新加载</el-button>
      <el-button @click="openInNewTab">在新标签页打开</el-button>
    </div>
    <iframe
      v-show="!error"
      ref="iframeRef"
      :src="docUrl"
      class="api-doc-iframe"
      frameborder="0"
      @load="handleIframeLoad"
    ></iframe>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Loading, CircleClose } from '@element-plus/icons-vue'

const iframeRef = ref(null)
const error = ref(null)

// 获取API文档URL
const docUrl = 'http://localhost:8080/doc.html'

// iframe加载完成
const handleIframeLoad = () => {
  console.log('iframe加载完成')
  error.value = null
}

// 重新加载文档
const reloadDoc = () => {
  error.value = null
  if (iframeRef.value) {
    iframeRef.value.src = docUrl + '?t=' + Date.now()
  }
}

// 在新标签页打开
const openInNewTab = () => {
  window.open(docUrl, '_blank')
}

onMounted(() => {
  console.log('API文档页面挂载，文档URL:', docUrl)
  
  // 检查后端是否可访问
  fetch(docUrl, { method: 'HEAD' })
    .then(response => {
      if (!response.ok) {
        error.value = `后端服务返回错误: ${response.status}`
      }
    })
    .catch(err => {
      console.error('无法连接到后端服务:', err)
      error.value = '无法连接到后端服务，请确保后端服务正在运行'
    })
})
</script>

<style scoped>
.api-doc-container {
  width: 100%;
  height: calc(100vh - 60px);
  overflow: hidden;
  position: relative;
  background-color: #f5f5f5;
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 20px;
}

.error-container {
  padding: 40px;
}

.error-message {
  font-size: 16px;
  color: #606266;
  text-align: center;
  margin: 20px 0;
}

.api-doc-iframe {
  width: 100%;
  height: 100%;
  border: none;
  background-color: #fff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .api-doc-container {
    height: calc(100vh - 50px);
  }
}
</style>
