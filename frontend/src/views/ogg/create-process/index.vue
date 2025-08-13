<template>
  <div class="ogg-container h100 pd20 create-process-container">
    <el-form :model="processForm" label-width="100px" label-position="left">
      <!-- 进程名称输入 -->
      <el-form-item label="进程名称">
        <el-input
          v-model="processForm.name"
          placeholder="请输入进程名称"
          @change="handleNameChange"
          @input="handleNameChange"
          :maxlength="4"
        />
      </el-form-item>

      <!-- 功能选择多选框 -->
      <el-form-item label="选择功能">
        <el-checkbox-group
          v-model="processForm.functions"
          @change="generateStatements"
        >
          <el-checkbox label="extract">抓取 (Extract)</el-checkbox>
          <el-checkbox label="pump">投递 (Pump)</el-checkbox>
          <el-checkbox label="replicat">复制 (Replicat)</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-row :gutter="180">
        <el-col
          :span="12"
          v-if="
            processForm.functions.includes('extract') ||
            processForm.functions.includes('pump')
          "
        >
          <el-form-item label="源端">
            <el-select
              v-model="processForm.sourceServer"
              @change="handleServerChange"
            >
              <el-option
                v-for="item in serverList"
                :key="item.id"
                :label="item.serverName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="目标端"
            v-if="processForm.functions.includes('replicat')"
          >
            <el-select
              v-model="processForm.targetServer"
              @change="handleServerChange"
            >
              <el-option
                v-for="item in serverList"
                :key="item.id"
                :label="item.serverName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div class="flex gap10">
      <div
        v-if="processForm.functions.includes('extract')"
        class="config-section flex-auto"
      >
        <h3>抓取进程配置</h3>
        <el-form
          :model="extractConfig"
          label-width="100px"
          label-position="left"
        >
          <el-form-item label="进程名">
            <el-input
              v-model="extractConfig.name"
              placeholder="根据名称自动生成"
              @change="generateStatements"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-input
              v-model="extractConfig.beginTime"
              placeholder="开始时间,NOW 或者 YYYY-MM-DD HH24:MI:SS"
              @change="generateStatements"
            >
            </el-input>
          </el-form-item>

          <el-form-item label="Trail文件路径">
            <el-input
              v-model="extractConfig.trailPath"
              placeholder="./dirdat/et"
              @input="generateStatements"
            />
          </el-form-item>

          <el-form-item label="文件大小(MB)">
            <el-input-number
              v-model="extractConfig.fileSize"
              :min="1"
              :max="1000"
              @change="generateStatements"
            />
          </el-form-item>

          <el-row>
            <el-checkbox
              v-model="extractConfig.isThread"
              class="mr20"
              @change="generateStatements"
              >指定线程</el-checkbox
            >
            <el-input-number
              v-if="extractConfig.isThread"
              v-model="extractConfig.threadNum"
              :min="1"
              :max="100"
              @change="generateStatements"
            />
          </el-row>
        </el-form>
      </div>

      <div v-if="processForm.functions.includes('pump')" class="config-section">
        <h3>投递进程配置</h3>
        <el-form :model="pumpConfig" label-width="100px" label-position="left">
          <el-form-item label="进程名">
            <el-input
              v-model="pumpConfig.name"
              placeholder="根据名称自动生成"
              @change="generateStatements"
            >
            </el-input>
          </el-form-item>

          <el-form-item label="源Trail文件">
            <el-input
              v-model="pumpConfig.sourceTrail"
              placeholder="./dirdat/et"
              @input="generateStatements"
            />
          </el-form-item>

          <el-form-item label="目标Trail文件">
            <el-input
              v-model="pumpConfig.targetTrail"
              placeholder="./dirdat/rt"
              @input="generateStatements"
            />
          </el-form-item>

          <el-form-item label="文件大小(MB)">
            <el-input-number
              v-model="pumpConfig.fileSize"
              :min="1"
              :max="1000"
              @change="generateStatements"
            />
          </el-form-item>
        </el-form>
      </div>

      <div
        v-if="processForm.functions.includes('replicat')"
        class="config-section"
      >
        <h3>复制进程配置</h3>
        <el-form
          :model="replicatConfig"
          label-width="100px"
          label-position="left"
        >
          <el-form-item label="进程名">
            <el-input
              v-model="replicatConfig.name"
              placeholder="根据名称自动生成"
              @change="generateStatements"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="源Trail文件">
            <el-input
              v-model="replicatConfig.sourceTrail"
              placeholder="./dirdat/rt"
              @input="generateStatements"
            />
          </el-form-item>

          <el-form-item label="检查点表">
            <el-input
              v-model="replicatConfig.checkpointTable"
              placeholder="ogg.checkpoint"
              @input="generateStatements"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 根据选择的功能显示对应配置 -->

    <!-- 生成的语句展示 -->
    <div class="statements-section">
      <h3>生成语句</h3>
      <div class="flex gap20">
        <div class="flex-auto">
          <code-editor
            type="sql"
            disabled
            v-model="generatedStatements"
            placeholder="源端"
          ></code-editor>
        </div>
        <div class="flex-auto">
          <code-editor
            type="sql"
            disabled
            v-model="generatedStatementsTarget"
            placeholder="目标端"
          ></code-editor>
        </div>
      </div>
    </div>

    <div class="action-buttons">
      <el-button type="primary" @click="saveProcess" plain v-loading="loading"
        >保存进程</el-button
      >
      <el-button @click="dialogVisible = true" plain type="success"
        >执行结果</el-button
      >
      <el-button @click="resetForm">重置</el-button>
    </div>
    <el-dialog v-model="dialogVisible" title="执行进度">
      <div class="flex-center pd20" style="height: 60vh">
        <code-editor
          v-model="execResultText"
          type="sql"
          :disabled="true"
        ></code-editor>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'
