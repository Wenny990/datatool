<template>
  <div class="h100 w100 border pd20" ref="chartContainerWp">
      <el-form>
        <el-form-item>
          <el-input v-model="command" placeholder="请输入命令" @keyup.enter.stop="fetchData">
            <template #append>
              <el-button @click="fetchData">执行</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    <code-editor v-model="history" type="sql" :disabled="true"></code-editor>
  </div>
</template>

<script setup>
import apis from '@/service/apis'
import CodeEditor from '@/components/codeEditor/index.vue'

const history = ref('')
const command = ref('')
const props = defineProps({
  data: {
    type: Object,
    required: true
  }
})
const fetchData = async () => {
  history.value += `${command.value}\n`
  let result = await apis.previewCommandResult({
    serverId: props.data.id,
    command: command.value
  })
  command.value = ''
  history.value += `${result}\n`
}

</script>

<style scoped lang="scss"></style>
