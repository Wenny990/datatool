<template>
  <div class="database-overview" v-loading="loading">
    <!-- 基本信息卡片 -->
    <custom-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>数据库基本信息</span>
          <el-button
            size="small"
            type="primary"
            :icon="Refresh"
            @click="refreshData"
          >
            刷新
          </el-button>
        </div>
      </template>

      <div class="info-grid" v-if="summary">
        <div class="info-item">
          <div class="label">数据库类型</div>
          <div class="value">
            <el-tag :type="getDatabaseTypeColor(summary.databaseType)">
              {{ summary.databaseType }}
            </el-tag>
          </div>
        </div>

        <div class="info-item">
          <div class="label">数据库版本</div>
          <div class="value">{{ summary.databaseVersion || '未知' }}</div>
        </div>

        <div class="info-item">
          <div class="label">连接状态</div>
          <div class="value">
            <el-tag
              :type="summary.connectionStatus === 1 ? 'success' : 'danger'"
            >
              <el-icon class="mr-1">
                <Select v-if="summary.connectionStatus === 1" />
                <Close v-else />
              </el-icon>
              {{ summary.connectionStatus === 1 ? '正常' : '异常' }}
            </el-tag>
          </div>
        </div>

        <div class="info-item">
          <div class="label">连接URL</div>
          <div class="value url-text">{{ summary.databaseUrl }}</div>
        </div>

        <div class="info-item">
          <div class="label">最后检查时间</div>
          <div class="value">{{ formatTime(summary.lastCheckTime) }}</div>
        </div>
      </div>
    </custom-card>

    <!-- 统计信息卡片 -->
    <custom-card
      class="stats-card"
      shadow="never"
      v-if="summary && summary.connectionStatus === 1"
    >
      <template #header>
        <div class="card-header">
          <el-icon><TrendCharts /></el-icon>
          <span>统计信息</span>
        </div>
      </template>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon schema-icon">
            <svg-icon name="iconfont icon-shujuku" :size="24"></svg-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summary.catalogCount || 0 }}</div>
            <div class="stat-label">Catalog数量</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon schema-icon">
            <el-icon><FolderOpened /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summary.schemaCount || 0 }}</div>
            <div class="stat-label">Schema数量</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon table-icon">
            <el-icon><Grid /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summary.tableCount || 0 }}</div>
            <div class="stat-label">表数量</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon view-icon">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summary.viewCount || 0 }}</div>
            <div class="stat-label">视图数量</div>
          </div>
        </div>

        <div class="stat-card" v-if="summary.databaseSize">
          <div class="stat-icon size-icon">
             <svg-icon name="iconfont icon-fuwuqi" :size="24"></svg-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summary.databaseSize }}</div>
            <div class="stat-label">数据库大小</div>
          </div>
        </div>
      </div>
    </custom-card>



    <div class="flex gap12">
      <!-- 连接信息卡片 -->
      <custom-card
        class="connection-card"
        shadow="never"
        v-if="summary && summary.connectionStatus === 1"
      >
        <template #header>
          <div class="card-header">
            <el-icon><Connection /></el-icon>
            <span>连接信息</span>
          </div>
        </template>

        <div class="connection-info">
          <div class="connection-item" v-if="summary.maxConnections">
            <div class="connection-label">最大连接数</div>
            <div class="connection-value">{{ summary.maxConnections }}</div>
          </div>

          <div class="connection-item" v-if="summary.currentConnections">
            <div class="connection-label">当前连接数</div>
            <div class="connection-value">
              {{ summary.currentConnections }}
              <el-progress
                v-if="summary.maxConnections"
                :percentage="
                Math.round(
                  (summary.currentConnections / summary.maxConnections) * 100,
                )
              "
                :stroke-width="8"
                :show-text="false"
                class="connection-progress"
              />
            </div>
          </div>

          <div class="connection-item" v-if="summary.uptime">
            <div class="connection-label">运行时间</div>
            <div class="connection-value">{{ formatUptime(summary.uptime) }}</div>
          </div>
        </div>
      </custom-card>
      <!-- 运行状态卡片 -->
      <custom-card
        class="status-card"
        shadow="never"
        v-if="summary && summary.connectionStatus === 1"
        :body-style="{ maxHeight: '240px' }"
      >
        <template #header>
          <div class="card-header">
            <el-icon><Monitor /></el-icon>
            <span>运行状态</span>
            <el-button
              size="small"
              type="success"
              :icon="Connection"
              @click="testConnection"
            >
              测试连接
            </el-button>
          </div>
        </template>

        <div class="status-info" v-loading="statusLoading">
          <div v-if="Object.keys(databaseStatus).length > 0">
            <div
              v-for="(value, key) in databaseStatus"
              :key="key"
              class="status-item"
            >
              <div class="status-label">{{ formatStatusKey(key) }}</div>
              <div class="status-value">{{ value }}</div>
            </div>
          </div>
          <div v-else class="no-status">
            <el-empty description="暂无状态信息" />
          </div>
        </div>
      </custom-card>

    </div>


    <!-- 错误信息显示 -->
    <el-alert
      v-if="summary && summary.connectionStatus === 0"
      type="error"
      :title="summary.errorMessage"
      show-icon
      :closable="false"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Close,
  Connection,
  DataBoard,
  FolderOpened,
  Grid,
  InfoFilled,
  Monitor,
  Refresh,
  Select,
  TrendCharts,
  View,
} from '@element-plus/icons-vue'
import apis from '@/service/apis'
import CustomCard from '@/components/CustomCard.vue'
import SvgIcon from '@/components/svgIcon/index.vue'

