<template>
  <div class="app-container h100">
    <div class="build-container flex-center h100">
      <div class="flex-auto">
        <div class="step1 flex-auto flex-center h100">
          <el-form
            :model="config"
            :rules="rules"
            ref="formRef"
            label-width="120px"
          >
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="任务组" prop="groupId">
                  <el-select v-model="config.groupId">
                    <el-option
                      v-for="item in groupList"
                      :key="item.id"
                      :label="item.groupName"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="datax服务器" prop="serverId">
                  <el-select v-model="config.serverId">
                    <el-option
                      v-for="item in serverList"
                      :key="item.id"
                      :label="item.serverName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="通道数">
                  <el-input-number v-model="config.channel"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="源端数据源" prop="source.repoId">
                  <el-select
                    v-model="config.source.repoId"
                    filterable
                    @change="onSourceRepoChange"
                  >
                    <el-option
                      v-for="item in repoList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="源端数据库" prop="source.schemaName">
                  <el-select
                    v-model="config.source.schemaName"
                    filterable
                    @change="onSourceSchemaChange"
                  >
                    <el-option
                      v-for="item in state.sourceSchemaList"
                      :key="item.schemaName"
                      :label="item.schemaName"
                      :value="item.schemaName"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="目标数据源" prop="target.repoId">
                  <el-select
                    v-model="config.target.repoId"
                    filterable
                    @change="onTargetRepoChange"
                  >
                    <el-option
                      v-for="item in repoList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="目标数据库" prop="target.schemaName">
                  <el-select
                    v-model="config.target.schemaName"
                    filterable
                    @change="onTargetSchemaChange"
                  >
                    <el-option
                      v-for="item in state.targetSchemaList"
                      :key="item.schemaName"
                      :label="item.schemaName"
                      :value="item.schemaName"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="清空目标表" prop="truncateTable">
                  <el-checkbox v-model="config.target.truncateTable" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-form-item
                label="数据表"
                prop="tableList"
                :rules="[{ required: true, message: '请选择要抽取的表' }]"
              >
                <el-transfer
                  v-model="config.tableList"
                  v-loading="getTableLoading"
                  style="text-align: left; display: inline-block"
                  filterable
                  :titles="['待选择', '已选择']"
                  :data="state.sourceTableList"
                  :props="{ key: 'tableName', label: 'tableName' }"
                >
                </el-transfer>
              </el-form-item>
            </el-row>
          </el-form>
        </div>
      </div>
      <el-dialog v-model="showImport" title="批量导入">
        <div style="height: 60vh">
          <code-editor
            v-model="importStr"
            type="sql"
            placeholder="在此输入要导入的表，每一行表示一张表，支持直接输入ogg参数"
          >
            <el-button type="primary" size="small" plain @click="parseImportStr"
              >解析
            </el-button>
            <el-button
              type="success"
              size="small"
              plain
              @click="batchImportByStr"
              >导入
            </el-button>
          </code-editor>
        </div>
      </el-dialog>
      <div style="display: flex; justify-content: flex-end">
        <el-button @click="showImport = true" type="primary"
          >批量导入
        </el-button>
        <el-button
          type="success"
          @click="buildJob"
          v-loading="state.saveLoading"
          >保存
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { useDataXStore } from '@/stores/datax'
import CodeEditor from '@/components/codeEditor/index.vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'

const { removeTab, onRefreshNode } = useDataXStore()
const rules = {
  groupId: [{ required: true, message: '名称不能为空' }],
  serverId: [{ required: true, message: '请选择datax服务器' }],
  'target.repoId': [{ required: true, message: '请选择目标数据连接' }],
  'source.repoId': [{ required: true, message: '请选择源端数据连接' }],
  'target.schemaName': [{ required: true, message: '请选择目标数据库' }],
  'source.schemaName': [{ required: true, message: '请选择源端数据库' }],
}
const importStr = ref('')
const showImport = ref(false)
const jobTemplate = {
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
  jobParams: '',
  jvmParams: '-Xms1G -Xmx8G',
}

