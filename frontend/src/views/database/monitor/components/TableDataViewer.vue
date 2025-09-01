<template>
  <div class="table-data-viewer flex-center" v-loading="loading">
    <!-- 数据查询工具栏 -->
    <custom-card class="toolbar-card" shadow="never" :no-scroll="true">
      <template #header>
        <div class="card-header">
          <el-icon><Grid /></el-icon>
          <span>表数据 - {{ tableName }}</span>
        </div>
      </template>
      <div class="data-toolbar">
        <div class="toolbar-left">
          <el-button
            size="small"
            type="primary"
            :icon="Search"
            @click="executeQuery"
          >
            查询数据
          </el-button>
          <el-button size="small" :icon="Refresh" @click="refreshData">
            刷新
          </el-button>
          <el-button
            size="small"
            :icon="Download"
            @click="exportData"
            :disabled="!tableData.length"
          >
            导出数据
          </el-button>
          <el-divider direction="vertical" />
          <el-button-group size="small">
            <el-button
              :type="viewMode === 'table' ? 'primary' : ''"
              :icon="Grid"
              @click="switchViewMode('table')">
              表格视图
            </el-button>
            <el-button
              :type="viewMode === 'row' ? 'primary' : ''"
              :icon="List"
              @click="switchViewMode('row')">
              行浏览
            </el-button>
          </el-button-group>
        </div>

        <div class="toolbar-right">
          <div class="where-input-container">
            <el-button
              text
              v-show="whereInputExpanded"
              size="small"
              :icon="whereInputExpanded ? ZoomOut : ZoomIn"
              @click="toggleWhereInput"
              :title="whereInputExpanded ? '收起' : '展开'"
            />
            <el-input
              v-model="queryParams.whereClause"
              :type="whereInputExpanded ? 'textarea' : 'text'"
              :autosize="whereInputExpanded ? { minRows: 3, maxRows: 8 } : false"
              placeholder="WHERE 条件 (可选)，例如: id > 100"
              clearable
              size="small"
              :style="whereInputExpanded ? 'width: 400px' : 'width: 300px'"
              @keyup.enter="!whereInputExpanded && executeQuery()"
              @focus="handleWhereInputFocus"
              @blur="handleWhereInputBlur"
            >
              <template #prepend>WHERE</template>
              <template #suffix>
                <el-button
                  text
                  size="small"
                  :icon="whereInputExpanded ? ZoomOut : ZoomIn"
                  @click="toggleWhereInput"
                  :title="whereInputExpanded ? '收起' : '展开'"
                />
              </template>
            </el-input>
          </div>

          <el-select
            v-model="queryParams.orderBy"
            placeholder="排序字段"
            clearable
            size="small"
            style="width: 150px"
          >
            <el-option
              v-for="column in columns"
              :key="column.columnName"
              :label="column.columnName"
              :value="column.columnName"
            />
          </el-select>

          <el-select
            v-model="queryParams.orderDirection"
            size="small"
            style="width: 80px"
          >
            <el-option label="ASC" value="ASC" />
            <el-option label="DESC" value="DESC" />
          </el-select>
        </div>
      </div>
      <div class="table-container flex-auto flex-center">
        <!-- 表格视图 -->
        <div class="flex-auto" v-show="viewMode === 'table'">
          <el-table
            v-loading="loadinData"
            ref="dataTableRef"
            :data="tableData"
            border
            stripe
            size="small"
            height="100%"
            :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
            @selection-change="handleSelectionChange"
            @row-click="handleRowClick"
            @cell-dblclick="handleCellClick"
            highlight-current-row
          >
            <el-table-column type="index" label="#" width="60" align="center" />
            <el-table-column
              v-for="column in visibleColumns"
              :key="column.columnName"
              :prop="column.columnName"
              :label="column.columnName"
              :min-width="getColumnWidth(column)"
              show-overflow-tooltip
              highlight-current-row
            >
              <template #header="scope">
                <div
                  class="column-header"
                  :title="
                    column.columnName +
                    '：' +
                    column.dataType +
                    ' ' +
                    column.remarks
                  "
                >
                  <div>
                    <el-icon v-if="isKeyColumn(column)" class="key-icon">
                      <Key />
                    </el-icon>
                    <svg-icon
                      v-if="getColumnType(column) === 'date'"
                      name="iconfont icon-shijian"
                      color="var(--el-color-warning)"
                    ></svg-icon>
                    <svg-icon
                      v-else-if="getColumnType(column) === 'number'"
                      name="iconfont icon-zhengshu"
                      color="var(--el-color-primary)"
                    ></svg-icon>
                    <svg-icon
                      v-else-if="getColumnType(column) === 'boolean'"
                      name="iconfont icon-buer"
                      color="var(--el-color-info)"
                    ></svg-icon>
                    <svg-icon
                      v-else
                      name="iconfont icon-zifuchuan"
                      color="var(--el-color-success)"
                    ></svg-icon>
                    <span class="ml4">{{ scope.column.label }}</span>
                  </div>
                  <el-dropdown
                    trigger="click"
                    @command="cmd => handleColumnAction(cmd, column)"
                  >
                    <el-icon class="column-menu-icon"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="sort-asc" :icon="SortUp"
                          >升序排列</el-dropdown-item
                        >
                        <el-dropdown-item command="sort-desc" :icon="SortDown"
                          >降序排列</el-dropdown-item
                        >
                        <el-dropdown-item command="filter" :icon="Filter"
                          >筛选</el-dropdown-item
                        >
                        <el-dropdown-item command="hide" :icon="Hide"
                          >隐藏列</el-dropdown-item
                        >
                        <el-dropdown-item command="copy" :icon="CopyDocument"
                          >复制列名</el-dropdown-item
                        >
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>

              <template #default="scope">
                <div class="cell-content" @click="handleCellClick(scope.row, column, scope.row[column.columnName], scope.$index, scope.column)">
                  <span
                    v-if="scope.row[column.columnName] !== null"
                    :class="getCellClass(column, scope.row[column.columnName])"
                  >
                    {{ formatCellValue(column, scope.row[column.columnName]) }}
                  </span>
                  <span v-else class="null-value">NULL</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 行浏览视图 -->
        <div class="flex-auto" v-show="viewMode === 'row'">
          <div class="row-browser" v-if="tableData.length > 0">
            <div class="row-navigator">
              <el-button-group size="small">
                <el-button
                  :icon="ArrowLeft"
                  @click="previousRow"
                  :disabled="currentRowIndex <= 0">
                  上一行
                </el-button>
                <el-button
                  :icon="ArrowRight"
                  @click="nextRow"
                  :disabled="currentRowIndex >= tableData.length - 1">
                  下一行
                </el-button>
              </el-button-group>
              <span class="row-info">
                第 {{ currentRowIndex + 1 }} 行 / 共 {{ tableData.length }} 行
              </span>
            </div>

            <div class="row-details">
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item
                  v-for="column in visibleColumns"
                  :key="column.columnName"
                  :label="column.columnName"
                  :label-style="{ width: '200px' }"
                >
                  <template #label>
                    <div class="field-label">
                      <el-icon v-if="isKeyColumn(column)" class="key-icon">
                        <Key />
                      </el-icon>
                      <svg-icon
                        v-if="getColumnType(column) === 'date'"
                        name="iconfont icon-shijian"
                        color="var(--el-color-warning)"
                      ></svg-icon>
                      <svg-icon
                        v-else-if="getColumnType(column) === 'number'"
                        name="iconfont icon-zhengshu"
                        color="var(--el-color-primary)"
                      ></svg-icon>
                      <svg-icon
                        v-else-if="getColumnType(column) === 'boolean'"
                        name="iconfont icon-buer"
                        color="var(--el-color-info)"
                      ></svg-icon>
                      <svg-icon
                        v-else
                        name="iconfont icon-zifuchuan"
                        color="var(--el-color-success)"
                      ></svg-icon>
                      <span class="ml4">{{ column.columnName }}</span>
                      <el-tag size="small" type="info" class="ml4">{{ column.dataType }}</el-tag>
                      <span class="ml4">{{ column.remarks }}</span>
                    </div>
                  </template>

                  <div class="field-value" @click="showCellViewer(getCurrentRowData()[column.columnName], column)">
                    <span
                      v-if="getCurrentRowData()[column.columnName] !== null"
                      :class="getCellClass(column, getCurrentRowData()[column.columnName])"
                    >
                      {{ formatCellValue(column, getCurrentRowData()[column.columnName]) }}
                    </span>
                    <span v-else class="null-value">NULL</span>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

          <div v-else class="no-data">
            <el-empty description="暂无数据" />
          </div>
        </div>

        <!-- 分页组件 -->
        <div class="pagination-wrapper flex">
          <el-text class="mr20">
            总记录数
            <el-tag type="primary">{{
              formatNumber(totalRows)
            }}</el-tag> </el-text
          ><el-text class="mr20"> 查询时间 {{ queryTime }}ms </el-text>
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100, 200, 500, 1000]"
            :total="totalRows || 0"
            layout="sizes, prev, pager ,next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </custom-card>

    <!-- 列管理对话框 -->
    <el-dialog v-model="columnDialogVisible" title="列管理" width="500px">
      <div class="column-manager">
        <div class="manager-toolbar">
          <el-button size="small" @click="showAllColumns">显示全部</el-button>
          <el-button size="small" @click="hideAllColumns">隐藏全部</el-button>
          <el-button size="small" @click="resetColumns">重置</el-button>
        </div>

        <el-checkbox-group v-model="selectedColumns">
          <div
            v-for="column in columns"
            :key="column.columnName"
            class="column-item"
          >
            <el-checkbox :value="column.columnName">
              <span class="column-name">{{ column.columnName }}</span>
              <span class="column-type">{{ column.dataType }}</span>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
    </el-dialog>

    <!-- 数据详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="数据详情" width="800px">
      <div v-if="selectedRowData" class="row-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item
            v-for="column in columns"
            :key="column.columnName"
            :label="column.columnName"
          >
            <div class="detail-value">
              <span v-if="selectedRowData[column.columnName] !== null">
                {{ selectedRowData[column.columnName] }}
              </span>
              <span v-else class="null-value">NULL</span>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 单元格内容查看器（浮动在表格上面） -->
    <el-dialog
      v-model="cellViewerVisible"
      :title="`数值查看器 - ${cellViewerData.columnName || ''}`"
      width="720px"
      :modal="false"
      :append-to-body="true"
      draggable
      destroy-on-close
    >
      <div class="cell-viewer">
        <div class="cell-info">
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="字段名">
              {{ cellViewerData.columnName }}
            </el-descriptions-item>
            <el-descriptions-item label="数据类型">
              {{ cellViewerData.dataType }}
            </el-descriptions-item>
            <el-descriptions-item label="数据长度">
              {{ getCellValueLength(cellViewerData.value) }}
            </el-descriptions-item>
            <el-descriptions-item label="是否为空">
              <el-tag :type="cellViewerData.value === null ? 'warning' : 'success'" size="small">
                {{ cellViewerData.value === null ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="cell-content-area">
          <div class="content-header">
            <span>内容预览</span>
            <el-button-group size="small">
              <el-button
                :type="cellContentFormat === 'text' ? 'primary' : ''"
                @click="cellContentFormat = 'text'">
                文本
              </el-button>
              <el-button
                :type="cellContentFormat === 'json' ? 'primary' : ''"
                @click="cellContentFormat = 'json'"
                :disabled="!isJsonLike(cellViewerData.value)">
                JSON
              </el-button>
              <el-button
                :type="cellContentFormat === 'hex' ? 'primary' : ''"
                @click="cellContentFormat = 'hex'"
                :disabled="typeof cellViewerData.value !== 'string'">
                十六进制
              </el-button>
            </el-button-group>
          </div>

          <div class="content-display">
            <el-input
              v-if="cellViewerData.value !== null"
              :model-value="formatCellContentForViewer(cellViewerData.value)"
              type="textarea"
              :autosize="{ minRows: 8, maxRows: 15 }"
              readonly
              placeholder="暂无内容"
            />
            <div v-else class="null-placeholder">
              <el-icon class="null-icon"><Warning /></el-icon>
              <span>该字段值为 NULL</span>
            </div>
          </div>

          <div class="content-actions">
            <el-button size="small" :icon="CopyDocument" @click="copyCellContent">
              复制内容
            </el-button>
            <el-button size="small" :icon="Download" @click="exportCellContent" v-if="cellViewerData.value !== null">
              导出为文件
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Grid,
  List,
  ZoomIn,
  ZoomOut,
  Key,
  MoreFilled,
  Sort,
  Filter,
  Hide,
  CopyDocument,
  SortUp,
  SortDown,
  ArrowLeft,
  ArrowRight,
  Warning,
} from '@element-plus/icons-vue'
import apis from '@/service/apis'
import CustomCard from '@/components/CustomCard.vue'
import SvgIcon from '@/components/svgIcon/index.vue'

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
})

