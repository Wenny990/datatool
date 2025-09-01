<template>
  <div class="http-query-params-config">
    <div class="params-toolbar">
      <span class="label">查询参数配置</span>
      <el-button size="small" type="primary" @click="addParam">
        <el-icon><Plus /></el-icon>
        添加参数
      </el-button>
    </div>

    <div class="params-list" v-if="params && params.length > 0">
      <div 
        v-for="(param, index) in params" 
        :key="index" 
        class="param-item">
        
        <el-checkbox 
          v-model="param.enabled" 
          @change="emitChange"
          class="param-enabled">
        </el-checkbox>

        <el-input
          v-model="param.key"
          placeholder="参数名称"
          @input="emitChange"
          class="param-key">
        </el-input>

        <el-input
          v-model="param.value"
          placeholder="参数值，支持${param}占位符"
          @input="emitChange"
          class="param-value">
        </el-input>

        <el-input
          v-model="param.description"
          placeholder="描述（可选）"
          @input="emitChange"
          class="param-description">
        </el-input>

        <el-button 
          size="small" 
          type="danger" 
          text
          @click="removeParam(index)"
          class="param-remove">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无查询参数配置</p>
      <el-button type="primary" text @click="addParam">
        <el-icon><Plus /></el-icon>
        添加第一个参数
      </el-button>
    </div>

    <!-- 批量导入 -->
    <div class="batch-import">
      <el-button size="small" text @click="showBatchImport = !showBatchImport">
        <el-icon><Upload /></el-icon>
        批量导入
      </el-button>
      
      <div v-if="showBatchImport" class="batch-import-panel">
        <el-input
          v-model="batchText"
          type="textarea"
          :rows="3"
          placeholder="支持URL参数格式：param1=value1&param2=value2"
          class="batch-textarea">
        </el-input>
        <div class="batch-actions">
          <el-button size="small" type="primary" @click="importBatchParams">导入</el-button>
          <el-button size="small" @click="showBatchImport = false">取消</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus, Delete, Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const params = ref(props.modelValue || [])
const showBatchImport = ref(false)
const batchText = ref('')

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  params.value = newVal || []
}, { deep: true, immediate: true })

// 添加参数
const addParam = () => {
  params.value.push({
    key: '',
    value: '',
    enabled: true,
    description: ''
  })
  emitChange()
}

// 删除参数
const removeParam = (index) => {
  params.value.splice(index, 1)
  emitChange()
}

// 批量导入参数
const importBatchParams = () => {
  try {
    const text = batchText.value.trim()
    if (!text) {
      ElMessage.warning('请输入要导入的参数')
      return
    }

    // 解析URL参数格式
    const urlParams = new URLSearchParams(text)
    const importedParams = []
    
    for (const [key, value] of urlParams) {
      importedParams.push({
        key,
        value,
        enabled: true,
        description: ''
      })
    }

    if (importedParams.length > 0) {
      params.value.push(...importedParams)
      emitChange()
      batchText.value = ''
      showBatchImport.value = false
      ElMessage.success(`成功导入 ${importedParams.length} 个参数`)
    } else {
      ElMessage.warning('未解析到有效参数')
    }
  } catch (error) {
    ElMessage.error('参数格式不正确，请检查输入格式')
  }
}

// 发送变更事件
const emitChange = () => {
  emit('update:modelValue', params.value)
}
</script>

<style scoped>
.http-query-params-config {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 16px;
}

.params-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.label {
  font-weight: 600;
  color: #303133;
}

.params-list {
  margin-bottom: 16px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.param-enabled {
  flex-shrink: 0;
}

.param-key {
  width: 150px;
  flex-shrink: 0;
}

.param-value {
  flex: 1;
  min-width: 200px;
}

.param-description {
  width: 150px;
  flex-shrink: 0;
}

.param-remove {
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

.batch-import {
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.batch-import-panel {
  margin-top: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.batch-textarea {
  margin-bottom: 8px;
}

.batch-actions {
  display: flex;
  gap: 8px;
}
</style>