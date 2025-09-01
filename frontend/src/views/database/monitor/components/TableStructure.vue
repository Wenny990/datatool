<template>
  <div class="table-structure flex-center" v-loading="loading">
    <!-- 表信息头部 -->

      <div class="table-meta-info mg8" v-if="tableInfo">
        <div class="meta-item">
          <span class="label">表名称:</span>
          <span class="value">{{ tableName || 'default' }}</span>
        </div>
        <div class="meta-item">
          <span class="label">模式:</span>
          <span class="value">{{ schemaName || 'default' }}</span>
        </div>
        <div class="meta-item">
          <span class="label">表类型:</span>
          <el-tag
            :type="tableInfo.tableType === 'TABLE' ? 'primary' : 'success'"
          >
            {{ tableInfo.tableType === 'TABLE' ? '数据表' : '视图' }}
          </el-tag>
        </div>
        <div class="meta-item" >
          <span class="label">描述:</span>
          <span class="value">{{ tableInfo.remarks || '无' }}</span>
        </div>

        <div class="meta-item">
          <span class="label">字段数:</span>
          <span class="value">{{ columns.length }}</span>
        </div>
        <div class="meta-item">
          <span class="label">索引数:</span>
          <span class="value">{{ indexes.length }}</span>
        </div>
        <div class="header-actions">
          <el-button size="small" :icon="Refresh" @click="refreshData">
            刷新
          </el-button>
          <el-button size="small" :icon="CopyDocument" @click="copyTableName">
            复制表名
          </el-button>
        </div>
      </div>
      <el-tabs v-model="activeTab" type="border-card" class="mg8 flex-auto" tab-position="left" size="small" >
        <!-- 字段信息标签页 -->
        <el-tab-pane label="字段信息" name="columns" class="flex-center">
          <template #label>
            <el-icon><List /></el-icon>
            字段信息 ({{ columns.length }})
          </template>
          <div class="table-toolbar">
            <el-input
              v-model="columnSearchText"
              placeholder="搜索字段名..."
              :prefix-icon="Search"
              clearable
              size="small"
              style="width: 300px"
            />

            <div class="toolbar-actions">
              <el-button size="small" :icon="Download" @click="exportColumns">
                导出字段
              </el-button>
            </div>
          </div>
          <div class="flex-auto pb10">
            <el-table
              :data="filteredColumns"
              border
              stripe
              size="small"
              height="100%"
              :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
            >
              <el-table-column type="index" label="#" width="50" align="center" />

              <el-table-column prop="columnName" label="字段名" min-width="150">
                <template #default="scope">
                  <div class="column-name">
                    <el-icon
                      v-if="scope.row.isPrimaryKey"
                      class="key-icon primary-key"
                    >
                      <Key />
                    </el-icon>
                    <el-icon
                      v-else-if="scope.row.isAutoIncrement"
                      class="key-icon auto-increment"
                    >
                      <Promotion />
                    </el-icon>
                    <span>{{ scope.row.columnName }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="dataType" label="数据类型" width="120">
                <template #default="scope">
                  <el-tag
                    size="small"
                    :type="getDataTypeColor(scope.row.dataType)"
                  >
                    {{ scope.row.dataType }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="长度/精度" width="100" align="center">
                <template #default="scope">
                  <span v-if="scope.row.dataPrecision">{{scope.row.dataPrecision}}.{{ scope.row.dataScale }}</span>
                  <span v-else-if="scope.row.dataLength">
                  {{ scope.row.dataLength }}
                </span>
                  <span v-else>-</span>
                </template>
              </el-table-column>

              <el-table-column label="允许空值" width="80" align="center">
                <template #default="scope">
                  <el-tag v-if="!scope.row.nullable"
                          type="success"
                          size="small"
                  >
                    否
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="columnDefault" label="默认值" width="120">
                <template #default="scope">
                <span v-if="scope.row.defaultValue">{{
                    scope.row.defaultValue
                  }}</span>
                  <span v-else class="null-value">NULL</span>
                </template>
              </el-table-column>

              <el-table-column label="属性" width="100" align="center">
                <template #default="scope">
                  <div class="column-attributes">
                    <el-tag
                      v-if="scope.row.isPrimaryKey"
                      size="small"
                      type="danger"
                    >PK</el-tag
                    >
                    <el-tag
                      v-if="scope.row.isAutoIncrement"
                      size="small"
                      type="warning"
                    >AI</el-tag
                    >
                    <el-tag v-if="scope.row.isUnique" size="small" type="info"
                    >UQ</el-tag
                    >
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                prop="remarks"
                label="备注"
                min-width="200"
                show-overflow-tooltip
              >
                <template #default="scope">
                  <span v-if="scope.row.remarks">{{ scope.row.remarks }}</span>
                  <span v-else class="no-comment">无备注</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 索引信息标签页 -->
        <el-tab-pane label="索引信息" name="indexes">
          <template #label>
            <el-icon><Key /></el-icon>
            索引信息 ({{ indexes.length }})
          </template>

          <div class="table-toolbar">
            <el-input
              v-model="indexSearchText"
              placeholder="搜索索引名..."
              :prefix-icon="Search"
              clearable
              size="small"
              style="width: 300px"
            />
          </div>

          <el-table
            :data="filteredIndexes"
            border
            stripe
            size="small"
            :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
          >
            <el-table-column type="index" label="#" width="50" align="center" />

            <el-table-column prop="indexName" label="索引名" min-width="200">
              <template #default="scope">
                <div class="index-name">
                  <el-icon
                    v-if="scope.row.isPrimaryKey"
                    class="index-icon primary"
                  >
                    <Key />
                  </el-icon>
                  <el-icon
                    v-else-if="scope.row.isUnique"
                    class="index-icon unique"
                  >
                    <Star />
                  </el-icon>
                  <el-icon v-else class="index-icon normal">
                    <Menu />
                  </el-icon>
                  <span>{{ scope.row.indexName }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="索引类型" width="100" align="center">
              <template #default="scope">
                <el-tag size="small" :type="getIndexTypeColor(scope.row)">
                  {{ getIndexTypeText(scope.row) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column
              prop="columnNames"
              label="索引字段"
              min-width="300"
            >
              <template #default="scope">
                <div class="index-columns">
                  <el-tag
                    v-for="column in scope.row.indexColumns"
                    :key="column.columnName"
                    size="small"
                    class="column-tag"
                  >
                    {{ column.columnName }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="升序" width="80" align="center">
              <template #default="scope">
                <el-tag
                  :type="scope.row.isAsc ? 'success' : 'info'"
                  size="small"
                >
                  {{ scope.row.isAsc ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="唯一性" width="80" align="center">
              <template #default="scope">
                <el-tag
                  :type="scope.row.isUnique ? 'success' : 'info'"
                  size="small"
                >
                  {{ scope.row.isUnique ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- DDL语句标签页 -->
        <el-tab-pane label="DDL语句" name="ddl">
          <template #label>
            <el-icon><Document /></el-icon>
            DDL语句
          </template>

          <div class="ddl-container">
            <div class="ddl-toolbar">
              <el-button size="small" :icon="CopyDocument" @click="copyDDL">
                复制DDL
              </el-button>
              <el-button size="small" :icon="Download" @click="downloadDDL">
                下载DDL
              </el-button>
            </div>

            <code-editor
              v-model="ddlStatement"
              type="sql"
              height="400px"
              :disabled="true"
            />
          </div>
        </el-tab-pane>
      </el-tabs>

  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  CopyDocument,
  Document,
  Download,
  Grid,
  Key,
  List,
  Menu,
  Promotion,
  Refresh,
  Search,
  Star,
} from '@element-plus/icons-vue'
import CodeEditor from '@/components/codeEditor/index.vue'
import apis from '@/service/apis'
import CustomCard from '@/components/CustomCard.vue'

const props = defineProps({
  repositoryId: {
    type: Number,
    required: true,
  },
  schemaName: {
    type: String,
    default: '',
  },
  tableName: {
    type: String,
    required: true,
  },
  tableData: {
    type: Object,
    default: () => {},
    required: true,
  }
})

// 响应式数据
const loading = ref(false)
const activeTab = ref('columns')
const columnSearchText = ref('')
const indexSearchText = ref('')
const columns = ref([])
const indexes = ref([])
const tableInfo = computed(() => props.tableData)
const ddlStatement = ref('')

// 计算属性
const filteredColumns = computed(() => {
  if (!columnSearchText.value) return columns.value
  return columns.value.filter(column =>
    column.columnName
      .toLowerCase()
      .includes(columnSearchText.value.toLowerCase()),
  )
})

const filteredIndexes = computed(() => {
  if (!indexSearchText.value) return indexes.value
  return indexes.value.filter(index =>
    index.indexName.toLowerCase().includes(indexSearchText.value.toLowerCase()),
  )
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([loadColumns(), loadIndexes(), generateDDL()])
  } finally {
    loading.value = false
  }
}

// 监听属性变化

const loadColumns = async () => {
  try {
    const response = await apis.getTableColumnList({
      repositoryId: props.repositoryId,
      schemaName: props.schemaName,
      tableName: props.tableName,
    })
    columns.value = response || []
  } catch (error) {
    ElMessage.error('获取字段信息失败')
    console.error('获取字段信息失败:', error)
  }
}

const loadIndexes = async () => {
  try {
    const response = await apis.getTableIndexList({
      repositoryId: props.repositoryId,
      schemaName: props.schemaName,
      tableName: props.tableName,
    })
    indexes.value = response || []
  } catch (error) {
    ElMessage.error('获取索引信息失败')
    console.error('获取索引信息失败:', error)
  }
}

const generateDDL = async () => {
  try {
    // 这里应该调用后端API生成DDL语句
    // 暂时生成一个简单的DDL示例
    let ddl = `-- 表结构: ${props.tableName}\n`
    ddl += `CREATE TABLE ${props.schemaName ? props.schemaName + '.' : ''}${props.tableName} (\n`

    const columnDDL = columns.value
      .map(col => {
        let line = `  ${col.columnName} ${col.dataType}`
        if (col.columnSize) {
          line += `(${col.columnSize}${col.decimalDigits ? ',' + col.decimalDigits : ''})`
        }
        if (col.isNullable === 'NO') {
          line += ' NOT NULL'
        }
        if (col.columnDefault) {
          line += ` DEFAULT ${col.columnDefault}`
        }
        if (col.remarks) {
          line += ` COMMENT '${col.remarks}'`
        }
        return line
      })
      .join(',\n')

    ddl += columnDDL

    // 添加主键
    const primaryKeys = columns.value.filter(col => col.isPrimaryKey)
    if (primaryKeys.length > 0) {
      ddl += `,\n  PRIMARY KEY (${primaryKeys.map(col => col.columnName).join(', ')})`
    }

    ddl += '\n);'

    ddlStatement.value = ddl
  } catch (error) {
    console.error('生成DDL失败:', error)
    ddlStatement.value = '-- 生成DDL失败'
  }
}

const refreshData = () => {
  loadData()
  ElMessage.success('刷新完成')
}

const copyTableName = () => {
  navigator.clipboard.writeText(props.tableName)
  ElMessage.success('表名已复制到剪贴板')
}

const exportColumns = () => {
  // 导出字段信息为CSV
  const headers = [
    '字段名',
    '数据类型',
    '长度',
    '允许空值',
    '默认值',
    '主键',
    '自增',
    '备注',
  ]
  const csvContent = [
    headers.join(','),
    ...columns.value.map(col =>
      [
        col.columnName,
        col.dataType,
        col.columnSize || '',
        col.isNullable === 'YES' ? '是' : '否',
        col.columnDefault || '',
        col.isPrimaryKey ? '是' : '否',
        col.isAutoIncrement ? '是' : '否',
        col.remarks || '',
      ].join(','),
    ),
  ].join('\n')

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${props.tableName}_columns.csv`
  link.click()
}

const copyDDL = () => {
  navigator.clipboard.writeText(ddlStatement.value)
  ElMessage.success('DDL语句已复制到剪贴板')
}

const downloadDDL = () => {
  const blob = new Blob([ddlStatement.value], {
    type: 'text/sql;charset=utf-8;',
  })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${props.tableName}_ddl.sql`
  link.click()
}

// 工具函数
const getDataTypeColor = dataType => {
  if (!dataType) return 'info'
  const type = dataType.toLowerCase()
  if (type.includes('int') || type.includes('number')) return 'primary'
  if (type.includes('char') || type.includes('text')) return 'success'
  if (type.includes('date') || type.includes('time')) return 'warning'
  if (type.includes('blob') || type.includes('binary')) return 'danger'
  return 'info'
}

const getIndexTypeColor = index => {
  if (index.isPrimaryKey) return 'danger'
  if (index.isUnique) return 'warning'
  return 'primary'
}

const getIndexTypeText = index => {
  if (index.isPrimaryKey) return '主键'
  if (index.isUnique) return '唯一'
  return '普通'
}

watch(
  () => [props.repositoryId, props.schemaName, props.tableName],
  () => {
    if (props.repositoryId && props.tableName) {
      loadData()
    }
  },
  { immediate: true },
)
</script>

<style scoped>
.table-structure {
  display: flex;
  flex-direction: column;
  gap: 6px;
  //padding: 16px;
  height: 100%;
}

/* 卡片样式 */
.table-info-card,
.content-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

/* 表元信息样式 */
.table-meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-item .label {
  font-weight: 500;
  color: #606266;
}

.meta-item .value {
  color: #303133;
  font-weight: 600;
}

/* 内容卡片样式 */
.content-card {
  flex: 1;
  min-height: 0;
}

/* 工具栏样式 */
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  //margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

/* 字段名样式 */
.column-name {
  display: flex;
  align-items: center;
  gap: 6px;
}

.key-icon {
  font-size: 14px;
}

.primary-key {
  color: #f56c6c;
}

.auto-increment {
  color: #e6a23c;
}

/* 字段属性样式 */
.column-attributes {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.null-value,
.no-comment {
  color: #909399;
  font-style: italic;
}

/* 索引名样式 */
.index-name {
  display: flex;
  align-items: center;
  gap: 6px;
}

.index-icon {
  font-size: 14px;
}

.index-icon.primary {
  color: #f56c6c;
}

.index-icon.unique {
  color: #e6a23c;
}

.index-icon.normal {
  color: #409eff;
}

/* 索引字段样式 */
.index-columns {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.column-tag {
  margin: 2px 0;
}

/* DDL容器样式 */
.ddl-container {
  padding: 12px;
  background: #f8f9fa;
}

.ddl-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

/* 标签页样式 */
:deep(.el-tabs__content) {
  padding: 0;
  height: 100%;
  overflow: hidden;
}

:deep(.el-tab-pane) {
  height: 100%;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .table-meta-info {
    flex-direction: column;
    gap: 12px;
  }

  .table-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .header-actions {
    margin-left: 0;
    margin-top: 8px;
  }
}
</style>