// 响应式数据
const loading = ref(false)
const loadinData = ref(false)
const dataTableRef = ref()
const tableData = ref([])
const columns = ref([])
const selectedColumns = ref([])
const totalRows = ref(null)
const queryTime = ref(0)
const tableHeight = ref(400)
const columnDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedRowData = ref(null)
const selectedRows = ref([])

// 新增的响应式数据
const viewMode = ref('table') // 'table' | 'row'
const currentRowIndex = ref(0)
const whereInputExpanded = ref(false)
const cellViewerVisible = ref(false)
const cellViewerData = ref({
  value: null,
  columnName: '',
  dataType: ''
})
const cellContentFormat = ref('text') // 'text' | 'json' | 'hex'

// 查询参数
const queryParams = reactive({
  whereClause: '',
  orderBy: '',
  orderDirection: 'ASC',
})

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 20,
})

// 计算属性
const visibleColumns = computed(() => {
  return columns.value.filter(col =>
    selectedColumns.value.includes(col.columnName),
  )
})

const loadColumns = async () => {
  try {
    const response = await apis.getTableColumnList({
      repositoryId: props.repositoryId,
      schemaName: props.schemaName,
      tableName: props.tableName,
    })
    columns.value = response || []
    selectedColumns.value = columns.value.map(col => col.columnName)
  } catch (error) {
    ElMessage.error('获取表字段失败')
    console.error('获取表字段失败:', error)
  }
}

