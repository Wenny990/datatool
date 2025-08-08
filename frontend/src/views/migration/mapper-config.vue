<script setup>
import { useRequest } from 'alova/client'
import apis from '@/service/apis'
import { useMigrationStore } from '@/stores/migration'
import { ElMessage } from 'element-plus'
import { computed } from 'vue'

const sourceDataSourceType = ref(null);
const targetDataSourceType = ref(null);

const sourceType = ref('')
const targetType = ref('')

const { getRepoMappingSend } = useMigrationStore()

const {
  mappingDialog,
  mappingListData,
  mappingListLoading,
  repoConfigList,
  targetState,
  sourceState,
  columnTypeList
} = storeToRefs(useMigrationStore())

const currMappingList = computed(() =>
  mappingListData.value.filter(
    t => t.sourceId === sourceDataSourceType.value && t.targetId === targetDataSourceType.value,
  ),
)

const sourceColumnTypeList = computed(() =>
  columnTypeList.value.filter(t => t.repoTypeId ===  sourceDataSourceType.value),
)

const targetColumnTypeList = computed(() =>
  columnTypeList.value.filter(
    t => t.repoTypeId === targetDataSourceType.value,
  )
)

watch(
  () => [sourceState.currRepo, targetState.currRepo],
  () => {
    sourceType.value = null
    targetType.value = null
    currRow.value = null
  },
)

const { loading: saveLoading, send: saveRepoMappingSend } = useRequest(
  data => apis.saveRepoMapping(data),
  {
    immediate: false,
  },
)

const currRow = ref({})

const handleCurrentChange = row => {
  currRow.value = row
}

const { loading: removeLoading, send: removeRepoMappingSend } = useRequest(
  data => apis.deleteRepoMapping(data),
  {
    immediate: false,
  },
)

const handleRemoveMapping = () => {
  if(currRow.value.id){
    removeRepoMappingSend(currRow.value.id).then(resp => {
      getRepoMappingSend()
      ElMessage.success('删除成功！')
      clearCurrRow()
    })
  }
}

const mappingTableRef = ref()

const clearCurrRow = () => {
  mappingTableRef.value.setCurrentRow()
  currRow.value = {}
}

const handleAdd = () => {
  saveRepoMappingSend({
    sourceType: sourceType.value,
    targetType: targetType.value,
    sourceId: sourceDataSourceType.value,
    targetId: targetDataSourceType.value,
  }).then(() => {
    getRepoMappingSend()
    clearCurrRow()
    ElMessage.success('保存成功！')
    sourceType.value = null
    targetType.value = null
  })
}
</script>

<template>
  <div>
    <el-dialog v-model="mappingDialog" title="字段映射">
      <div class="flex-center gap10">
        <div class="flex gap20">
          <el-select v-model="sourceDataSourceType" style="width: 240px">
            <el-option
              v-for="item in repoConfigList"
              :label="item.dbName"
              :value="item.id"
            ></el-option>
          </el-select>
          <el-select v-model="targetDataSourceType" style="width: 240px">
            <el-option
              v-for="item in repoConfigList"
              :label="item.dbName"
              :value="item.id"
            ></el-option>
          </el-select>
        </div>
        <div class="flex gap20">
          <el-select
            v-model="sourceType"
            style="width: 240px"
            placeholder="源类型"
            filterable
            clearable
            default-first-option
            allow-create
          >
            <el-option
              v-for="item in sourceColumnTypeList"
              :label="item.columnType"
              :value="item.columnType"
            ></el-option>
          </el-select>
          <el-select
            v-model="targetType"
            style="width: 240px"
            placeholder="目标类型"
            filterable
            clearable
            default-first-option
            allow-create
          >
            <el-option
              v-for="item in targetColumnTypeList"
              :label="item.columnType"
              :value="item.columnType"
            ></el-option>
          </el-select>
          <div>
            <el-button
              type="success"
              @click="handleAdd"
              :disabled="!sourceType || !targetType"
              v-loading="saveLoading"
            >
              添加
            </el-button>
            <el-button
              type="danger"
              v-loading="removeLoading"
              :disabled="!currRow?.id"
              @click="handleRemoveMapping"
              >删除</el-button
            >
          </div>
        </div>
        <div>
          <el-table
            ref="mappingTableRef"
            :data="currMappingList"
            v-loading="mappingListLoading"
            border
            height="50vh"
            highlight-current-row
            current-row-key="id"
            row-key="id"
            @current-change="handleCurrentChange"
          >
            <el-table-column type="index"></el-table-column>
            <el-table-column
              prop="sourceType"
              label="源端类型"
            ></el-table-column>
            <el-table-column
              prop="targetType"
              label="目标类型"
            ></el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss"></style>
