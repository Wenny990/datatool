<template>
  <div class="http-headers-config">
    <div class="header-toolbar">
      <span class="label">请求头配置</span>
      <el-button size="small" type="primary" @click="addHeader">
        <el-icon><Plus /></el-icon>
        添加请求头
      </el-button>
    </div>

    <div class="headers-list" v-if="headers && headers.length > 0">
      <div 
        v-for="(header, index) in headers" 
        :key="index" 
        class="header-item">
        
        <el-checkbox 
          v-model="header.enabled" 
          @change="emitChange"
          class="header-enabled">
        </el-checkbox>

        <el-input
          v-model="header.key"
          placeholder="请求头名称"
          @input="emitChange"
          class="header-key">
        </el-input>

        <el-input
          v-model="header.value"
          placeholder="请求头值，支持${param}占位符"
          @input="emitChange"
          class="header-value">
        </el-input>

        <el-input
          v-model="header.description"
          placeholder="描述（可选）"
          @input="emitChange"
          class="header-description">
        </el-input>

        <el-button 
          size="small" 
          type="danger" 
          text
          @click="removeHeader(index)"
          class="header-remove">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无请求头配置</p>
      <el-button type="primary" text @click="addHeader">
        <el-icon><Plus /></el-icon>
        添加第一个请求头
      </el-button>
    </div>

    <!-- 常用请求头快捷添加 -->
    <div class="common-headers">
      <span class="label">常用请求头：</span>
      <el-button 
        v-for="commonHeader in commonHeaders"
        :key="commonHeader.key"
        size="small"
        text
        @click="addCommonHeader(commonHeader)">
        {{ commonHeader.label }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const headers = ref(props.modelValue || [])

// 常用请求头
const commonHeaders = ref([
  { label: 'Content-Type: JSON', key: 'Content-Type', value: 'application/json' },
  { label: 'Content-Type: Form', key: 'Content-Type', value: 'application/x-www-form-urlencoded' },
  { label: 'Accept: JSON', key: 'Accept', value: 'application/json' },
  { label: 'Authorization', key: 'Authorization', value: 'Bearer ${token}' },
  { label: 'User-Agent', key: 'User-Agent', value: 'DataTool-API-Client/1.0' }
])

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  headers.value = newVal || []
}, { deep: true, immediate: true })

// 添加请求头
const addHeader = () => {
  headers.value.push({
    key: '',
    value: '',
    enabled: true,
    description: ''
  })
  emitChange()
}

// 添加常用请求头
const addCommonHeader = (commonHeader) => {
  headers.value.push({
    key: commonHeader.key,
    value: commonHeader.value,
    enabled: true,
    description: ''
  })
  emitChange()
}

// 删除请求头
const removeHeader = (index) => {
  headers.value.splice(index, 1)
  emitChange()
}

// 发送变更事件
const emitChange = () => {
  emit('update:modelValue', headers.value)
}
</script>

<style scoped>
.http-headers-config {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 16px;
}

.header-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.label {
  font-weight: 600;
  color: #303133;
}

.headers-list {
  margin-bottom: 16px;
}

.header-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.header-enabled {
  flex-shrink: 0;
}

.header-key {
  width: 150px;
  flex-shrink: 0;
}

.header-value {
  flex: 1;
  min-width: 200px;
}

.header-description {
  width: 150px;
  flex-shrink: 0;
}

.header-remove {
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 20px;
  color: #909399;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.common-headers {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  flex-wrap: wrap;
}

.common-headers .label {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}
</style>