<template>
  <div class="app-container h100">
    <div class="build-container flex-center h100">
      <el-steps :active="active" finish-status="success">
        <el-step title="基本配置">1</el-step>
        <el-step title="输入输出配置">2</el-step>
        <el-step title="字段匹配">3</el-step>
        <el-step title="转换">4</el-step>
        <el-step title="预览">5</el-step>
      </el-steps>
      <div class="flex-auto">
        <div
          v-show="active === 1"
          class="step1 flex-auto flex-center h100 basic"
        >
          <el-form
            :model="job"
            :rules="rules"
            ref="formRef"
            label-position="right"
            label-width="100px"
          >
            <el-row :gutter="20" class="mb32">
              <el-col :span="8">
                <el-form-item label="任务名称" prop="jobName">
                  <el-input v-model="job.jobName"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="datax服务器" prop="serverId">
                  <el-select v-model="job.serverId">
                    <el-option
                      v-for="item in serverList"
                      :key="item.id"
                      :label="item.serverName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row class="mb32">
              <el-col :span="8">
                <el-form-item label="通道数">
                  <el-input-number
                    v-model="job.setting.speed.channel"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="总记录限速">
                  <el-input-number
                    v-model="job.setting.speed.record"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="总字节限速">
                  <el-input-number
                    v-model="job.setting.speed.byte"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row class="mb32">
              <el-col :span="8">
                <el-form-item label="容错记录数">
                  <el-input-number
                    v-model="job.setting.errorLimit.record"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="容错比例">
                  <el-input-number
                    :max="1"
                    :min="0"
                    :step="0.01"
                    v-model="job.setting.errorLimit.percentage"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row class="mb32">
              <el-col :span="24">
                <el-form-item label="jvm参数" prop="jvmParams">
                  <el-input v-model="job.jvmParams"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <div v-if="active === 2" class="step1 flex-auto flex-center h100">
          <div class="flex-auto flex config-container">
            <div class="flex-auto">
              <Reader
                ref="readerRef"
                :reader="job.reader"
                :repo-list="repoList"
              />
            </div>
            <div class="flex-auto">
              <Writer
                ref="writerRef"
                :writer="job.writer"
                :repo-list="repoList"
              />
            </div>
          </div>
        </div>
        <div v-if="active === 3" class="h100">
          <mapper :job="job"></mapper>
        </div>
        <div v-if="active === 4" class="step1 flex-auto flex-center h100">
          <Transformer ref="transformerRef" :transformer="job.transformer" />
        </div>
        <div v-show="active === 5" class="h100">
          <code-editor type="json" v-model="job.jobParams" :disabled="false" />
        </div>
      </div>
      <div>
        <el-button :disabled="active === 1" @click="last">上一步</el-button>
        <el-button v-show="active < 5" type="primary" @click="next"
          >下一步</el-button
        >
        <slot>
          <el-button v-show="active === 5" type="success" @click="save"
            >保存</el-button
          >
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup>
import Reader from './reader.vue'
import Writer from './writer.vue'
import CodeEditor from '@/components/codeEditor/index.vue'
import Mapper from './mapper.vue'
import { ElMessage } from 'element-plus'
import { useDataXStore } from '@/stores/datax'
import apis from '@/service/apis'
import Transformer from '@/views/datax/job-build/transformer.vue'

const rules = {
  jobName: [{ required: true, message: '任务名称不能为空' }],
  serverId: [{ required: true, message: '请选择datax服务器' }],
}
const active = ref(1)

const serverList = ref([])
const repoList = ref([])