const loadData = async () => {
  loadinData.value = true
  const startTime = Date.now()

  try {
    const response = await apis.getTableData({
      repositoryId: props.repositoryId,
      schemaName: props.schemaName,
      tableName: props.tableName,
      current: pagination.current,
      pageSize: pagination.pageSize,
      whereClause: queryParams.whereClause,
      orderBy: queryParams.orderBy,
      orderDirection: queryParams.orderDirection,
    })

    tableData.value = response.records || []
    totalRows.value = response.total || 0
    queryTime.value = Date.now() - startTime
  } catch (error) {
    ElMessage.error('查询表数据失败')
    console.error('查询表数据失败:', error)
    tableData.value = []
  } finally {
    loadinData.value = false
  }
}

const loadTotalRows = async () => {
  try {
    const count = await apis.getTableRowCount(
      props.repositoryId,
      props.schemaName,
      props.tableName,
    )
    totalRows.value = count
  } catch (error) {
    console.warn('获取表记录总数失败:', error)
  }
}

const executeQuery = () => {
  pagination.current = 1
  loadData()
}

const refreshData = () => {
  queryParams.whereClause = ''
  queryParams.orderBy = ''
  queryParams.orderDirection = 'ASC'
  pagination.current = 1
  loadData()
  ElMessage.success('刷新完成')
}

