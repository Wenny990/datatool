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

const loading = ref(false)

const currDefFile = ref({})

const fetchData = async () => {
  loading.value = true
  currDefFile.value = await apis
    .getDefFileContent({
      serverId: serverId,
      processName: processName,
    })
    .send()
  loading.value = false
}

const handleDefgenDefFile = async () => {
  loading.value = true
  const respData = await apis
    .defgenDefFile({
      serverId: serverId,
      processName: processName,
    })
    .send()
  loading.value = false
  console.log(respData.defgenResult)
  const lines = respData.defgenResult.split(/\r?\n/) // 按换行符分割 (\r?\n 支持 Windows 和 Unix 的换行)
  const errorLines = []
  lines.forEach(t => {
    if (t.indexOf('ERROR') > -1) {
      errorLines.push(t)
    }
  })
  if (errorLines.length > 0) {
    ElMessage.warning('生成有错误：\r\n' + errorLines.join('\n'))
    return
  }
  ElMessage.success('生成完成！')
  fetchData()
}

const state = reactive({
  targetServerId: null,
})
const dialogVisible = ref(false)
const formRef = ref()

const handleShowUploadDefFile = () => {
  dialogVisible.value = true
  const odsServerList = serverList.value.filter(
    t =>
      t.serverType === 'ogg' &&
      t.serverName.toLowerCase().indexOf('ods') !== -1,
  )
  if (odsServerList.length === 1) {
    state.targetServerId = odsServerList[0].id
  }
}

const { send: uploadDefFileSend , loading: uploadLoading } = useRequest(data => apis.uploadDefFile(data), {
  immediate: false
})

const handleUploadDefFile = () => {
  formRef.value.validate(async () => {
    await uploadDefFileSend({
      serverId: serverId,
      processName: processName,
      targetServerId: state.targetServerId,
    })
    dialogVisible.value = false
    ElMessage.success('上传成功')
  })
}

const { data: serverList } = useRequest(apis.getServerList())

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="h100" v-loading="loading">
    <code-editor
      v-model="currDefFile.fileContent"
      type="sql"
      height="100%"
      :disabled="true"
    >
      <el-button type="info" size="small" plain @click="fetchData"
        >刷新</el-button
      >
      <el-button type="primary" size="small" plain @click="handleDefgenDefFile"
        >生成</el-button
      >
      <el-button
        type="success"
        size="small"
        plain
        @click="handleShowUploadDefFile"
        >上传</el-button
      >
    </code-editor>
    <el-dialog v-model="dialogVisible" title="选择目标">
      <el-form
        ref="formRef"
        :rules="{ targetServerId: { required: true } }"
        :model="state"
      >
        <el-form-item label="目标服务器" prop="targetServerId">
          <el-select v-model="state.targetServerId">
            <el-option
              v-for="item in serverList"
              :key="item.id"
              :label="item.serverName"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUploadDefFile" v-loading="uploadLoading">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss"></style>
