<template>
  <div class="h100">
    <div class="flex h100">
      <div style="width:280px;" class="h100 mr10 border flex-center">
        <div class="pt6 pb6 pl6 border-b">
          <el-input size="small" v-model="state.keyword" style="width:82%" class="mr6" clearable></el-input>
          <el-tooltip
              effect="dark"
              content="刷新"
          >
            <el-button type="primary" :icon="Refresh" circle size="small" @click="fetchData"/>
          </el-tooltip>
        </div>
        <div class="flex-auto">
          <el-tree
              ref="treeRef"
              class="h100 w100"
              style="overflow: auto"
              :data="data"
              :props="{
                  children: 'children',
                  label: 'name',
                  isLeaf: 'isLeaf'
              }"
              lazy
              :load="onLoadChildren"
              @node-click="handleNodeClick"
              :filter-node-method="filterNode"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node" :title="node.label + ' ' +(data.tableComment??'') ">
                <div class="tree-content-label">
                  <span class="mr6">
                    <el-badge is-dot type="success" :hidden="!data.isLoaded">
                      <svg-icon v-if="data.type === 'schema'" name="iconfont icon-shujuku" color="#ffc107"></svg-icon>
                      <svg-icon v-else-if="data.type === 'table'" name="iconfont icon-file" color="#538cfa"></svg-icon>
                      <svg-icon v-else :name="getIcon(data)"></svg-icon>
                    </el-badge>
                  </span>
                  <span class="mr20">{{ node.label }}</span>
                </div>
                <div class="tree-content-opt">
                  <el-text v-if="data.dataSourceName" truncated>
                    {{data.host + ':' + data.port}}
                  </el-text>
                  <el-text v-else-if="data.type === 'table'" truncated>
                    {{data.tableComment}}
                  </el-text>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>
      <div class="flex-auto h100 right-panel">
        <el-tabs v-model="state.activeName" class="h100 flex-center">
          <el-tab-pane label="表结构"  name="tableStructure" class="h100" lazy>
            <table-view :table-data="state.tableData" v-loading="state.columnLoading"  border height="100%" size="small">
              <template #isPrimaryKey="{data}">
                <svg-icon name="ele-Select" v-show="data.isPrimaryKey == 1"></svg-icon>
              </template>
              <template #nullable="{data}">
                <svg-icon name="ele-Select" v-show="data.nullable == 1"></svg-icon>
              </template>
            </table-view>
          </el-tab-pane>
          <el-tab-pane label="表数据" name="tableData" class="h100" lazy>
            <table-data :currTable="currTable"></table-data>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>

</template>

<script setup>
import {useRepoApi} from '/@/api/repo/index.js'
import TableView from '/@/components/tableview/index.vue'
import SvgIcon from "/@/components/svgIcon/index.vue";
import {Refresh} from '@element-plus/icons-vue';
import TableData from "/@/views/repo/table-data.vue";

const {getRepo, getSchemas, getTables, getColumns} = useRepoApi();

const data = ref([])

const columnData = ref([])

const state = reactive({
  loading: false,
  columnLoading: false,
  tableData: {
    tableHeader: [{
      type: 'index',
      width: '60'
    }, {
      prop: 'columnName',
      label: '字段名'
    }, {
      prop: 'columnComment',
      label: '注释'
    }, {
      prop: 'dataType',
      label: '字段类型',

    }, {
      prop: 'dataLength',
      label: '字段长度',
    }, {
      label: '主键',
      type: 'slot',
      slotName: 'isPrimaryKey',
    }, {
      label: '允许为空',
      type: 'slot',
      slotName: 'nullable',

    }],
    tableList: []
  },
  keyword: '',
  activeName: 'tableStructure'
})

const fetchData = () => {
  state.loading = true
  getRepo().then(response => {
    data.value = response.data
  }).finally(() => {
    state.loading = false
  })
}

const treeRef = ref()

 watch(() => state.keyword, (val) => {
   treeRef.value.filter(val)
 })

const filterNode = (value, data) => {
  if (!value) return true
  return data.name.toUpperCase().includes(value.toUpperCase())
}

const currTable = ref({});

const handleNodeClick = (data) => {
  if (data.type === 'table') {
    currTable.value = data;
    state.columnLoading = true;
    getColumns(data).then(resp => {
      columnData.value = resp.data
      state.tableData.tableList = resp.data
    }).finally(() => state.columnLoading = false)
  }
}

const onLoadChildren = (node, resolve) => {
  if (node.data.url) {
    getSchemas(node.data.id).then(resp => {
      resolve(resp.data.map(t => {
        return {
          ...t,
          name: t['schemaName'],
          isLeaf: false,
          type: 'schema',
          id: node.data.id,
          schemaName: t['schemaName'],

        }
      }))
      node.data.isLoaded = true
    })
  }

  if (node.data.type === 'schema') {
    getTables(node.data).then(resp => {
      resolve(resp.data.map(t => {
        return {
          name: t['tableName'],
          schemaName: node.data.schemaName,
          tableName: t['tableName'],
          isLeaf: true,
          id: node.data.id,
          type: 'table',
          remarks: t.remarks
        }
      }))
      node.data.isLoaded = true
    })
  }
}

const iconArray = ['icon-sqlserver','icon-mysql','icon-shujukuleixingtubiao-kuozhan-','icon-postgresql','icon-a-ziyuan40']
const getIcon = (data) => {
  return 'iconfont ' + iconArray[data.dataProviderType - 1];
}

onMounted(() => {
  fetchData();
})





</script>

<style scoped lang="scss">
$border: 1px solid var(--el-border-color);

.tree-content {
  border: $border;
}

.custom-tree-node {
  flex: 1;
  margin-right: 12px;
  display: flex;
  justify-content: space-between;
}

.tree-content-opt .el-text{
  color: var(--el-text-color-placeholder);
  //斜体
  font-style: italic;
  font-size: 12px;

}
</style>