const exportData = () => {
  if (!tableData.value.length) {
    ElMessage.warning('没有数据可导出')
    return
  }

  // 导出为CSV
  const headers = visibleColumns.value.map(col => col.columnName)
  const csvContent = [
    headers.join(','),
    ...tableData.value.map(row =>
      headers
        .map(header => {
          const value = row[header]
          return value !== null ? `"${value}"` : ''
        })
        .join(','),
    ),
  ].join('\n')

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${props.tableName}_data.csv`
  link.click()

  ElMessage.success('数据导出成功')
}

const handleSizeChange = size => {
  pagination.pageSize = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = page => {
  pagination.current = page
  loadData()
}

const increasePageSize = () => {
  const sizes = [10, 20, 50, 100, 200, 500, 1000]
  const currentIndex = sizes.indexOf(pagination.pageSize)
  if (currentIndex < sizes.length - 1) {
    pagination.pageSize = sizes[currentIndex + 1]
    pagination.current = 1
    loadData()
  }
}

const decreasePageSize = () => {
  const sizes = [10, 20, 50, 100, 200, 500, 1000]
  const currentIndex = sizes.indexOf(pagination.pageSize)
  if (currentIndex > 0) {
    pagination.pageSize = sizes[currentIndex - 1]
    pagination.current = 1
    loadData()
  }
}

const handleColumnAction = (command, column) => {
  switch (command) {
    case 'sort-asc':
      queryParams.orderBy = column.columnName
      queryParams.orderDirection = 'ASC'
      executeQuery()
      break
    case 'sort-desc':
      queryParams.orderBy = column.columnName
      queryParams.orderDirection = 'DESC'
      executeQuery()
      break
    case 'filter':
      ElMessage.info('筛选功能开发中...')
      break
    case 'hide':
      selectedColumns.value = selectedColumns.value.filter(
        col => col !== column.columnName,
      )
      break
    case 'copy':
      navigator.clipboard.writeText(column.columnName)
      ElMessage.success('列名已复制到剪贴板')
      break
  }
}

const handleSelectionChange = selection => {
  selectedRows.value = selection
}

const showAllColumns = () => {
  selectedColumns.value = columns.value.map(col => col.columnName)
}

const hideAllColumns = () => {
  selectedColumns.value = []
}

const resetColumns = () => {
  selectedColumns.value = columns.value.map(col => col.columnName)
}

// 新增的方法

// 视图模式切换
const switchViewMode = (mode) => {
  viewMode.value = mode
  if (mode === 'row' && tableData.value.length > 0) {
    currentRowIndex.value = 0
  }
}

// 行浏览导航
const previousRow = () => {
  if (currentRowIndex.value > 0) {
    currentRowIndex.value--
  }
}

const nextRow = () => {
  if (currentRowIndex.value < tableData.value.length - 1) {
    currentRowIndex.value++
  }
}

const getCurrentRowData = () => {
  return tableData.value[currentRowIndex.value] || {}
}

// 处理行点击
const handleRowClick = (row, column, event) => {
  if (viewMode.value === 'table') {
    const index = tableData.value.findIndex(r => r === row)
    if (index >= 0) {
      currentRowIndex.value = index
    }
  }
}

// 处理单元格点击
const handleCellClick = (row, column, cellValue, rowIndex, columnConfig) => {
  showCellViewer(cellValue, column)
}

// WHERE输入框处理
const toggleWhereInput = () => {
  whereInputExpanded.value = !whereInputExpanded.value
}

const handleWhereInputFocus = () => {
  // 可以在这里添加自动展开逻辑
}

const handleWhereInputBlur = () => {
  // 可以在这里添加自动收起逻辑
}

// 单元格内容查看器
const showCellViewer = (value, column) => {
  cellViewerData.value = {
    value: value,
    columnName: column.columnName,
    dataType: column.dataType
  }
  cellContentFormat.value = 'text'
  cellViewerVisible.value = true
}

const getCellValueLength = (value) => {
  if (value === null || value === undefined) return 0
  return String(value).length
}

const isJsonLike = (value) => {
  if (typeof value !== 'string') return false
  try {
    JSON.parse(value)
    return true
  } catch {
    return false
  }
}

const formatCellContentForViewer = (value) => {
  if (value === null || value === undefined) return ''

  switch (cellContentFormat.value) {
    case 'json':
      try {
        return JSON.stringify(JSON.parse(value), null, 2)
      } catch {
        return String(value)
      }
    case 'hex':
      return Array.from(new TextEncoder().encode(String(value)))
        .map(byte => byte.toString(16).padStart(2, '0'))
        .join(' ')
    default:
      return String(value)
  }
}

const copyCellContent = () => {
  const content = formatCellContentForViewer(cellViewerData.value.value)
  navigator.clipboard.writeText(content)
  ElMessage.success('内容已复制到剪贴板')
}

const exportCellContent = () => {
  const content = formatCellContentForViewer(cellViewerData.value.value)
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${cellViewerData.value.columnName}_content.txt`
  link.click()
  ElMessage.success('内容已导出')
}