import CodeEditor from '@/components/codeEditor/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 获取服务器的目录分隔符
const getDirectorySeparator = (serverId: string) => {
  const server = serverList.value.find(t => t.id === serverId)
  if (!server) return '/'

  // 01 表示 Windows，使用 \
  // 02 表示 Linux，使用 /
  return server.serverOs === '01' ? '\\' : '/'
}

// 格式化路径为对应操作系统的格式
const formatPathForOS = (path: string, serverId: string) => {
  const separator = getDirectorySeparator(serverId)
  // 将路径中的 / 或 \ 统一替换为对应操作系统的分隔符
  return path.replace(/[\/\\]/g, separator)
}

// 表单数据
const processForm = reactive({
  name: '',
  functions: [] as string[],
  sourceServer: '',
  targetServer: '',
})

// Extract配置
const extractConfig = reactive({
  name: '',
  sourceType: 'TRANLOG',
  beginTime: 'NOW',
  trailPath: './dirdat/et',
  fileSize: 100,
  isThread: false,
  threadNum: 2,
})

// Pump配置
const pumpConfig = reactive({
  name: '',
  sourceTrail: './dirdat/et',
  targetTrail: './dirdat/rt',
  fileSize: 100,
})

// Replicat配置
const replicatConfig = reactive({
  name: '',
  sourceTrail: './dirdat/rt',
  checkpointTable: 'public.ckp',
})

const statements = ref({
  source: [],
  target: [],
})

// 生成的语句
const generatedStatements = ref('')
const generatedStatementsTarget = ref('')
const execResult = ref([])
const dialogVisible = ref(false)

const execResultText = computed(() => execResult.value.join('\n'))

const loading = ref(false)

// 生成语句的方法
const generateStatements = () => {
  statements.value.source = []

  const processName = processForm.name.toLowerCase()
  if (processForm.functions.includes('extract')) {
    let mkdir =
      'sh mkdir ' +
      (getDirectorySeparator(processForm.sourceServer) === '/' ? '-p ' : '') +
      formatPathForOS(`./dirdat/${processName}`, processForm.sourceServer)
    statements.value.source.push(mkdir)
    let statement = `ADD EXTRACT ${extractConfig.name}, ${extractConfig.sourceType}, BEGIN ${extractConfig.beginTime}`

    if (extractConfig.isThread) {
      statement += `, THREADS ${extractConfig.threadNum}`
    }
    statements.value.source.push(statement)
    statements.value.source.push(
      `ADD EXTTRAIL ${extractConfig.trailPath}, EXTRACT ${extractConfig.name}, MEGABYTES ${extractConfig.fileSize}`,
    )
  }

  if (processForm.functions.includes('pump')) {
    statements.value.source.push(
      `ADD EXTRACT ${pumpConfig.name}, EXTTRAILSOURCE ${pumpConfig.sourceTrail} ,EXTSEQNO 0,EXTRBA 0`,
    )
    statements.value.source.push(
      `ADD RMTTRAIL ${pumpConfig.targetTrail}, EXTRACT ${pumpConfig.name}, MEGABYTES ${pumpConfig.fileSize}`,
    )
  }
  statements.value.target = []
  if (processForm.functions.includes('replicat')) {
    statements.value.target.push(
      `ADD REPLICAT ${replicatConfig.name}, EXTTRAIL ${replicatConfig.sourceTrail},EXTSEQNO 0,EXTRBA 0,checkpointtable ${replicatConfig.checkpointTable}`,
    )
  }

  generatedStatements.value = `##源端##\n` + statements.value.source.join('\n')
  generatedStatementsTarget.value =
    `##目标端##\n` + statements.value.target.join('\n')
}

const { send } = useRequest(data => apis.commandByOgg(data), {
  immediate: false,
})

