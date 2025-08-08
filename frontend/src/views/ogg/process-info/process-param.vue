<script setup>
import CodeEditor from '@/components/codeEditor/index.vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'
import { cloneDeep } from 'lodash-es'
import { ElMessage } from 'element-plus'

const { serverId, processName } = defineProps({
  serverId: {
    type: Number,
    require: true,
  },
  processName: {
    type: String,
    require: true,
  },
})

const { send, loading } = useRequest(
  (...data) => apis.getProcessParams(...data),
  {
    immediate: false,
  },
)

const { send: saveProcessParamsSend, loading: saveProcessParamsLoading } =
  useRequest((...data) => apis.saveProcessParams(...data), {
    immediate: false,
  })

const params = ref('')

const fetchData = () => {
  send(serverId, processName).then(respData => {
    params.value = respData?.params
  })
}

const handleSaveProcessParams = () => {
  const processObj = { serverId, processName }
  processObj.params = params.value
  saveProcessParamsSend(processObj).then(() => {
    ElMessage.success('保存成功')
    fetchData()
  })
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <code-editor v-model="params" type="sql" height="100%" v-loading="loading">
    <el-button type="info" size="small" plain @click="fetchData"
      >刷新
    </el-button>
    <el-button
      type="success"
      size="small"
      plain
      @click="handleSaveProcessParams"
      v-loading="saveProcessParamsLoading"
      >保存
    </el-button>
  </code-editor>
</template>

<style scoped lang="scss"></style>
