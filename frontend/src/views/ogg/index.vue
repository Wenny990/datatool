<template>
  <div class="ogg-container h100">
    <div class="flex h100">
      <div style="width:280px" class="mr12">
        <server-tree/>
      </div>
      <div class="flex-auto">
        <el-tab-pro :component-list="componentList" v-model="currComponent">
          <template #item="item">
            <component :is="item.type" :data="item.data"></component>
          </template>
        </el-tab-pro>
      </div>
    </div>
  </div>

</template>

<script setup name="OggProcess">
import ServerTree from './server-tree/index.vue'
import {useProcessStore} from "/@/stores/process";
import {storeToRefs} from "pinia"
import ElTabPro from "/@/components/el-tab-pro/index.vue";

const processStore = useProcessStore();
const { componentList, currComponent} = storeToRefs(processStore);


</script>

<script>
export default {
  components: {
    // CreateProcess: defineAsyncComponent(() => import('./create-process/index.vue')),
    ProcessInfo: defineAsyncComponent(() => import('./process-info/index.vue')),
    // ServerShell: defineAsyncComponent(() => import('./server-shell/index.vue')),
    OggMonitor: defineAsyncComponent(() => import('./monitor/index.vue')),
    ServerMonitor: defineAsyncComponent(() => import('./server-monitor/index.vue')),
    CommandPreview: defineAsyncComponent(() => import('./command-preview/index.vue')),
    // ServerShell: defineAsyncComponent(() => import('/@/components/shell/index.vue')),
  }
}
</script>

<style scoped lang="scss">


</style>

