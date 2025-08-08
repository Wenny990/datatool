<script setup>
import CodeEditor from '/@/components/codeEditor/index.vue'
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'

const { serverId, processName } = defineProps({
  serverId: {
    type: Number,
    require: true
  },
  processName: {
    type: String,
    require: true
  },
})

const {
  send,
  loading,
  data: currDefFile,
} = useRequest(
  apis.getDefFileParam({
    serverId: serverId,
    processName: processName,
  }),
  {
    immediate: false,
    initialData: () => ({}),
  },
)

const fetchData = async () => {
  send()
}

const handleSaveDefFileParam = async () => {
  loading.value = true
  await apis
    .saveDefFileParam({
      serverId: serverId,
      processName: processName,
      paramContent: currDefFile.value.defParamContent,
    })
    .send()
  loading.value = false
  ElMessage.success('保存成功')
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="h100" v-loading="loading">
    <code-editor
      v-model="currDefFile.defParamContent"
      type="sql"
      height="100%"
      :disabled="false"
    >
      <el-button type="info" size="small" plain @click="fetchData"
        >刷新</el-button
      >
      <el-button
        type="success"
        size="small"
        plain
        @click="handleSaveDefFileParam"
        >保存</el-button
      >
    </code-editor>
  </div>
</template>

<style scoped lang="scss"></style>
