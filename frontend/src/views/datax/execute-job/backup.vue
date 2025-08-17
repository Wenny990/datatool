<template>
  <div class="h100 executor-container flex-center app-container">
    <div class="mb12">
      <el-descriptions
        class="margin-top"
        title="运行情况"
        :column="2"
        border
      >
        <template #title>
          <div class="el-descriptions__title flex justify-center align-center gap6">
            <el-icon color="var(--el-color-primary)" size="16"><HelpFilled /></el-icon>
            <el-text>运行情况</el-text>
          </div>
        </template>
        <template #extra>
          <el-button type="primary" @click="fetchData">刷新</el-button>
          <el-button type="danger" @click="kill" v-show="jobLog.status === 0"
            >中止
          </el-button>
        </template>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">任务名称</div>
          </template>
          {{ jobLog.jobName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">任务标识</div>
          </template>
          {{ jobLog.uuid }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">开始时间</div>
          </template>
          {{ jobLog.startTime }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">结束时间</div>
          </template>
          {{ jobLog.endTime ?? '未结束' }}
        </el-descriptions-item>

        <el-descriptions-item>
          <template #label>
            <div class="cell-item">记录数</div>
          </template>
          {{ jobLog.totalRecords }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">速度</div>
          </template>
          {{ jobLog.speed  }}
        </el-descriptions-item>

        <el-descriptions-item :span="2">
          <template #label>
            <div class="cell-item">进度</div>
          </template>

          <div class="flex-auto">
            <el-progress
              :percentage="
                jobLog.percentage
                  ? jobLog.percentage
                  : jobLog.status === 0
                    ? 100
                    : 0
              "
              :indeterminate="jobLog.status === 0 && !jobLog.percentage"
              :duration="3"
              :status="
                jobLog.status <= 0
                  ? ''
                  : jobLog.status == 1
                    ? 'success'
                    : 'exception'
              "
            >
              <div class="flex" style="gap: 6px" v-if="jobLog.status <= 0">
                <el-text v-show="jobLog.status < 0">等待</el-text>
                <el-text v-show="jobLog.status >= 0"
                  >{{ jobLog.percentage ? jobLog.percentage + '%' : '' }}
                </el-text>
                <el-icon class="is-loading" v-if="jobLog.status === 0">
                  <Loading />
                </el-icon>
              </div>
            </el-progress>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </div>
    <div class="flex-auto">
      <code-editor
        v-model="jobLog.logContent"
        placeholder="运行日志"
        type="sql"
        :disabled="true"
        :is-bottom="true"
        height="100%"
      ></code-editor>
    </div>
  </div>
</template>

<script setup>
import apis from '@/service/apis'
import CodeEditor from '@/components/codeEditor/index.vue'
import { useIntervalFn } from '@vueuse/core'
import { onMounted } from 'vue'
import { useRequest } from 'alova/client'
import { HelpFilled, Loading } from '@element-plus/icons-vue'

const props = defineProps({
  data: {
    type: String,
    required: true,
  },
})

const {
  send,
  data: jobLog,
  loading,
} = useRequest(data => apis.getExecuteLogDetail(data), {
  immediate: true,
  initialData: {},
})

const fetchData = async () => {
  pause()
  await send(props.data)
  if (jobLog.value.status <= 0) {
    resume()
  }
}

const { pause, resume, isActive } = useIntervalFn(fetchData, 5000)

const kill = () => {
  apis.stopExecuteLog(props.data).send()
}

onMounted(() => {
  setTimeout(() => {
    fetchData()
  },500)
})
</script>

<style scoped>
.executor-container {
  padding: 12px;
}

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
