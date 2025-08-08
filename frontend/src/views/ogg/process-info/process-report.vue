<script setup>
import CodeEditor from '@/components/codeEditor/index.vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'

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

const { send , loading } = useRequest(
  (...data) => apis.getProcessReport(...data),{
    immediate: false
  }
)

const report = ref('');

const fetchReport = () => {
  send(
    serverId, processName
  ).then(respData => {
    report.value = respData?.report
  })
}

onMounted(() => {
  fetchReport()
})


</script>

<template>
  <code-editor
    v-model="report"
    type="sql"
    height="100%"
    :disabled="true"
    v-loading="loading"
  >
    <el-button type="info" size="small" plain @click="fetchReport"
      >刷新
    </el-button>
  </code-editor>
</template>

<style scoped lang="scss"></style>
