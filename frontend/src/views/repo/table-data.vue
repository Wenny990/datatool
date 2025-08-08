<template>
  <div class="h100 flex-center" v-loading="state.loading">
    <div>
      <el-form-item label="WHERE条件" size="small" class="mb2">
        <el-input v-model="state.where" size="small" @keydown.enter="onSearch"></el-input>
      </el-form-item>
    </div>
    <div class="flex-auto">
      <table-view :table-data="state.tableData" v-loading="state.columnLoading" border height="100%" size="small">
      </table-view>
    </div>
    <div>
      <el-pagination  class="pd4" layout="total, prev, pager, next, sizes" small
                      :page-sizes="[10, 20, 50, 100, 200]"
                      @current-change="handleCurrentChange"
                      @sizeChange="handleSizeChange"
                      :current-page="state.request.current"
                      :total="state.request.total"
                      :page-size="state.request.pageSize"/>
    </div>
  </div>
</template>

<script setup>

import TableView from "/@/components/tableview/index.vue";
import {useRepoApi} from "/@/api/repo";
import {watch} from "vue";

const state = reactive({
  loading: false,
  tableData: {
    tableHeader: [],
    tableList: []
  },
  request: {
    current: 1,
    pageSize: 20,
    sqlText: '',
    repoId: null,
    total: 0
  },
  where: ''
})

const props = defineProps({
  currTable: {
    type: Object,
    default: () => {
    }
  }
})

const {execPageQuery} = useRepoApi();


const handleCurrentChange = (val) => {
  state.request.current = val
  fetchData()
}

const handleSizeChange = (val) => {
  state.request.pageSize = val
  fetchData()
}


const fetchData = () => {
  state.loading = true;
  execPageQuery(state.request).then(resp => {
    state.request.total = resp.data.total;
    if(resp.data.records && resp.data.records.length > 0){
      state.tableData.tableHeader = Object.keys(resp.data.records[0]).map(t => {
        return {
          prop: t,
          label: t
        }
      })
    }
    state.tableData.tableList = resp.data.records;
  }).finally(() => state.loading = false)
}

const onSearch = () => {
  state.request.repoId = props.currTable.id;
  state.request.sqlText = 'select * from ' + props.currTable.schemaName + '.' + props.currTable.tableName;
  if( state.where && state.where.trim() !== ''  ){
    state.request.sqlText += ' where ' + state.where;
  }
  if (state.request.repoId && state.request.sqlText) {
    fetchData();
  }
}

watch(() => props.currTable, (val) => {
  onSearch()
}, {
  immediate: true
})

</script>

<style scoped lang="scss">

</style>
