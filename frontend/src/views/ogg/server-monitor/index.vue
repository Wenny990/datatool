<template>
  <div class="h100 w100 flex-center monitor-container" v-loading="loading">
    <div style="position: relative">
      <sys-info-monitor :sys="serverMonitorInfo.sys"></sys-info-monitor>
      <div style="position: absolute; right: 10px; top: 12px">
        <el-tooltip content="刷新">
          <svg-icon name="ele-Refresh" @click="fetchData" :size="16"></svg-icon>
        </el-tooltip>
      </div>
    </div>
    <div class="flex-auto flex">
      <div class="flex-auto">
        <cpu-monitor :cpu="serverMonitorInfo.cpu"></cpu-monitor>
      </div>
      <div class="flex-auto">
        <mem-monitor :mem="serverMonitorInfo.mem"></mem-monitor>
      </div>
    </div>
    <div class="flex-auto">
      <sys-files-monitor
        :sys-files="serverMonitorInfo.sysFiles"
      ></sys-files-monitor>
    </div>
  </div>
</template>

<script setup>
import SysFilesMonitor from './sys-files-monitor.vue'
import CpuMonitor from './cpu-monitor.vue'
import MemMonitor from './mem-monitor.vue'
import SysInfoMonitor from './sys-info-monitor.vue'
import apis from '@/service/apis'
import { useRequest } from 'alova/client'

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const {
  loading,
  send,
  data: serverMonitorInfo,
} = useRequest(data => apis.monitorServer(data), {
  immediate: false,
  initialData: {
    sys: {},
    cpu: {},
    mem: {},
    sysFiles: [],
  },
})

const fetchData = () => {
  send(props.data.id)
}

watch(
  () => props.data.id,
  () => {
    fetchData()
  },
)

onMounted(() => {
  console.log()
  fetchData()
})
</script>

<style scoped lang="scss">
.monitor-container {
  background-color: #282a36;
}
</style>
