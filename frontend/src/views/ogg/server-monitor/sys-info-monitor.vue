<template>
  <div class="sys-info-monitor flex">
    <div class="monitor-item">
      <div class="title">主机名称</div>
      <div>{{ props.sys.computerName }}</div>
    </div>
    <div class="monitor-item">
      <div class="title">操作系统</div>
      <div>{{ props.sys.osName }}</div>
    </div>
    <div class="monitor-item">
      <div class="title">运行时间</div>
      <div>{{ getTimeInterval(props.sys.lastBootUpTime) }}</div>
    </div>
  </div>
</template>

<script setup>

import {  getTimeInterval} from "/@/utils/formatTime";

const props = defineProps({
  sys: {
    type: Object,
    required: true,
    default: () => {}
  }
})

</script>

<style scoped lang="scss">
.sys-info-monitor {
  gap: 10px;
  margin: 12px 8px;
}

.monitor-item {
  padding: 6px;
  width: 220px;
  height: 74px;
  background-color: rgba(98, 106, 239, 0.24);
  border-radius: 8px;
  border: 2px  solid #abdcff;
  color: #e0dddd;

  .title {
    margin-bottom: 6px;
    font-weight: bold;
    font-size: 14px;
    color: #ffffff  ;
  }
}
</style>