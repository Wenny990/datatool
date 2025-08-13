<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import setIntroduction from '/@/utils/setIconfont';
import { useGlobalSettingStore } from '@/stores/config'


// 设置初始化，防止刷新时恢复默认
onBeforeMount(() => {
  // 设置批量第三方 icon 图标
  setIntroduction.cssCdn();
  // 设置批量第三方 js
  setIntroduction.jsCdn();
});

const globalSettingStore = useGlobalSettingStore();
</script>

<script>
import defaults from "@/layout/main/defaults.vue"
import vertical from "@/layout/main/classic.vue"

export default {
  components: {
    defaults,
    vertical
  }
}
</script>

<template>
  <el-config-provider :locale="zhCn" :size="globalSettingStore.defaultSize">
     <component :is="globalSettingStore.layout"></component>
  </el-config-provider>
</template>

<style lang="scss">


body , .app-container {
  background-color: var(--el-bg-color);
}

.app-container{
  padding: 10px;
  border: 1px solid var(--el-border-color);
}

.ogg-container  {
  padding: 10px;
  border: 1px solid var(--el-border-color);
  background-color: var(--el-fill-color-lighter);
  width: 100%;
}

$border: 1px solid var(--el-border-color);

.tree-content {
  border: $border;
  background-color: var(--el-fill-color-blank);
}

.custom-tree-node {
  width: 80%;
  flex: 1;
  margin-right: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .tree-content-opt .el-dropdown {
    visibility: hidden;
  }

  &:hover{
    .tree-content-opt .el-dropdown {
      visibility: visible;
    }
  }
}

.el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content{
  .tree-content-opt .el-dropdown {
    visibility: visible;
  }
}

.tree-content-opt:hover .el-dropdown {
  color: var(--el-color-primary);
}
</style>
