<template>
  <div class="api-test-form px10">
    <el-form :model="testForm" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="API编码">
            <el-input v-model="apiConfig.apiCode" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="API名称">
            <el-input v-model="apiConfig.apiName" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="接口类型">
        <el-tag :type="apiConfig.apiType === 1 ? 'success' : 'info'">
          {{ apiConfig.apiType === 1 ? 'HTTP接口' : 'SQL查询' }}
        </el-tag>
      </el-form-item>

      <!-- HTTP接口配置预览 -->
      <template v-if="apiConfig.apiType === 1 && httpConfigPreview">
        <div class="mb20 ml20">
          <el-descriptions border :column="2" size="small">
            <el-descriptions-item label="请求方法" span="2">
              <template #label>
                <div style="width: 70px" class="cell-item">请求方法</div>
              </template>
              <el-tag size="small">{{ httpConfigPreview.method }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="请求URL" span="2">
              {{ httpConfigPreview.url }}
            </el-descriptions-item>
            <el-descriptions-item label="请求头" span="2">
              <el-tag
                v-for="header in httpConfigPreview.headers"
                :key="header.key"
                size="small"
                style="margin-right: 4px"
              >
                {{ header.key }}: {{ header.value }}
              </el-tag>
              <span
                v-if="
                  !httpConfigPreview.headers ||
                  httpConfigPreview.headers.length === 0
                "
                >无</span
              >
            </el-descriptions-item>
            <el-descriptions-item label="查询参数" span="2">
              <el-tag
                v-for="param in httpConfigPreview.queryParams"
                :key="param.key"
                size="small"
                style="margin-right: 4px"
              >
                {{ param.key }}={{ param.value }}
              </el-tag>
              <span
                v-if="
                  !httpConfigPreview.queryParams ||
                  httpConfigPreview.queryParams.length === 0
                "
                >无</span
              >
            </el-descriptions-item>
            <el-descriptions-item label="请求体类型">
              <el-tag
                size="small"
                :type="getBodyTypeTagType(httpConfigPreview.body?.type)"
              >
                {{ getBodyTypeLabel(httpConfigPreview.body?.type) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </template>

      <el-form-item label="请求参数">
        <code-editor
          v-model="testForm.params"
          type="json"
          height="200px"
          placeholder='{"param1": "value1", "param2": "value2"}'
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleTest" :loading="testing">
          {{ testing ? '测试中...' : '执行测试' }}
        </el-button>
        <el-button @click="handleClear">清空结果</el-button>
        <el-button @click="handleGenerateExample" v-if="apiConfig.apiType === 1"
          >生成示例参数</el-button
        >
      </el-form-item>
    </el-form>

    <el-divider>测试结果</el-divider>

    <div v-if="testResult" class="test-result">
      <el-descriptions border :column="2">
        <el-descriptions-item label="执行状态">
          <el-tag :type="testResult.success ? 'success' : 'danger'">
            {{ testResult.success ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行耗时">
          {{ testResult.executionTime }}ms
        </el-descriptions-item>
      </el-descriptions>

      <div class="result-content">
        <h4>响应数据：</h4>
        <code-editor
          v-model="formattedResult"
          type="json"
          height="300px"
          :disabled="true"
        />
      </div>

      <div v-if="testResult.errorMessage" class="error-message">
        <h4>错误信息：</h4>
        <el-alert
          :title="testResult.errorMessage"
          type="error"
          show-icon
          :closable="false"
        />
      </div>
    </div>

    <div v-if="!testResult && hasExecuted" class="no-result">
      <el-empty description="暂无测试结果" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CodeEditor from '@/components/codeEditor/index.vue'
import apis from '@/service/apis'

const props = defineProps({
  apiConfig: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['close'])

const testing = ref(false)
const hasExecuted = ref(false)
const testResult = ref(null)
const httpConfigPreview = ref(null)

const testForm = reactive({
  params: '',
})

const formattedResult = computed(() => {
  try {
    return JSON.stringify(testResult.value, null, 2)
  } catch (e) {
    return String(testResult.value)
  }
})

const handleTest = async () => {
  if (!testForm.params.trim()) {
    ElMessage.warning('请输入测试参数')
    return
  }

  try {
    // 验证JSON格式
    const params = JSON.parse(testForm.params)

    testing.value = true
    hasExecuted.value = true

    const response = await apis.callApi({
      apiCode: props.apiConfig.apiCode,
      params: params,
    })

    testResult.value = response

    if (testResult.value.success) {
      ElMessage.success('测试执行成功')
    } else {
      ElMessage.error('测试执行失败')
    }
  } catch (error) {
    if (error.name === 'SyntaxError') {
      ElMessage.error('请求参数格式不正确，请输入有效的JSON')
    } else {
      ElMessage.error('测试执行失败')
      testResult.value = {
        success: false,
        errorMessage: error.message || '未知错误',
        executionTime: 0,
      }
    }
  } finally {
    testing.value = false
  }
}

const handleClear = () => {
  testResult.value = null
  hasExecuted.value = false
}

// 生成示例参数
const generateExampleParams = config => {
  const exampleParams = {}

  // 从 HTTP 配置中提取参数
  if (config.apiType === 1 && httpConfigPreview.value) {
    const httpConfig = httpConfigPreview.value

    // 从 URL 中提取路径参数
    if (httpConfig.url) {
      const urlParams = httpConfig.url.match(/\{([^}]+)\}/g)
      if (urlParams) {
        urlParams.forEach(param => {
          const paramName = param.slice(1, -1)
          exampleParams[paramName] = `示例${paramName}`
        })
      }
    }

    // 从请求头中提取参数
    if (httpConfig.headers) {
      httpConfig.headers.forEach(header => {
        if (header.value && header.value.includes('${')) {
          const matches = header.value.match(/\$\{([^}]+)\}/g)
          if (matches) {
            matches.forEach(match => {
              const paramName = match.slice(2, -1)
              exampleParams[paramName] = `示例${paramName}`
            })
          }
        }
      })
    }

    // 从查询参数中提取参数
    if (httpConfig.queryParams) {
      httpConfig.queryParams.forEach(param => {
        if (param.value && param.value.includes('${')) {
          const matches = param.value.match(/\$\{([^}]+)\}/g)
          if (matches) {
            matches.forEach(match => {
              const paramName = match.slice(2, -1)
              exampleParams[paramName] = `示例${paramName}`
            })
          }
        }
      })
    }
  }

  testForm.params = JSON.stringify(exampleParams, null, 2)
}

// 手动生成示例参数
const handleGenerateExample = () => {
  generateExampleParams(props.apiConfig)
}

// 监听API配置变化，设置默认参数
watch(
  () => props.apiConfig,
  newConfig => {
    if (newConfig) {
      // 解析HTTP配置预览
      if (newConfig.apiType === 1 && newConfig.httpRequestConfig) {
        try {
          httpConfigPreview.value = JSON.parse(newConfig.httpRequestConfig)
        } catch (e) {
          console.warn('解析HTTP配置失败:', e)
          httpConfigPreview.value = null
        }
      } else {
        httpConfigPreview.value = null
      }

      // 生成示例参数
      generateExampleParams(newConfig)
    }

    // 重置测试结果
    testResult.value = null
    hasExecuted.value = false
  },
  { immediate: true },
)

// 获取请求体类型标签类型
const getBodyTypeTagType = type => {
  const typeMap = {
    none: 'info',
    'form-data': 'success',
    'x-www-form-urlencoded': 'warning',
    raw: 'primary',
    binary: 'danger',
  }
  return typeMap[type] || 'info'
}

// 获取请求体类型标签文本
const getBodyTypeLabel = type => {
  const labelMap = {
    none: '无',
    'form-data': 'Form Data',
    'x-www-form-urlencoded': 'URL Encoded',
    raw: 'Raw',
    binary: 'Binary',
  }
  return labelMap[type] || '未知'
}
</script>

<style scoped>
.api-test-form {
  padding: 20px 0;
}

.test-result {
  margin-top: 20px;
}

.result-content {
  margin-top: 20px;
}

.result-content h4 {
  margin-bottom: 10px;
  color: #303133;
}

.error-message {
  margin-top: 20px;
}

.error-message h4 {
  margin-bottom: 10px;
  color: #f56c6c;
}

.no-result {
  margin-top: 20px;
  text-align: center;
}
</style>