const batchImportByStr = () => {
  let index = 0
  const failedTables = [] // 用于记录导入失败的表
  importStr.value.split('\n').forEach(item => {
    const table = item.trim()
    //如果table不等于空且table不区分大小写在state.sourceTableList中，就加入到config.value.tableList中
    const sourceTable = state.sourceTableList.find(
      t => t.tableName.toUpperCase() === table.toUpperCase(),
    )
    const sourceTableName = sourceTable?.tableName
    if (
      table !== '' &&
      sourceTableName &&
      config.value.tableList.findIndex(
        t => t.toUpperCase() == table.toUpperCase(),
      ) < 0
    ) {
      config.value.tableList.push(sourceTableName)
      index++
    } else if (table !== '') {
      // 记录未能成功导入的表（非空但未找到匹配或已存在）
      failedTables.push(table)
    }
  })

  let message = `导入成功${index}张表`
  if (failedTables.length > 0) {
    message += `，导入失败${failedTables.length}张表: ${failedTables.join(', ')}`
  }

  ElMessage.success(message)
}


const parseImportStr = () => {
  function matchOggStr(lineStr) {
    const table = lineStr.trim()
    // 使用正则表达式匹配表名
    const regex = /^TABLE\s+\w+\.(\w+)\s*;/i
    const match = regex.exec(table)
    if (match) {
      return match[1]
    }
    const regex1 = /^MAP\s+\w+\.(\w+)\s*,/i
    const match1 = regex1.exec(table)
    if (match1) {
      return match1[1]
    }
    return false
  }

  // 将字符串分割成行
  const lines = importStr.value.split('\n')

  // 遍历每一行
  const newLines = lines.map(line => {
    let newStr = matchOggStr(line)
    // 判断是否符合条件
    if (newStr) {
      // 替换当前行
      return newStr
    } else {
      // 返回原始行
      return line
    }
  })

  // 将新行连接成字符串
  importStr.value = newLines.join('\n')
}

const state = reactive({
  loading: false,
  saveLoading: false,
  sourceSchemaList: [],
  sourceTableList: [],
  targetSchemaList: [],
  targetTableList: [],
})
const config = ref({
  source: {},
  target: {
    truncateTable: true,
  },
  channel: 10,
  tableList: [],
})
const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const formRef = ref()

const { send: getSchemasSend } = useRequest(data => apis.getSchemas(data), {
  immediate: false,
})

const { send: getColumnsSend } = useRequest(data => apis.getColumns(data), {
  immediate: false,
})

const onSourceRepoChange = val => {
  config.value.source.schemaName = ''
  config.value.source.tableName = ''
  getSchemasSend({ repositoryId: val }).then(data => {
    state.sourceSchemaList = data
  })
}

const onTargetRepoChange = val => {
  config.value.target.schemaName = ''
  config.value.target.tableName = ''
  getSchemasSend({ repositoryId: val }).then(data => {
    state.targetSchemaList = data
  })
}