const props = defineProps({
  repositoryId: {
    type: Number,
    required: true,
  },
})

// 响应式数据
const loading = ref(false)
const statusLoading = ref(false)
const summary = ref(null)
const databaseStatus = ref({})

const loadSummary = async () => {
  loading.value = true
  try {
    const response = await apis.getDatabaseSummary(props.repositoryId)
    summary.value = response
  } catch (error) {
    ElMessage.error('获取数据库概要信息失败')
    console.error('获取数据库概要信息失败:', error)
  } finally {
    loading.value = false
  }
}

const loadStatus = async () => {
  statusLoading.value = true
  try {
    const response = await apis.getDatabaseStatus(props.repositoryId)
    databaseStatus.value = response || {}
  } catch (error) {
    console.warn('获取数据库状态信息失败:', error)
    databaseStatus.value = {}
  } finally {
    statusLoading.value = false
  }
}

const refreshData = () => {
  loadData()
  ElMessage.success('刷新完成')
}

const testConnection = async () => {
  try {
    const response = await apis.testDatabaseConnection(props.repositoryId)
    if (response) {
      ElMessage.success('数据库连接正常')
    } else {
      ElMessage.error('数据库连接失败')
    }
  } catch (error) {
    ElMessage.error('测试连接失败')
    console.error('测试连接失败:', error)
  }
}

// 工具函数
const getDatabaseTypeColor = type => {
  const colorMap = {
    MySQL: 'primary',
    PostgreSQL: 'success',
    Oracle: 'warning',
    'SQL Server': 'info',
    H2: 'danger',
    SQLite: 'info',
  }
  return colorMap[type] || 'info'
}

const formatTime = timeStr => {
  if (!timeStr) return '未知'
  return new Date(timeStr).toLocaleString()
}

const formatSize = sizeInMB => {
  if (!sizeInMB) return '0 MB'
  if (sizeInMB < 1024) {
    return `${sizeInMB} MB`
  }
  return `${(sizeInMB / 1024).toFixed(2)} GB`
}

const formatUptime = seconds => {
  if (!seconds) return '未知'

  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)

  if (days > 0) {
    return `${days}天 ${hours}小时 ${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时 ${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}

const formatStatusKey = key => {
  const keyMap = {
    maxConnections: '最大连接数',
    currentConnections: '当前连接数',
    uptime: '运行时间',
    databaseSize: '数据库大小',
  }
  return keyMap[key] || key
}

// 方法
const loadData = async () => {
  await loadSummary()
  await loadStatus()
}

// 监听属性变化
watch(
  () => props.repositoryId,
  newId => {
    if (newId) {
      loadData()
    }
  },
  { immediate: true },
)
</script>

<style scoped>
.database-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow-y: auto;
}

/* 卡片样式 */
.info-card,
.stats-card,
.connection-card,
.status-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.card-header .el-button {
  margin-left: auto;
}

/* 基本信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #fafafa;
}

.info-item .label {
  font-weight: 500;
  color: #606266;
}

.info-item .value {
  color: #303133;
  font-weight: 600;
}

.url-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.schema-icon {
  background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);
}

.table-icon {
  background: linear-gradient(45deg, #f093fb 0%, #f5576c 100%);
}

.view-icon {
  background: linear-gradient(45deg, #4facfe 0%, #00f2fe 100%);
}

.size-icon {
  background: linear-gradient(45deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

/* 连接信息样式 */
.connection-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.connection-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #fafafa;
}

.connection-label {
  font-weight: 500;
  color: #606266;
}

.connection-value {
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.connection-progress {
  width: 100px;
}

/* 状态信息样式 */
.status-info {
  min-height: 200px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.status-item:last-child {
  border-bottom: none;
}

.status-label {
  font-weight: 500;
  color: #606266;
}

.status-value {
  color: #303133;
  font-weight: 600;
}

.no-status {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
}

/* 通用样式 */
.mr-1 {
  margin-right: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
