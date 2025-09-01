<template>
  <div class="http-body-config">
    <div class="body-header">
      <span class="label">请求体配置</span>
      <el-radio-group v-model="bodyType" @change="handleBodyTypeChange" size="small">
        <el-radio-button label="none">无</el-radio-button>
        <el-radio-button label="form-data">form-data</el-radio-button>
        <el-radio-button label="x-www-form-urlencoded">x-www-form-urlencoded</el-radio-button>
        <el-radio-button label="raw">raw</el-radio-button>
        <el-radio-button label="binary">binary</el-radio-button>
      </el-radio-group>
    </div>

    <!-- None 类型 -->
    <div v-if="bodyType === 'none'" class="body-content">
      <div class="empty-state">
        <p>不发送请求体</p>
      </div>
    </div>

    <!-- Form-data 类型 -->
    <div v-else-if="bodyType === 'form-data'" class="body-content">
      <div class="form-data-toolbar">
        <el-button size="small" type="primary" @click="addFormDataParam">
          <el-icon><Plus /></el-icon>
          添加参数
        </el-button>
      </div>

      <div class="form-data-list" v-if="formDataParams && formDataParams.length > 0">
        <div
          v-for="(param, index) in formDataParams"
          :key="index"
          class="form-data-item">

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

          <el-select
            v-model="param.type"
            @change="emitChange"
            class="param-type">
            <el-option label="文本" value="text" />
            <el-option label="文件" value="file" />
          </el-select>

          <el-input
            v-model="param.value"
            :placeholder="param.type === 'file' ? '文件路径或Base64' : '参数值，支持${param}占位符'"
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
            @click="removeFormDataParam(index)"
            class="param-remove">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="empty-state" v-else>
        <p>暂无form-data参数</p>
        <el-button type="primary" text @click="addFormDataParam">
          <el-icon><Plus /></el-icon>
          添加第一个参数
        </el-button>
      </div>
    </div>

    <!-- x-www-form-urlencoded 类型 -->
    <div v-else-if="bodyType === 'x-www-form-urlencoded'" class="body-content">
      <div class="url-encoded-toolbar">
        <el-button size="small" type="primary" @click="addUrlEncodedParam">
          <el-icon><Plus /></el-icon>
          添加参数
        </el-button>
      </div>

      <div class="url-encoded-list" v-if="urlEncodedParams && urlEncodedParams.length > 0">
        <div
          v-for="(param, index) in urlEncodedParams"
          :key="index"
          class="url-encoded-item">

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
            @click="removeUrlEncodedParam(index)"
            class="param-remove">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="empty-state" v-else>
        <p>暂无URL编码参数</p>
        <el-button type="primary" text @click="addUrlEncodedParam">
          <el-icon><Plus /></el-icon>
          添加第一个参数
        </el-button>
      </div>
    </div>

    <!-- Raw 类型 -->
    <div v-else-if="bodyType === 'raw'" class="body-content">
      <div class="raw-toolbar">
        <span class="raw-type-label">数据类型：</span>
        <el-select v-model="rawContentType" @change="emitChange" size="small">
          <el-option label="JSON" value="JSON" />
          <el-option label="XML" value="XML" />
          <el-option label="HTML" value="HTML" />
          <el-option label="TEXT" value="TEXT" />
          <el-option label="JavaScript" value="JavaScript" />
        </el-select>
      </div>

      <div class="raw-editor">
        <code-editor
          v-model="rawContent"
          :type="getRawEditorType()"
          height="300px"
          placeholder="请输入请求体内容，支持${param}占位符"
        />
      </div>
    </div>

    <!-- Binary 类型 -->
    <div v-else-if="bodyType === 'binary'" class="body-content">
      <div class="binary-config">
        <el-form label-width="100px">
          <el-form-item label="文件名称">
            <el-input
              v-model="binaryFileName"
              placeholder="文件名称"
              @input="emitChange">
            </el-input>
          </el-form-item>

          <el-form-item label="Content-Type">
            <el-input
              v-model="binaryContentType"
              placeholder="例如：image/jpeg, application/pdf"
              @input="emitChange">
            </el-input>
          </el-form-item>

          <el-form-item label="文件数据">
            <el-input
              v-model="binaryFileData"
              type="textarea"
              :rows="4"
              placeholder="文件路径或Base64编码的文件数据"
              @input="emitChange">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import CodeEditor from '@/components/codeEditor/index.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ type: 'none' })
  }
})

