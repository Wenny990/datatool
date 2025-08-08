<template>
  <div ref="target" class="right-menu-container" :style="{'--show-left':props.position.x,'--show-top':props.position.y}">
    <div class="menu-item" v-for="(item) in props.menuList" :key="item.name" @click="props.onClick(item.name)">
      <div></div>
      <div> {{ item.label }}</div>
    </div>
  </div>
</template>

<script setup>
import {onClickOutside} from '@vueuse/core'

const props = defineProps({
  position: {
    type: Object,
  },
  menuList: {
    type: Array,
    required: true
  },
  onClick: {
    type: Function
  },
  onRemove: {
    type: Function
  }
})

const target = ref()


onClickOutside(target, () => props.onRemove && props.onRemove())

</script>

<style scoped lang="scss">
$border: 1px solid var(--el-border-color);

.right-menu-container {
  position: fixed;
  left: calc(var(--show-left) * 1px);
  top: calc(var(--show-top) * 1px);
  z-index: 9999;
  border: $border;
  background-color: var(--el-bg-color);

  .menu-item {
    display: flex;
    width: 100px;
    height: 28px;
    line-height: 28px;
    color: var(--el-text-color-regular);
    font-size: 12px;
    padding: 0 1.2em;
    &:hover {
      color: var(--el-color-primary);
      font-weight: 500;
      background-color: var(--el-color-primary-light-9);
    }

    transition: all .5s;
  }
}
</style>