// 工具函数
const formatNumber = num => {
  if (num == null) return '0'
  return num.toLocaleString()
}

const getColumnWidth = column => {
  const baseWidth = Math.max(column.columnName.length * 8, 120)
  const typeWidth = {
    VARCHAR: 150,
    TEXT: 200,
    LONGTEXT: 300,
    INT: 120,
    BIGINT: 120,
    DECIMAL: 120,
    DATETIME: 150,
    DATE: 120,
  }

  const dataType = column.dataType.toUpperCase()
  return typeWidth[dataType] || baseWidth
}

const isKeyColumn = column => {
  return column.isPrimaryKey || column.isUnique
}

const getColumnType = column => {
  const dataType = column.dataType?.toUpperCase() || ''

  // 通过包含关键字判断类型
  if (
    dataType.includes('DATE') ||
    dataType.includes('TIME') ||
    dataType.includes('TIMESTAMP')
  ) {
    return 'date'
  } else if (
    dataType.includes('INT') ||
    dataType.includes('NUMBER') ||
    dataType.includes('DECIMAL') ||
    dataType.includes('FLOAT') ||
    dataType.includes('DOUBLE') ||
    dataType.includes('BIGINT') ||
    dataType.includes('TINYINT') ||
    dataType.includes('SMALLINT') ||
    dataType.includes('MEDIUMINT')
  ) {
    return 'number'
  } else if (
    dataType.includes('CHAR') ||
    dataType.includes('TEXT') ||
    dataType.includes('VARCHAR') ||
    dataType.includes('STRING') ||
    dataType.includes('ENUM') ||
    dataType.includes('SET')
  ) {
    return 'string'
  } else if (dataType.includes('BOOL')) {
    return 'boolean'
  }

  // 默认返回字符串类型
  return 'string'
}