const emit = defineEmits(['update:modelValue'])

// 请求体类型
const bodyType = ref(props.modelValue?.type || 'none')

// Form-data参数
const formDataParams = ref(props.modelValue?.formData || [])

// URL编码参数
const urlEncodedParams = ref(props.modelValue?.urlEncoded || [])

// Raw数据
const rawContent = ref(props.modelValue?.raw?.content || '')
const rawContentType = ref(props.modelValue?.raw?.contentType || 'JSON')

// Binary数据
const binaryFileName = ref(props.modelValue?.binary?.fileName || '')
const binaryContentType = ref(props.modelValue?.binary?.contentType || '')
const binaryFileData = ref(props.modelValue?.binary?.fileData || '')

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    bodyType.value = newVal.type || 'none'
    formDataParams.value = newVal.formData || []
    urlEncodedParams.value = newVal.urlEncoded || []
    rawContent.value = newVal.raw?.content || ''
    rawContentType.value = newVal.raw?.contentType || 'JSON'
    binaryFileName.value = newVal.binary?.fileName || ''
    binaryContentType.value = newVal.binary?.contentType || ''
    binaryFileData.value = newVal.binary?.fileData || ''
  }
}, { deep: true, immediate: true })

// 请求体类型变更
const handleBodyTypeChange = () => {
  emitChange()
}

// 添加Form-data参数
const addFormDataParam = () => {
  formDataParams.value.push({
    key: '',
    value: '',
    type: 'text',
    enabled: true,
    description: ''
  })
  emitChange()
}

// 删除Form-data参数
const removeFormDataParam = (index) => {
  formDataParams.value.splice(index, 1)
  emitChange()
}

// 添加URL编码参数
const addUrlEncodedParam = () => {
  urlEncodedParams.value.push({
    key: '',
    value: '',
    enabled: true,
    description: ''
  })
  emitChange()
}

// 删除URL编码参数
const removeUrlEncodedParam = (index) => {
  urlEncodedParams.value.splice(index, 1)
  emitChange()
}


// 获取Raw编辑器类型
const getRawEditorType = () => {
  const typeMap = {
    'JSON': 'json',
    'XML': 'xml',
    'HTML': 'html',
    'TEXT': 'text',
    'JavaScript': 'javascript'
  }
  return typeMap[rawContentType.value] || 'text'
}




// 发送变更事件
const emitChange = () => {
  const bodyConfig = {
    type: bodyType.value
  }

  if (bodyType.value === 'form-data') {
    bodyConfig.formData = formDataParams.value
  } else if (bodyType.value === 'x-www-form-urlencoded') {
    bodyConfig.urlEncoded = urlEncodedParams.value
  } else if (bodyType.value === 'raw') {
    bodyConfig.raw = {
      content: rawContent.value,
      contentType: rawContentType.value
    }
  } else if (bodyType.value === 'binary') {
    bodyConfig.binary = {
      fileName: binaryFileName.value,
      contentType: binaryContentType.value,
      fileData: binaryFileData.value
    }
  }

  emit('update:modelValue', bodyConfig)
}

watch(rawContent , () => emitChange() , {
  immediate: true
})
</script>

<style scoped>
.http-body-config {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 16px;
}

.body-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.label {
  font-weight: 600;
  color: #303133;
}

.body-content {
  min-height: 100px;
}

.form-data-toolbar,
.url-encoded-toolbar {
  margin-bottom: 12px;
}

.form-data-item,
.url-encoded-item {
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

.param-type {
  width: 100px;
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
}

.raw-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.raw-type-label {
  color: #606266;
  font-size: 14px;
}

.raw-editor {
  border-radius: 4px;
  overflow: hidden;
}

.binary-config {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}
</style>
