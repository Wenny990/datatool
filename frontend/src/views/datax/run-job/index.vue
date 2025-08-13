<template>
  <div class="mb20 h100 flex-center border pd12" >
    <div class="mb12 flex ">
      <el-button type="primary" @click="fetchKeysData">刷新</el-button>
      <el-button
        type="danger"
        @click="onRemove"
        :disabled="selectData.length === 0"
        >清理
      </el-button>
      <el-radio-group v-model="filterType" class="ml12">
        <el-radio-button value="all"
          >全部（{{ runningJobList.length }}）
        </el-radio-button>
        <el-radio-button value="0">运行中</el-radio-button>
        <el-radio-button value="-1">等待中</el-radio-button>
        <el-radio-button value="1">已成功</el-radio-button>
        <el-radio-button value="2">已失败</el-radio-button>
      </el-radio-group>
    </div>
    <div class="border flex-auto" v-loading="loading">
      <el-table
        :data="computedRunningJobList"
        height="100%"
        @selection-change="handleSelectionChange"
        current-row-key="uuid"
        stripe
      >
        <el-table-column type="selection" width="40" />
        <el-table-column type="index" width="60" />
        <el-table-column label="任务名" show-overflow-tooltip>
          <template v-slot="{ row }">
            {{
              row.jobName +
              (!row.total || row.total === 1
                ? ''
                : '(' + row.current + '/' + row.total + ')')
            }}
          </template>
        </el-table-column>
        <el-table-column
          property="totalRecords"
          label="记录数"
          width="100"
        ></el-table-column>
        <el-table-column
          property="startTime"
          label="开始时间"
          width="160"
          show-overflow-tooltip
        >
        </el-table-column>
        <el-table-column
          property="startTime"
          label="耗时"
          width="140"
          show-overflow-tooltip
        >
          <template v-slot="{ row }">
            {{ getTimeInterval(row.startTime, row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column width="280" property="address" label="进度">
          <template v-slot="{ row }">
            <div class="flex">
              <div class="flex-auto">
                <el-progress
                  :percentage="
                    row.percentage ? row.percentage : row.status === 0 ? 100 : 0
                  "
                  :indeterminate="row.status === 0 && !row.percentage"
                  :duration="3"
                  :status="
                    row.status <= 0
                      ? ''
                      : row.status == 1
                        ? 'success'
                        : 'exception'
                  "
                >
                  <div class="flex" style="gap: 6px" v-if="row.status <= 0">
                    <el-text v-show="row.status < 0">等待</el-text>
                    <el-text v-show="row.status >= 0"
                      >{{ row.percentage ? row.percentage + '%' : '' }}
                    </el-text>
                    <el-icon class="is-loading" v-if="row.status === 0">
                      <Loading/>
                    </el-icon>
                  </div>
                </el-progress>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="140" label="操作" fixed="right">
          <template v-slot="{ row }">
            <el-tooltip content="重试" v-if="row.status == 2">
              <el-button
                @click="reload(row)"
                type="warning"
                circle
                plain
                :icon="RefreshRight"
              >
              </el-button>
            </el-tooltip>
            <el-tooltip content="中止" v-if="row.status == 0">
              <el-button
                @click="kill(row.uuid)"
                type="danger"
                circle
                plain
                :icon="SwitchButton"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="详情">
              <el-button
                @click="toDetail(row)"
                type="primary"
                circle
                plain
                :icon="Search"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup name="RunJob">
import {
  ChatLineRound, Loading,
  RefreshRight,
  Search,
  SwitchButton
} from '@element-plus/icons-vue'
import { useDataXStore } from '@/stores/datax'
import { storeToRefs } from 'pinia'
import { useIntervalFn, useWebNotification } from '@vueuse/core'
import apis from '@/service/apis'
import successIcon from '@/assets/success.png'
import warningIcon from '@/assets/warning.png'
import { ElMessage, ElNotification } from 'element-plus'
import { getTimeInterval } from '@/utils/formatTime'
import { useRequest } from 'alova/client'

const state = reactive({
  dialogVisible: false,
})

const dataxStore = useDataXStore()

const { runningJobList } = storeToRefs(dataxStore)
const { addTab, removeTabList } = dataxStore

const {
  getExecuteLog,
  stopExecuteLog,
  getAllExecuteLogKeys,
  removeExecuteLog,
  executeDataxJob,
} = apis

const filterType = ref('all')

const title = ref('')
const { isSupported, show, onClick } = useWebNotification()
const selectData = ref([])

const { send: getExecLogSend } = useRequest( data => getExecuteLog(data), {
  immediate: false
})

let runningVersion = 0;
const fetchData = async () => {
  runningVersion = runningVersion++;
  let currentVersion = runningVersion;
  pause()
  const temp = []
  const length = runningJobList.value.length
  for (let i = 0; i < length; i++) {
    if(currentVersion !== runningVersion ){
      break;
    }
    let currentRow = runningJobList.value[i]
    if (currentRow.status < 1 ) {
      const respData = await getExecLogSend(currentRow.uuid)
      runningJobList.value[i] = respData;
      //已完成
      if (respData.status >= 1) {
        if (respData.hasNext) {
          temp.push({
            uuid: respData.nextUuid,
            status: -1,
          })
        }
        let isSuccess = respData.result === 0
        title.value =
          '任务【' +
          respData.jobName +
          '】执行' +
          (isSuccess ? '成功' : '失败') +
          '!'
        ElNotification({
          title: 'Datax运行完成',
          message: title.value,
          position: 'bottom-right',
          type: isSuccess ? 'success' : 'warning',
          duration: 1500,
        })
        if (isSupported) {
          await show({
            title: title.value,
            dir: 'auto',
            lang: 'zh-CN',
            renotify: true,
            icon: isSuccess ? successIcon : warningIcon,
            tag: respData.uuid,
          })
          onClick(() => {
            state.dialogVisible = true
          })
        }
      }
    }
  }
  if (
    runningJobList.value.length > 0 &&
    runningJobList.value.findIndex(t => t.status <= 0) > -1
  ) {
    resume()
  }
  if (temp.length > 0) {
    runningJobList.value.unshift(...temp)
  }
}

const handleSelectionChange = val => {
  selectData.value = val
}

const computedRunningJobList = computed(() => {
  return filterType.value === 'all'
    ? runningJobList.value
    : runningJobList.value.filter(t => t.status == filterType.value)
})

watch(
  () => runningJobList.value.length,
  () => {
    fetchData()
  },
)

const kill = uuid => {
  apis.stopExecuteLog(uuid).send()
}

const reload = data => {
  apis.executeDataxJob(data.jobId).send().then(newUuid => {
    ElMessage.success('任务提交成功，返回任务标识：' + newUuid)
    apis.removeExecuteLog({
      keys: [data.uuid],
    }).then(() => {
      removeTabList(['BackupExecute-' + data.uuid])
      runningJobList.value.splice(
        runningJobList.value.findIndex(t => t.uuid === data.uuid),
        1,
      )
      runningJobList.value.unshift({
        uuid: newUuid,
        jobName: data.jobName,
        status: -1,
      })
      fetchData()
    })
  })
}

const onRemove = () => {
  apis.removeExecuteLog({
    keys: selectData.value.map(t => t.uuid),
  }).send().then(respData => {
    removeTabList(selectData.value.map(t => 'BackupExecute-' + t.uuid))
    ElMessage.success('删除成功！' + respData)
    fetchKeysData()
  })
}

const toDetail = data => {
  const data1 = {
    title: '任务运行情况-' + data.jobName,
    key: 'BackupExecute-' + data.uuid,
    type: 'BackupExecute',
    data: data.uuid,
  }
  addTab(data1)
  state.dialogVisible = false
}

const { pause, resume, isActive } = useIntervalFn(fetchData, 3000)

const showDialog = () => {
  state.dialogVisible = true
}

const {  send, loading } = useRequest(getAllExecuteLogKeys, { immediate: false })

const fetchKeysData = async () => {
  send().then(data => {
    data.sort((a, b) => b.startTime - a.startTime)
    runningJobList.value = data
  })
  fetchData()
}

onMounted(() => {
  fetchKeysData()
})
</script>

<style scoped lang="scss">
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.rotating {
  animation: rotate 3s infinite linear;
}
</style>