const getCellClass = (column, value) => {
  const classes = ['cell-value']
  if (value === null) {
    classes.push('null-value')
  } else if (column.dataType.toLowerCase().includes('int')) {
    classes.push('number-value')
  } else if (column.dataType.toLowerCase().includes('date')) {
    classes.push('date-value')
  }

  return classes.join(' ')
}

const formatCellValue = (column, value) => {
  if (value === null) return 'NULL'

  const dataType = column.dataType.toLowerCase()

  if (dataType.includes('date') || dataType.includes('time')) {
    try {
      return new Date(value).toLocaleString()
    } catch {
      return value
    }
  }

  if (typeof value === 'string' && value.length > 100) {
    return value.substring(0, 100) + '...'
  }

  return value
}

// 调整表格高度
onMounted(() => {
  const updateTableHeight = () => {
    const container = document.querySelector('.table-container')
    if (container) {
      const windowHeight = window.innerHeight
      const containerTop = container.getBoundingClientRect().top
      tableHeight.value = Math.max(windowHeight - containerTop - 120, 300)
    }
  }

  updateTableHeight()
  window.addEventListener('resize', updateTableHeight)

  return () => {
    window.removeEventListener('resize', updateTableHeight)
  }
})

// 方法
const initData = async () => {
  await loadColumns()
  await loadData()
  // await loadTotalRows()
}

// 监听属性变化
watch(
  () => [props.repositoryId, props.schemaName, props.tableName],
  () => {
    if (props.repositoryId && props.tableName) {
      initData()
    }
  },
  { immediate: true },
)
</script>

<style scoped>
.table-data-viewer {
  display: flex;
  flex-direction: column;
  gap: 16px;

  height: 100%;
}

/* 工具栏样式 */
.toolbar-card {
  border-radius: 8px;
}

.data-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats-info {
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

/* 数据卡片样式 */
.data-card {
  flex: 1;
  border-radius: 8px;
  min-height: 0;
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
}

/* 表格容器样式 */
.table-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 列头样式 */
.column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.key-icon {
  color: #f56c6c;
  font-size: 14px;
}

.column-menu-icon {
  color: #909399;
  cursor: pointer;
  font-size: 14px;
}

.column-menu-icon:hover {
  color: #409eff;
}

/* 单元格样式 */
.cell-content {
  max-width: 100%;
}

.cell-value {
  color: #303133;
}

.number-value {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  text-align: right;
}

.date-value {
  //color: #e6a23c;
}

.null-value {
  color: #909399;
  font-style: italic;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 6px;
  display: flex;
  justify-content: flex-end;
  padding-top: 0px;
  //border-top: 1px solid #ebeef5;
}

/* 列管理样式 */
.column-manager {
  max-height: 400px;
  overflow-y: auto;
}

.manager-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.column-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.column-item:last-child {
  border-bottom: none;
}

.column-name {
  font-weight: 500;
  color: #303133;
}

.column-type {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

/* 数据详情样式 */
.row-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-value {
  max-width: 100%;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }

  .toolbar-right {
    flex-wrap: wrap;
    gap: 8px;
  }
}

/* 新增样式 */

/* WHERE输入框样式 */
.where-input-container {
  position: relative;
}

/* 行浏览器样式 */
.row-browser {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.row-navigator {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.row-info {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.row-details {
  flex: 1;
  overflow-y: auto;
}

.field-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.field-value {
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.field-value:hover {
  background-color: #f0f8ff;
  border: 1px solid #409eff;
}

.no-data {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 单元格查看器样式 */
.cell-viewer {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cell-info {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.cell-content-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  color: #303133;
}

.content-display {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.null-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #909399;
  background: #fafafa;
}

.null-icon {
  font-size: 24px;
}

.content-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 单元格可点击样式 */
.cell-content {
  cursor: pointer;
  transition: all 0.2s;
}

.cell-content:hover {
  background-color: rgba(64, 158, 255, 0.1);
  border-radius: 2px;
}
</style>
