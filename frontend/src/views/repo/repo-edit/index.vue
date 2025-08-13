<template>
  <div>
    <!--    数据连接编辑弹窗  -->
    <el-dialog v-model="dialogVisible" title="编辑连接">
      <el-form
        v-if="dialogVisible"
        ref="formRef"
        :model="current_row"
        label-width="120px"
      >
        <el-form-item
          label="连接类型"
          prop="dataProviderType"
          :rules="[{ required: true, message: '选择连接类型' }]"
        >
          <el-select
            v-model="current_row.dataProviderType"
            placeholder="请选择连接类型"
            @change="onDataProviderTypeChange"
          >
            <el-option
              v-for="item in repoDriveConfig"
              :key="item.id"
              :value="item.id"
              :label="item.dbName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="连接名称"
          prop="name"
          :rules="[{ required: true, message: '请输入连接名称' }]"
        >
          <el-input v-model="current_row.name"></el-input>
        </el-form-item>
        <el-form-item
          label="服务器地址"
          prop="host"
          :rules="[{ required: true, message: '请输入服务器地址' }]"
        >
          <el-input
            v-model="current_row.host"
            @input="generateJdbcUrl"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="端口"
          prop="port"
          :rules="[{ required: true, message: '请输入服务器端口' }]"
        >
          <el-input-number
            v-model="current_row.port"
            @change="generateJdbcUrl"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="数据库名称">
          <el-input
            v-model="current_row.repositoryName"
            @input="generateJdbcUrl"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="连接字符串"
          prop="url"
          :rules="[{ required: true, message: '请输入连接字符串' }]"
        >
          <el-input
            v-model="current_row.url"
            placeholder="输入连接字符串"
            @input="parseTemplate"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="用户名"
          prop="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <el-input v-model="current_row.username"></el-input>
        </el-form-item>
        <el-form-item
          label="密码"
          prop="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <el-input
            type="password"
            ord
            show-password
            clearable
            v-model="current_row.password"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="读取器"
          prop="reader"
        >
          <el-select v-model="current_row.reader">
            <el-option
              v-for="item in dataxPlugins.filter(t => t.type== 'reader')"
              :key="item.code"
              :value="item.code"
              :label="item.name"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="写入器"
          prop="writer"
        >
          <el-select v-model="current_row.writer">
            <el-option
              v-for="item in dataxPlugins.filter(t => t.type== 'writer')"
              :key="item.code"
              :value="item.code"
              :label="item.name"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button
          type="success"
          @click="test(current_row)"
          v-loading="testLoading"
          >测试连接
        </el-button>
        <el-button type="primary" @click="save(current_row)">保存</el-button>
        <el-button @click="dialogVisible = false" plain>取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import apis from '@/service/apis'
import { useRequest } from 'alova/client'

const dialogVisible = ref(false)

const current_row = ref({})
const formRef = ref(null)

const props = defineProps({
  onSave: {
    type: Function,
  },
})

const { data: repoDriveConfig } = useRequest(apis.getRepoConfig)
const { data: dataxPlugins } = useRequest(apis.getDataxPlugins)

const save = data => {
  formRef.value.validate(valid => {
    if (valid) {
      apis
        .saveRepo(data)
        .send()
        .then(() => {
          dialogVisible.value = false
          ElMessage({
            type: 'success',
            message: '保存成功！',
          })
          props.onSave && props.onSave()
        })
    }
  })
}

const { loading: testLoading, send: testLoadingSend } = useRequest(
  data => apis.checkRepo(data),
  {
    immediate: false,
  },
)

const test = data => {
  formRef.value.validate(valid => {
    if (valid) {
      const config = repoDriveConfig.value.find(
        t => t.id === data.dataProviderType,
      )
      data.driverClassName = config?.dbDriver
      data.dataSourceName = config?.dbName
      testLoadingSend(data).then(respData => {
        ElMessage({
          message:
            '服务器：' +
            respData.databaseVersion +
            '<br/>' +
            '驱动：' +
            respData.driverVersion,
          type: 'success',
          dangerouslyUseHTMLString: true,
        })
      })
    }
  })
}

const openDialog = data => {
  current_row.value = cloneDeep(data)
  dialogVisible.value = true
}

function replaceVariables(str, data) {
  // 使用ES6的模板字符串替换变量
  return str.replace(/\${(.*?)}/g, (match, variable) => {
    // 根据变量名获取数据对象中对应的属性值
    const value = data[variable.trim()]
    // 如果找到了属性值，则将其替换成对应的值
    return value !== undefined ? value : match
  })
}

// 自动计算JDBC连接字符串
const onDataProviderTypeChange = () => {
  const config = repoDriveConfig.value.find(
    t => t.id === current_row.value.dataProviderType,
  )
  if (config) {
    current_row.value.port = config.dbPort // 类型改变时重置端口
  }
  generateJdbcUrl()
}
// 自动计算JDBC连接字符串
const generateJdbcUrl = () => {
  const config = repoDriveConfig.value.find(
    t => t.id === current_row.value.dataProviderType,
  )
  if (!config) {
    return
  }

  // 只在端口未设置时使用默认端口
  if (!current_row.value.port) {
    current_row.value.port = config.dbPort
  }

  current_row.value.driverClassName = config.dbDriver
  current_row.value.dataSourceName = config.name
  let jdbcUrl = replaceVariables(config.dbUrl, current_row.value)
  // 将JDBC连接字符串设置为文本框的值
  current_row.value.url = jdbcUrl
}


const parseTemplate = () => {
  const config = repoDriveConfig.value.find(
    t => t.id === current_row.value.dataProviderType,
  )
  let template = config.dbUrl
  let str = current_row.value.url
  // 使用正则表达式匹配模板字符串中的变量名
  const variableNames = template.match(/\${([\w-]+)}/g).map(s => s.slice(2, -1))
  // 构造正则表达式，并从字符串中获取变量值
  const pattern = new RegExp(template.replace(/\${[\w-]+}/g, '([^/]+)'))
  const match = str.match(pattern)
  if (match) {
    const values = match.slice(1)
    // 构建对象，将变量名和变量值对应起来
    for (let i = 0; i < variableNames.length; i++) {
      if (values[i] != '$') {
        if (variableNames[i] == 'port') {
          values[i] = parseInt(values[i])
        }
        current_row.value[variableNames[i]] = values[i]
      }
    }
  }
}

defineExpose({
  openDialog,
})
</script>

<style scoped></style>
