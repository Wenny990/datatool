<template>
  <div class="api-config-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      :disabled="isView">

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="接口名称" prop="apiName">
            <el-input v-model="form.apiName" placeholder="请输入接口名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="接口编码" prop="apiCode">
            <el-input v-model="form.apiCode" placeholder="请输入接口编码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="接口类型" prop="apiType">
            <el-select v-model="form.apiType" placeholder="请选择接口类型" style="width: 100%">
              <el-option label="HTTP接口" :value="1" />
              <el-option label="SQL查询" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- HTTP接口配置 -->
      <template v-if="form.apiType === 1">
        <el-tabs v-model="activeHttpTab" type="border-card" class="mb20 ml20">
          <el-tab-pane label="基本配置" name="basic">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="HTTP方法" prop="httpMethod">
                  <el-select v-model="httpConfig.method" @change="syncHttpConfig" placeholder="请选择HTTP方法" style="width: 100%">
                    <el-option label="GET" value="GET" />
                    <el-option label="POST" value="POST" />
                    <el-option label="PUT" value="PUT" />
                    <el-option label="DELETE" value="DELETE" />
                    <el-option label="PATCH" value="PATCH" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="超时时间(ms)" prop="timeout">
                  <el-input-number v-model="httpConfig.timeout" @change="syncHttpConfig" :min="1000" :max="300000" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="跟随重定向">
                  <el-switch v-model="httpConfig.followRedirects" @change="syncHttpConfig" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="请求URL" prop="httpUrl">
              <el-input v-model="httpConfig.url" @input="syncHttpConfig" placeholder="请输入HTTP请求URL，支持{param}路径参数" />
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="请求头" name="headers">
            <http-headers-config v-model="httpConfig.headers" @update:modelValue="syncHttpConfig" />
          </el-tab-pane>

          <el-tab-pane label="查询参数" name="query">
            <http-query-params-config v-model="httpConfig.queryParams" @update:modelValue="syncHttpConfig" />
          </el-tab-pane>

          <el-tab-pane label="请求体" name="body">
            <http-body-config v-model="httpConfig.body" @update:modelValue="syncHttpConfig" />
          </el-tab-pane>
        </el-tabs>
      </template>

      <!-- SQL查询配置 -->
      <template v-if="form.apiType === 2">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源" prop="repositoryId">
              <el-select v-model="form.repositoryId" placeholder="请选择数据源" style="width: 100%">
                <el-option
                  v-for="repo in repositoryList"
                  :key="repo.id"
                  :label="repo.name"
                  :value="repo.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="超时时间(ms)" prop="timeout">
              <el-input-number v-model="form.timeout" :min="1000" :max="300000" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="SQL查询" prop="sqlQuery">
          <code-editor
            v-model="form.sqlQuery"
            type="sql"
            height="200px"
            placeholder="SELECT * FROM table WHERE id = ${id}" />
        </el-form-item>
      </template>
      <el-form-item label="描述">
        <el-input
          v-model="form.description"
          placeholder="请输入接口描述" />
      </el-form-item>
      <el-form-item label="后处理脚本">
        <code-editor
          v-model="form.postProcessScript"
          type="javascript"
          height="120px"
          placeholder="data.size() > 0 ? data[0] : null" />
      </el-form-item>



      <el-form-item v-if="!isView">
        <el-button type="primary" @click="handleSubmit">确定</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import CodeEditor from '@/components/codeEditor/index.vue'
import HttpHeadersConfig from './HttpHeadersConfig.vue'
import HttpQueryParamsConfig from './HttpQueryParamsConfig.vue'
import HttpBodyConfig from './HttpBodyConfig.vue'
import apis from '@/service/apis'

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  isView: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formRef = ref()
const repositoryList = ref([])
const activeHttpTab = ref('basic')

const form = reactive({
  id: null,
  apiName: '',
  apiCode: '',
  apiType: 1,
  httpMethod: 'GET',
  httpUrl: '',
  httpHeaders: '',
  httpRequestConfig: '',
  repositoryId: null,
  sqlQuery: '',
  postProcessScript: '',
  timeout: 30000,
  status: 1,
  description: ''
})

