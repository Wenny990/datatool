<template>
  <div class="ogg-container h100">
    <div class="flex h100">
      <div style="width:280px" class="mr12">
        <group-tree/>
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
import GroupTree from './group-tree/index.vue'
import {storeToRefs} from "pinia"
import ElTabPro from "@/components/el-tab-pro/index.vue";
import {useDataXStore} from "@/stores/datax";

const dataxStore = useDataXStore();
const {componentList, currComponent} = storeToRefs(dataxStore);

</script>

<script>
export default {
  components: {
    JobRun: defineAsyncComponent(() => import('./run-job/index.vue')),
    JobBuild: defineAsyncComponent(() => import('./job-build/index.vue')),
    BatchCreate: defineAsyncComponent(() => import('./batch-create/index.vue')),
    BackupExecute: defineAsyncComponent(() => import('./execute-job/backup.vue'))
  }
}
</script>

<style scoped lang="scss">


.tab-pane {
  background-color: var(--el-fill-color-blank);
}
</style>

