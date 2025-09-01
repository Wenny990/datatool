<template>
  <div class="database-browser h100 flex-center" v-loading="loading" >
    <div class="browser-toolbar">
      <el-input
        v-model="searchText"
        placeholder="搜索表名..."
        :prefix-icon="Search"
        clearable
        size="small"
        @input="handleSearch"
      />
      <el-button size="small" :icon="Refresh" @click="refreshTree">
        刷新
      </el-button>
    </div>

    <div class="tree-container flex-auto">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        :highlight-current="true"
        :filter-node-method="filterNode"
        @node-click="handleNodeClick"
        @node-expand="handleNodeExpand"
        lazy
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <svg-icon
              :name="getNodeIcon(data)"
              :class="getNodeIconClass(data)"
              :color="getNodeIconClass(data)"
            ></svg-icon>
            <span class="node-label">{{ node.label }}</span>
            <div class="node-actions" v-if="data.type === 'table'">
              <el-tooltip content="查看表结构" placement="top">
                <div><el-button
                  size="small"
                  type="primary"
                  text
                  :icon="List"
                  @click.stop="viewTableStructure(data)"
                /></div>

              </el-tooltip>

              <el-tooltip content="浏览数据" placement="top">
                <div><el-button
                  size="small"
                  type="success"
                  text
                  :icon="View"
                  @click.stop="viewTableData(data)"
                /></div>
              </el-tooltip>

              <el-tooltip content="统计信息" placement="top">
                <div><el-button
                  size="small"
                  type="info"
                  text
                  :icon="DataBoard"
                  @click.stop="viewTableStats(data)"
                /></div>
              </el-tooltip>
            </div>

            <div
              class="node-badge"
              v-if="data.type === 'schema' && data.tableCount"
            >
              <el-tag size="small" type="info">{{ data.tableCount }}</el-tag>
            </div>

            <div
              class="node-badge"
              v-if="
                data.type === 'datasource' &&
                data.connectionStatus !== undefined
              "
            >
              <el-tag
                size="small"
                :type="data.connectionStatus === 1 ? 'success' : 'danger'"
              >
                {{ data.connectionStatus === 1 ? '在线' : '离线' }}
              </el-tag>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 右键菜单 -->
    <el-dropdown
      ref="contextMenuRef"
      :teleported="false"
      trigger="contextmenu"
      @command="handleContextMenu"
    >
      <div></div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="refresh" :icon="Refresh">
            刷新
          </el-dropdown-item>
          <el-dropdown-item
            command="copy"
            :icon="CopyDocument"
            v-if="contextNode?.type === 'table'"
          >
            复制表名
          </el-dropdown-item>
          <el-dropdown-item
            command="export"
            :icon="Download"
            v-if="contextNode?.type === 'table'"
          >
            导出数据
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 表统计信息对话框 -->
    <el-dialog
      v-model="statsDialogVisible"
      :title="`表统计 - ${selectedTable?.tableName}`"
      width="600px"
    >
      <div v-loading="statsLoading">
        <div v-if="tableStats">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="表类型">
              <el-tag
                :type="tableStats.tableType === 'TABLE' ? 'primary' : 'success'"
              >
                {{ tableStats.tableType === 'TABLE' ? '数据表' : '视图' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="记录数">
              {{ formatNumber(tableStats.rowCount) }}
            </el-descriptions-item>
            <el-descriptions-item label="字段数">
              {{ tableStats.columnCount }}
            </el-descriptions-item>
            <el-descriptions-item label="索引数">
              {{ tableStats.indexCount }}
            </el-descriptions-item>
            <el-descriptions-item label="表备注" :span="2">
              {{ tableStats.remarks || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  CopyDocument,
  DataBoard,
  Download,
  List,
  Refresh,
  Search,
  View,
} from '@element-plus/icons-vue'
import apis from '@/service/apis'
import SvgIcon from '@/components/svgIcon/index.vue'

const props = defineProps({
  dataSources: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['datasource-selected', 'table-selected'])

// 响应式数据
const loading = ref(false)
const statsLoading = ref(false)
const treeRef = ref()
const contextMenuRef = ref()
const searchText = ref('')
const treeData = ref([])
const contextNode = ref(null)
const selectedTable = ref(null)
const statsDialogVisible = ref(false)
const tableStats = ref(null)

// 树形控件属性配置
const treeProps = {
  children: 'children',
  label: 'label',
  isLeaf: 'isLeaf',
}

// 监听数据源变化
watch(
  () => props.dataSources,
  newDataSources => {
    if (newDataSources && newDataSources.length > 0) {
      loadTreeData()
      treeRef.value.setCurrentKey(treeData.value[0]?.id)
      handleNodeClick(treeData.value[0])
    }
  },
  { immediate: true },
)

// 方法
const loadTreeData = () => {
  loading.value = true
  try {
    // 构建三层树结构：数据源 → Schema → 表
    treeData.value = props.dataSources.map(dataSource => ({
      id: `datasource_${dataSource.id}`,
      label: dataSource.name,
      type: 'datasource',
      repositoryId: dataSource.id,
      repositoryName: dataSource.repositoryName,
      connectionStatus: dataSource.connectionStatus,
      databaseType: dataSource.databaseType,
      children: [],
      isLeaf: false,
    }))
  } catch (error) {
    ElMessage.error('加载数据源失败')
    console.error('加载数据源失败:', error)
  } finally {
    loading.value = false
  }
}

const loadSchemas = async repositoryId => {
  try {
    const schemas = await apis.getSchemaList(repositoryId)
    return schemas.map(schema => ({
      id: `schema_${repositoryId}_${schema.schemaName}`,
      label: schema.schemaName || 'default',
      type: 'schema',
      schemaName: schema.schemaName,
      repositoryId: repositoryId,
      children: [],
      isLeaf: false,
    }))
  } catch (error) {
    ElMessage.error(`加载Schema失败: ${error.message}`)
    return []
  }
}

const loadTables = async (repositoryId, schemaName) => {
  try {
    const response = await apis.getTableList({
      repositoryId: repositoryId,
      schemaName: schemaName,
    })

    return response.map(table => ({
      id: `table_${repositoryId}_${schemaName}_${table.tableName}`,
      label: table.tableName,
      type: 'table',
      schemaName: schemaName,
      tableName: table.tableName,
      tableType: table.tableType,
      remarks: table.remarks,
      repositoryId: repositoryId,
      isLeaf: true,
    }))
  } catch (error) {
    ElMessage.error(`加载表列表失败: ${error.message}`)
    return []
  }
}

const handleNodeExpand = async data => {
  if (data.type === 'datasource' && data.children.length === 0) {
    loading.value = true
    try {
      const schemas = await loadSchemas(data.repositoryId)
      data.children = schemas
      if(schemas.length > 0){
        data.connectionStatus = 1
      }
    } finally {
      loading.value = false
    }
  } else if (data.type === 'schema' && data.children.length === 0) {
    loading.value = true
    try {
      const tables = await loadTables(data.repositoryId, data.schemaName)
      data.children = tables
      data.tableCount = tables.length
    } finally {
      loading.value = false
    }
  }
}

const handleNodeClick = data => {
  if (data.type === 'datasource') {
    // 点击数据源，通知父组件显示概要信息
    emit('datasource-selected', {
      repositoryId: data.repositoryId,
      repositoryName: data.repositoryName,
      connectionStatus: data.connectionStatus,
      databaseType: data.databaseType,
    })
  } else if (data.type === 'table') {
    // 点击表，通知父组件显示表信息
    selectedTable.value = data
    emit('table-selected', {
      ...data,
      repositoryId: data.repositoryId,
      schemaName: data.schemaName,
      tableName: data.tableName,
      tableType: data.tableType,
    })
  }
}

const refreshTree = () => {
  searchText.value = ''
  loadTreeData()
  ElMessage.success('刷新完成')
}

const handleSearch = value => {
  treeRef.value.filter(value)
}

const filterNode = (value, data) => {
  if (!value) return true
  return data.label.toLowerCase().includes(value.toLowerCase())
}

const getNodeIcon = data => {
  switch (data.type) {
    case 'datasource':
      return 'iconfont icon-shujuku'
    case 'schema':
      return data.children && data.children.length > 0
        ? 'iconfont icon-24gf-folderOpen'
        : 'iconfont icon-24gf-folderMinus'
    case 'table':
      return data.tableType === 'VIEW'
        ? 'iconfont icon-browse'
        : 'iconfont icon-table'
    default:
      return 'iconfont icon-shujuku'
  }
}

const getNodeIconClass = data => {
  switch (data.type) {
    case 'datasource':
      return data.connectionStatus === 1
        ? 'datasource-icon online'
        : 'datasource-icon offline'
    case 'schema':
      return 'schema-icon'
    case 'table':
      return data.tableType === 'VIEW' ? 'view-icon' : 'table-icon'
    default:
      return 'default-icon'
  }
}

const viewTableStructure = data => {
  emit('table-selected', {
    repositoryId: data.repositoryId,
    schemaName: data.schemaName,
    tableName: data.tableName,
    tableType: data.tableType,
    action: 'structure',
  })
}

const viewTableData = data => {
  emit('table-selected', {
    repositoryId: data.repositoryId,
    schemaName: data.schemaName,
    tableName: data.tableName,
    tableType: data.tableType,
    action: 'data',
  })
}

const viewTableStats = async data => {
  selectedTable.value = data
  statsDialogVisible.value = true
  statsLoading.value = true

  try {
    // 获取表的详细统计信息
    const [columns, indexes, rowCount] = await Promise.all([
      apis.getTableColumnList({
        repositoryId: data.repositoryId,
        schemaName: data.schemaName,
        tableName: data.tableName,
      }),
      apis.getTableIndexList({
        repositoryId: data.repositoryId,
        schemaName: data.schemaName,
        tableName: data.tableName,
      }),
      apis.getTableRowCount(data.repositoryId, data.schemaName, data.tableName),
    ])

    tableStats.value = {
      tableType: data.tableType,
      columnCount: columns.length,
      indexCount: indexes.length,
      rowCount: rowCount,
      remarks: data.remarks,
    }
  } catch (error) {
    ElMessage.error('获取表统计信息失败')
    console.error('获取表统计信息失败:', error)
  } finally {
    statsLoading.value = false
  }
}

const handleContextMenu = command => {
  switch (command) {
    case 'refresh':
      refreshTree()
      break
    case 'copy':
      if (contextNode.value?.type === 'table') {
        navigator.clipboard.writeText(contextNode.value.tableName)
        ElMessage.success('表名已复制到剪贴板')
      }
      break
    case 'export':
      if (contextNode.value?.type === 'table') {
        ElMessage.info('导出功能开发中...')
      }
      break
  }
}

const formatNumber = num => {
  if (num == null) return '0'
  return num.toLocaleString()
}

// 生命周期
onMounted(() => {
  if (props.dataSources && props.dataSources.length > 0) {
    loadTreeData()

  }
})
</script>

<style scoped>
.database-browser {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.browser-toolbar {
  display: flex;
  gap: 8px;
  //margin-bottom: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
}

.browser-toolbar .el-input {
  flex: 1;
}

.tree-container {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 8px;
}

/* 树节点样式 */
.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 4px 0;
}

.node-icon {
  font-size: 16px;
}

.datasource-icon.online {
  color: #67c23a;
}

.datasource-icon.offline {
  color: var(--el-color-info);
}

.schema-icon {
  color: #409eff;
}

.table-icon {
  color: #67c23a;
}

.view-icon {
  color: #e6a23c;
}

.default-icon {
  color: #909399;
}

.node-label {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.node-actions {
  display: none;
  gap: 4px;
}

.tree-node:hover .node-actions {
  display: flex;
}

.node-badge {
  margin-left: auto;
}

/* 自定义树节点样式 */
:deep(.el-tree-node__content) {
  height: 32px;
  padding: 0 8px;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #e8f4fd;
  border-radius: 4px;
}

/* 滚动条样式 */
.tree-container::-webkit-scrollbar {
  width: 6px;
}

.tree-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .browser-toolbar {
    flex-direction: column;
  }

  .node-actions {
    display: flex !important;
  }

  .tree-node {
    flex-wrap: wrap;
  }
}
</style>