// HTTP可视化配置
const httpConfig = reactive({
  method: 'GET',
  url: '',
  headers: [],
  queryParams: [],
  body: { type: 'none' },
  timeout: 30000,
  followRedirects: true,
  verifySsl: true
})

const rules = {
  apiName: [
    { required: true, message: '请输入接口名称', trigger: 'blur' }
  ],
  apiCode: [
    { required: true, message: '请输入接口编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '编码只能包含字母、数字和下划线，且以字母开头', trigger: 'blur' }
  ],
  apiType: [
    { required: true, message: '请选择接口类型', trigger: 'change' }
  ],
  httpMethod: [
    { required: true, message: '请选择HTTP方法', trigger: 'change' }
  ],
  httpUrl: [
    { required: true, message: '请输入HTTP请求URL', trigger: 'blur' }
  ],
  repositoryId: [
    { required: true, message: '请选择数据源', trigger: 'change' }
  ],
  sqlQuery: [
    { required: true, message: '请输入SQL查询语句', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听表单数据变化
watch(() => props.formData, (newVal) => {
  if (newVal) {
    Object.assign(form, {
      id: newVal.id || null,
      apiName: newVal.apiName || '',
      apiCode: newVal.apiCode || '',
      apiType: newVal.apiType || 1,
      httpMethod: newVal.httpMethod || 'GET',
      httpUrl: newVal.httpUrl || '',
      httpHeaders: newVal.httpHeaders || '',
      httpRequestConfig: newVal.httpRequestConfig || '',
      repositoryId: newVal.repositoryId || null,
      sqlQuery: newVal.sqlQuery || '',
      postProcessScript: newVal.postProcessScript || '',
      timeout: newVal.timeout || 30000,
      status: newVal.status !== undefined ? newVal.status : 1,
      description: newVal.description || ''
    })

    // 处理HTTP配置
    if (newVal.httpRequestConfig) {
      try {
        const config = JSON.parse(newVal.httpRequestConfig)
        Object.assign(httpConfig, {
          method: config.method || 'GET',
          url: config.url || '',
          headers: config.headers || [],
          queryParams: config.queryParams || [],
          body: config.body || { type: 'none' },
          timeout: config.timeout || 30000,
          followRedirects: config.followRedirects !== undefined ? config.followRedirects : true,
          verifySsl: config.verifySsl !== undefined ? config.verifySsl : true
        })
      } catch (e) {
        console.warn('解析HTTP配置失败:', e)
      }
    } else if (newVal.httpUrl) {
      // 兼容旧的配置格式
      httpConfig.method = newVal.httpMethod || 'GET'
      httpConfig.url = newVal.httpUrl || ''
      if (newVal.httpHeaders) {
        try {
          const headers = JSON.parse(newVal.httpHeaders)
          httpConfig.headers = Object.keys(headers).map(key => ({
            key,
            value: headers[key],
            enabled: true,
            description: ''
          }))
        } catch (e) {
          console.warn('解析旧HTTP请求头失败:', e)
        }
      }
    }
  }
}, { immediate: true, deep: true })

onMounted(() => {
  fetchRepositoryList()
})

const fetchRepositoryList = async () => {
  try {
    const response = await apis.getRepo()
    repositoryList.value = response || []
  } catch (error) {
    console.error('获取数据源列表失败:', error)
  }
}

// 同步HTTP配置
const syncHttpConfig = () => {
  // 同步基本信息
  form.httpMethod = httpConfig.method
  form.httpUrl = httpConfig.url
  form.timeout = httpConfig.timeout

  // 将完整的HTTP配置序列化为JSON
  form.httpRequestConfig = JSON.stringify(httpConfig)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    // 验证JSON格式
    if (form.httpHeaders && !isValidJson(form.httpHeaders)) {
      ElMessage.error('请求头格式不正确，请输入有效的JSON')
      return
    }



    // 对于HTTP接口，确保配置同步
    if (form.apiType === 1) {
      syncHttpConfig()
    }

    emit('submit', { ...form })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleCancel = () => {
  emit('cancel')
}

const isValidJson = (str) => {
  if (!str.trim()) return true
  try {
    JSON.parse(str)
    return true
  } catch (e) {
    return false
  }
}
</script>

<style scoped>
.api-config-form {
  padding: 20px 0;
}
</style>