const job = ref({
  jobName: '',
  serverId: null,
  setting: {
    speed: {
      channel: 10, //通道数
      byte: -1, //通道速度
      record: -1,
    },
    //出错限制
    errorLimit: {
      record: 0, //记录数
      percentage: 0.02, //百分比
    },
  },
  reader: {
    repoId: '',
    type: 0,
    schemaName: '',
    tableName: '',
    columns: [],
    where: '',
    fetchSize: 1024,
    querySql: '',
    checkAll: true,
    splitPk: '',
    tableSchema: '',
  },
  writer: {
    repoId: '',
    type: 0,
    schemaName: '',
    tableName: '',
    columns: [],
    batchSize: 1024,
    checkAll: true,
    preSql: '',
    postSql: '',
    tableSchema: '',
  },
  transformer: [],
  jobParams: '',
  jvmParams: '-Xms1G -Xmx8G',
})

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const formRef = ref()
const readerRef = ref()
const writerRef = ref()
const buildJson = () => {
  const sourceRepository = repoList.value.find(
    t => t.id === job.value.reader.repoId,
  )
  const targetRepository = repoList.value.find(
    t => t.id === job.value.writer.repoId,
  )
  const dataxJob = {}
  dataxJob.setting = job.value.setting
  const reader = {
    name: sourceRepository.reader,
    parameter: {
      // 数据库连接用户名
      username: sourceRepository.username,
      // 数据库连接密码
      password: sourceRepository.password,

      connection: [
        {
          jdbcUrl: [sourceRepository.url],
        },
      ],
    },
  }
  if (job.value.reader.type === 1) {
    reader.parameter.connection[0].querySql = [job.value.reader.querySql]
  } else {
    reader.parameter.connection[0].table = [
      job.value.reader.schemaName + '.' + job.value.reader.tableName,
    ]
    reader.parameter.splitPk = job.value.reader.splitPk
    reader.parameter.column = job.value.mapperList.map(item => item.left)
    reader.parameter.where = job.value.reader.where
    reader.parameter.fetchSize = job.value.reader.fetchSize
  }

  const writer = {
    name: targetRepository?.writer,
    parameter: {
      username: targetRepository.username,
      password: targetRepository.password,
      batchSize: job.value.writer.batchSize,
      connection: [
        {
          jdbcUrl: targetRepository.url,
          table: [
            job.value.writer.schemaName + '.' + job.value.writer.tableName,
          ],
        },
      ],
    },
  }
  writer.parameter.column = job.value.mapperList.map(item => item.right)
  writer.parameter.preSql = job.value.writer.preSql.split(';')
  if (job.value.writer.truncateTable) {
    writer.parameter.preSql.push(
      'truncate table ' +
        job.value.writer.schemaName +
        '.' +
        job.value.writer.tableName,
    )
  }
  writer.parameter.postSql = job.value.writer.postSql.split(';')

  const transformer = job.value.transformer

  const content = []
  content.push({
    reader,
    writer,
    transformer
  })
  dataxJob.content = content
  return { job: dataxJob }
}

const next = () => {
  if (active.value === 1) {
    formRef.value.validate().then(() => {
      active.value++
    })
  } else if (active.value === 2) {
    Promise.all([
      readerRef.value.formRef.validate(),
      writerRef.value.formRef.validate(),
    ]).then(resps => {
      if (resps[0] && resps[1]) {
        active.value++
      }
    })
  } else if (active.value === 4) {
    job.value.jobParams = JSON.stringify(buildJson(), null, 4)
    active.value++
  } else {
    active.value++
  }
}
const last = () => {
  if (active.value > 1) {
    active.value--
  }
}

const dataXStore = useDataXStore()

const { removeTab, onRefreshNode } = useDataXStore()

const save = () => {
  apis.saveDataxJob(job.value).then(res => {
    ElMessage.success('任务保存成功')
    onRefreshNode(job.value.groupId)
    if (dataXStore.currComponent === 'JobBuild') {
      removeTab(dataXStore.currComponent)
    }
  })
}

onMounted(() => {
  apis
    .getServerList()
    .send()
    .then(data => {
      serverList.value = data.filter(t => t.serverType === 'datax')
      if (!job.value.serverId && serverList.value.length > 0) {
        job.value.serverId = serverList.value[0].id
      }
    })
  apis
    .getRepo()
    .send()
    .then(data => {
      repoList.value = data
    })
  if (props.data.id) {
    apis.getDataxJob(props.data.id).then(data => {
      job.value = data
      if (!job.value.transformer) {
        job.value.transformer = []
      }
    })
  } else {
    job.value.groupId = props.data.groupId
  }
})
</script>

<style lang="scss" scoped>
$border: 1px solid var(--el-border-color);

.app-container,
.basic {
  padding: 12px;
  border: $border;
  background-color: var(--el-bg-color);
}

.build-container,
.config-container {
  gap: 12px;
}

.step1 {
  gap: 10px;
}
</style>