// 保存进程
const saveProcess = async () => {
  if (!processForm.name) {
    ElMessage.warning('请输入进程名称')
    return
  }

  if (processForm.functions.length === 0) {
    ElMessage.warning('请选择至少一个功能')
    return
  }

  if (
    processForm.functions.includes('pump') ||
    processForm.functions.includes('extract')
  ) {
    if (!processForm.sourceServer) {
      ElMessage.warning('请选择正确的源端服务器')
      return
    }
  }

  if (processForm.functions.includes('replicat')) {
    if (!processForm.targetServer) {
      ElMessage.warning('请选择正确的目标端服务器')
      return
    }
  }

  dialogVisible.value = true
  execResult.value = []
  loading.value = true

  try {
    if (
      processForm.functions.includes('pump') ||
      processForm.functions.includes('extract')
    ) {
      execResult.value.push('##源端##')

      const resp = await send({
        commandList: statements.value.source,
        serverId: processForm.sourceServer,
      })
      execResult.value.push(resp)

      execResult.value.push('')
    }

    if (processForm.functions.includes('replicat')) {
      execResult.value.push('##目标端##')

      const resp1 = await send({
        commandList: statements.value.target,
        serverId: processForm.targetServer,
      })
      execResult.value.push(resp1)
    }
    execResult.value.push('执行完成！！')
    ElMessage.success('执行完成！！')
  } catch (error: any) {
    execResult.value.push(`执行出错: ${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  processForm.name = ''
  processForm.functions = []
  extractConfig.name = ''
  extractConfig.sourceType = 'TRANLOG'
  extractConfig.beginTime = 'NOW'
  extractConfig.trailPath = './dirdat/et'
  extractConfig.fileSize = 100
  pumpConfig.sourceTrail = './dirdat/et'
  pumpConfig.targetTrail = './dirdat/rt'
  pumpConfig.fileSize = 100
  replicatConfig.sourceTrail = './dirdat/rt'
  replicatConfig.checkpointTable = 'public.checkpoint'
  generatedStatements.value = ''
}

const handleNameChange = () => {
  if (!processForm.name) return
  extractConfig.name = 'EXT' + processForm.name.toUpperCase()
  pumpConfig.name = 'PUM' + processForm.name.toUpperCase()
  replicatConfig.name = 'REP' + processForm.name.toUpperCase()
  extractConfig.trailPath = './dirdat/' + processForm.name.toLowerCase() + '/ea'
  pumpConfig.sourceTrail = './dirdat/' + processForm.name.toLowerCase() + '/ea'
  pumpConfig.targetTrail = './dirdat/' + processForm.name.toLowerCase() + '/ta'
  replicatConfig.sourceTrail =
    './dirdat/' + processForm.name.toLowerCase() + '/ta'
  handleServerChange()
}

// 处理服务器变更的函数
const handleServerChange = () => {
  // 处理源端服务器变更
  if (processForm.sourceServer) {
    const sourceSeparator = getDirectorySeparator(processForm.sourceServer)

    // 更新 Extract 配置中的路径分隔符
    if (extractConfig.trailPath) {
      extractConfig.trailPath = formatPathForOS(
        extractConfig.trailPath,
        processForm.sourceServer,
      )
    }

    // 更新 Pump 配置中的路径分隔符
    if (pumpConfig.sourceTrail) {
      pumpConfig.sourceTrail = formatPathForOS(
        pumpConfig.sourceTrail,
        processForm.sourceServer,
      )
    }

    if (pumpConfig.targetTrail) {
      pumpConfig.targetTrail = formatPathForOS(
        pumpConfig.targetTrail,
        processForm.targetServer,
      )
    }
  }

  // 处理目标端服务器变更
  if (processForm.targetServer) {
    const targetSeparator = getDirectorySeparator(processForm.targetServer)

    // 更新 Replicat 配置中的路径分隔符
    if (replicatConfig.sourceTrail) {
      replicatConfig.sourceTrail = formatPathForOS(
        replicatConfig.sourceTrail,
        processForm.targetServer,
      )
    }
  }

  // 重新生成语句
  generateStatements()
}

// 监听服务器选择变化，重新生成语句
watch([() => processForm.sourceServer, () => processForm.targetServer], () => {
  generateStatements()
})

const serverList = ref([])

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
})

// 组件挂载时初始化
onMounted(() => {
  apis
    .getServerList()
    .send()
    .then(data => {
      serverList.value = data
      const ods = serverList.value.find(
        t => t.serverName.toUpperCase() === 'ODS',
      )
      if (ods) {
        processForm.targetServer = ods.id
      }
      const source = serverList.value.find(t => t.id === props.data.id)
      if (source) {
        processForm.sourceServer = source.id
      }
    })
  generateStatements()
})
</script>

<style scoped lang="scss">
.create-process-container {
  background-color: var(--el-fill-color-blank);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.config-section {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fafafa;
  flex: 1;

  h3 {
    margin-top: 0;
    margin-bottom: 16px;
    color: #303133;
  }
}

.statements-section {
  margin-top: 20px;

  h3 {
    margin-bottom: 16px;
    color: #303133;
  }
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
}
</style>
