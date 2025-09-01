<!-- CustomCard.vue -->
<template>
  <div
    class="flex-center"
    :class="['custom-card', shadowClass]"
  >
    <div class="custom-card-header">
      <slot name="header">{{ header }}</slot>
    </div>
    <div class="custom-card-body flex-auto flex-center" :style="bodyStyle" :class="{pd10: noScroll}">
      <el-scrollbar
        v-if="!noScroll"
        :style="{ height: '100%' }"
        class="custom-card-body-s pd10"
      >
        <slot></slot>
      </el-scrollbar>
      <slot v-else></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  header: {
    type: String,
    default: ''
  },
  bodyStyle: {
    type: Object,
    default: () => ({})
  },
  shadow: {
    type: String,
    default: 'hover' // always / hover / never
  },
  scrollHeight: {
    type: [String, Number],
    default: '400px'
  },
  noScroll: {
    type: Boolean,
    default: false
  }
})

const shadowClass = computed(() => {
  return `is-${props.shadow}-shadow`
})
</script>

<style scoped>
.custom-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  background-color:  var(--el-card-bg-color, #ffffff);
  border: 1px solid var(--el-card-border-color, #e4e7ed);
}

.custom-card :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.custom-card :deep(.el-scrollbar) {
  flex: 1;
}



.custom-card-header {
  border-bottom: 1px solid var(--el-card-border-color, #ebeef5);
  box-sizing: border-box;
  padding: calc(var(--el-card-padding, 12px) - 2px) var(--el-card-padding, 12px);
}
</style>
