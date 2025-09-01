<template>
  <div class="database-monitor-container h100">
    <!-- 页面头部 -->
    <el-card class="header-card" shadow="never">
      <div class="header">
        <div class="header-left">
          <el-icon class="header-icon"><Monitor /></el-icon>
          <h2>数据库监控</h2>
          <el-tag class="ml-2" type="info"
            >数据源总数: {{ dataSources.length }}</el-tag
          >
        </div>
        <div class="header-right">
          <el-button type="primary" :icon="Refresh" @click="refreshAll">
            刷新全部
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 主要内容区域 -->
    <div class="main-content flex-auto">
      <!-- 左侧：数据库树形浏览器 -->
      <div class="left-panel">
        <!-- 数据库浏览器 -->
        <custom-card class="browser-card" shadow="never" :no-scroll="true">
          <template #header>
            <div class="card-header">
              <el-icon><FolderOpened /></el-icon>
              <span>数据库浏览器</span>
            </div>
          </template>

          <database-browser
            :data-sources="summaries"
            @datasource-selected="handleDataSourceSelected"
            @table-selected="handleTableSelected"
          />
        </custom-card>
      </div>

      <!-- 右侧：详细信息展示 -->
      <div class="right-panel flex-auto">
        <el-tabs v-model="activeTab" type="border-card" class="h100">
          <el-tab-pane label="概要信息" name="overview" class="h100">
            <template #label>
              <el-icon><Monitor /></el-icon>
              概要信息
            </template>
            <!-- 数据库概要信息 -->
            <database-overview
              v-if="selectedDataSource && activeTab === 'overview'"
              :repository-id="selectedDataSource.repositoryId"
            />
          </el-tab-pane>
          <el-tab-pane
            v-if="selectedTable"
            label="表结构"
            name="structure"
            class="h100"
          >
            <template #label>
              <el-icon><List /></el-icon>
              表结构
            </template>
            <!-- 表结构信息 -->
            <table-structure
              v-if="selectedTable && activeTab === 'structure'"
              :repository-id="selectedTable.repositoryId"
              :schema-name="selectedTable.schemaName"
              :table-name="selectedTable.tableName"
              :table-data="selectedTable"
            />
          </el-tab-pane>
          <el-tab-pane
            v-if="selectedTable"
            label="表数据"
            name="data"
            class="h100"
          >
            <template #label>
              <el-icon><View /></el-icon>
              表数据
            </template>
            <!-- 表数据查看 -->
            <table-data-viewer
              v-if="selectedTable && activeTab === 'data'"
              :repository-id="selectedTable.repositoryId"
              :schema-name="selectedTable.schemaName"
              :table-name="selectedTable.tableName"
              :table-data="selectedTable"
            />
          </el-tab-pane>
        </el-tabs>

        <!-- 默认欢迎页面 -->
        <div v-if="!selectedDataSource" class="welcome-panel">
          <el-empty description="请选择一个数据源开始监控">
            <el-icon class="welcome-icon"><Memo /></el-icon>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  FolderOpened,
  List,
  Memo,
  Monitor,
  Refresh,
  View,
} from '@element-plus/icons-vue'
import DatabaseBrowser from './components/DatabaseBrowser.vue'
import DatabaseOverview from './components/DatabaseOverview.vue'
import TableStructure from './components/TableStructure.vue'
import TableDataViewer from './components/TableDataViewer.vue'
import apis from '@/service/apis'
import CustomCard from '@/components/CustomCard.vue'

// 响应式数据
const summaryLoading = ref(false)
const refreshingIds = ref([])
const summaries = ref([])
const dataSources = ref([])
const selectedDataSource = ref(null)
const selectedTable = ref(null)
const activeTab = ref('overview')

// 生命周期
onMounted(() => {
  loadAllSummaries()
})

// 方法
const loadAllSummaries = async () => {
  summaryLoading.value = true
  apis.getRepo()
  try {
    const response = await apis.getRepo()
    summaries.value = response || []
    summaries.value = summaries.value.map(t => {
      return {
        ...t,
        label: t.name,
        type: 'datasource',
      }
    })
    dataSources.value = summaries.value
    handleDataSourceSelected(dataSources.value[0])
  } catch (error) {
    ElMessage.error('获取数据源概要信息失败')
    console.error('获取数据源概要信息失败:', error)
  } finally {
    summaryLoading.value = false
  }
}

const refreshSummary = async repositoryId => {
  refreshingIds.value.push(repositoryId)
  try {
    const response = await apis.getDatabaseSummary(repositoryId)
    const index = summaries.value.findIndex(
      s => s.repositoryId === repositoryId,
    )
    if (index !== -1) {
      summaries.value[index] = response
    }

    // 如果是当前选中的数据源，更新选中状态
    if (selectedDataSource.value?.repositoryId === repositoryId) {
      selectedDataSource.value = response
    }
  } catch (error) {
    ElMessage.error(`刷新数据源 ${repositoryId} 失败`)
    console.error('刷新数据源失败:', error)
  } finally {
    refreshingIds.value = refreshingIds.value.filter(id => id !== repositoryId)
  }
}

const refreshAll = async () => {
  await loadAllSummaries()
  ElMessage.success('刷新完成')
}

const handleDataSourceSelected = dataSourceInfo => {
  selectedDataSource.value = dataSourceInfo
  selectedTable.value = null
  activeTab.value = 'overview'
}

const handleTableSelected = tableInfo => {
  selectedTable.value = tableInfo
  // 根据操作类型决定显示哪个标签页
  if (tableInfo.action === 'structure') {
    activeTab.value = 'structure'
  } else if (tableInfo.action === 'data') {
    activeTab.value = 'data'
  } else {
    activeTab.value = 'structure' // 默认显示表结构
  }
}
</script>

<style scoped>
.database-monitor-container {
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #f5f7fa;
}

/* 头部样式 */
.header-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #409eff;
}

.header h2 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

/* 主要内容区域 */
.main-content {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.left-panel {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

/* 数据库浏览器样式 */
.browser-card {
  flex: 1;
  border-radius: 8px;
  min-height: 0;
}

/* 欢迎页面样式 */
.welcome-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

/* 标签页样式 */
.tabs-container {
  margin-top: 16px;
}

/* 通用样式 */
.ml-2 {
  margin-left: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
  }
}
</style>