const buildJson = job => {
  const sourceRepository = repoList.value.find(t => t.id === job.reader.repoId)
  const targetRepository = repoList.value.find(t => t.id === job.writer.repoId)
  const dataxJob = {}
  dataxJob.setting = job.setting
  const reader = {
    name: sourceRepository?.reader,
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
  if (job.reader.type === 1) {
    reader.parameter.connection[0].querySql = [job.reader.querySql]
  } else {
    reader.parameter.connection[0].table = [
      job.reader.schemaName + '.' + job.reader.tableName,
    ]
    reader.parameter.splitPk = job.reader.splitPk
    reader.parameter.column = job.reader.columns
  }

  const writer = {
    name: targetRepository?.writer,
    parameter: {
      username: targetRepository.username,
      password: targetRepository.password,
      batchSize: job.writer.batchSize,
      connection: [
        {
          jdbcUrl: targetRepository.url,
          table: [job.writer.schemaName + '.' + job.writer.tableName],
        },
      ],
    },
  }
  writer.parameter.column = job.writer.columns

  writer.parameter.preSql = job.writer.preSql.split(';')
  if (job.writer.truncateTable) {
    writer.parameter.preSql.push(
      'truncate table ' + job.writer.schemaName + '.' + job.writer.tableName,
    )
  }

  writer.parameter.postSql = job.writer.postSql.split(';')
  const content = []
  content.push({
    reader,
    writer,
  })
  dataxJob.content = content
  return { job: dataxJob }
}

function matchAndRemove(arr1, arr2) {
  let result = []
  for (let i = 0; i < arr1.length; i++) {
    for (let j = 0; j < arr2.length; j++) {
      let left = arr1[i]
      let right = arr2[j]
      if (arr1[i].indexOf('.') > -1) {
        left = arr1[i].substring(arr1[i].indexOf('.') + 1)
      }
      if (arr2[j].indexOf('.') > -1) {
        right = arr2[j].substring(arr2[j].indexOf('.') + 1)
      }
      if (left.toLowerCase() == right.toLowerCase()) {
        result.push({
          left: arr1[i],
          right: arr2[j],
        })
        arr1.splice(i, 1)
        i--
        arr2.splice(j, 1)
        break
      }
    }
  }
  return result
}

function isUpperCaseContainsNumberOrInt(str) {
  if (!str) {
    return false
  }
  const upperCaseStr = str.toUpperCase()
  return upperCaseStr.includes('NUMBER') || upperCaseStr.includes('INT')
}

const buildJob = () => {
  formRef.value.validate().then(val => {
    if (val) {
      let mapperList = matchAndRemove(
        config.value.tableList,
        state.targetTableList.map(t => t.tableName),
      )
      let i = 0
      if (mapperList.length > 0) {
        state.saveLoading = true
      }
      mapperList.forEach(t => {
        let job = cloneDeep(jobTemplate)
        let sourceTable = t.left
        let targetTable = t.right
        job.reader.type = 0
        job.reader.repoId = config.value.source.repoId
        job.reader.schemaName = config.value.source.schemaName
        job.reader.tableName = sourceTable

        job.writer.repoId = config.value.target.repoId
        job.writer.schemaName = config.value.target.schemaName
        job.writer.tableName = targetTable
        job.writer.truncateTable = config.value.target.truncateTable

        Promise.all([
          getColumnsSend({
            repositoryId: job.reader.repoId,
            schemaName: job.reader.schemaName,
            tableName: job.reader.tableName,
          }),
          getColumnsSend({
            repositoryId: job.writer.repoId,
            schemaName: job.writer.schemaName,
            tableName: job.writer.tableName,
          }),
        ]).then(resps => {
          const data = resps[0]
          job.reader.columns = data.map(t => t.columnName)
          const primaryKeyList = data
            .filter(
              t => t.isPrimaryKey && isUpperCaseContainsNumberOrInt(t.dataType),
            )
            .map(t => t.columnName)
          if (primaryKeyList.length >= 1) {
            job.reader.splitPk = primaryKeyList[0]
          }
          job.writer.columns = resps[1].map(t => t.columnName)
          let mapperColumnList = matchAndRemove(
            job.reader.columns,
            job.writer.columns,
          )
          job.reader.columns = mapperColumnList.map(t => t.left)
          job.writer.columns = mapperColumnList.map(t => t.right)
          job.jobName = sourceTable
          job.jobParams = JSON.stringify(buildJson(job))
          job.groupId = config.value.groupId
          job.serverId = config.value.serverId
          saveDataxJobSend(job)
            .then(res => {
              i++
              if (i == mapperList.length) {
                removeTab('BatchCreate')
                state.saveLoading = false
                onRefreshNode(props.data.groupId)
                ElMessage.success(`${i}项任务创建成功！`)
              }
            })
            .finally(() => (state.saveLoading = false))
        })
      })
    }
  })
}

const { send: saveDataxJobSend } = useRequest(data => apis.saveDataxJob(data), {
  immediate: false,
})

const { loading: getTableLoading, send: getTablesSend } = useRequest(
  data => apis.getTables(data),
  {
    immediate: false,
  },
)

const onSourceSchemaChange = val => {
  config.value.source.tableList = []
  getTablesSend({
    repositoryId: config.value.source.repoId,
    schemaName: val,
  }).then(data => {
    state.sourceTableList = data
  })
}

const onTargetSchemaChange = val => {
  config.value.target.tableList = []
  getTablesSend({
    repositoryId: config.value.target.repoId,
    schemaName: val,
  }).then(data => {
    state.targetTableList = data
  })
}

const { data: serverList, send: getServerListSend } = useRequest(
  apis.getServerList,
)

const { data: repoList } = useRequest(apis.getRepo)
const { data: groupList } = useRequest(apis.getDataxJobGroupList)

onMounted(() => {
  config.value.groupId = props.data.groupId
  getServerListSend().then(data => {
    const dataxServer = data.find(t => t.serverName.toLowerCase() === 'datax')
    if (dataxServer) {
      config.value.serverId = dataxServer?.id
    }
  })
})
</script>

<style lang="scss" scoped>
$border: 1px solid var(--el-border-color);

.app-container {
  padding: 12px;
  border: $border;
}

.build-container,
.config-container {
  gap: 12px;
}

.step1 {
  gap: 10px;
  padding: 10px;
  overflow-y: auto;
}
</style>
