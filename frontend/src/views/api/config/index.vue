<template>
  <div class="api-config-container">
    <!-- 页面头部 -->
    <el-card class="header-card" shadow="never">
      <div class="header">
        <div class="header-left">
          <el-icon class="header-icon"><Setting /></el-icon>
          <h2>API接口配置管理</h2>
          <el-tag class="ml-2" type="info"
            >接口总数: {{ pagination.total }}</el-tag
          >
        </div>
        <div class="header-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            新增接口
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 查询条件 -->
    <el-card class="search-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Search /></el-icon>
          <span>查询条件</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="接口名称">
          <el-input
            v-model="searchForm.apiName"
            placeholder="请输入接口名称"
            clearable
            :prefix-icon="Search"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="接口类型">
          <el-select
            v-model="searchForm.apiType"
            placeholder="请选择接口类型"
            clearable
            style="width: 180px"
          >
            <el-option label="HTTP接口" :value="1">
              <el-icon class="mr-2"><Link /></el-icon>
              HTTP接口
            </el-option>
            <el-option label="SQL查询" :value="2">
              <el-icon class="mr-2"><Memo /></el-icon>
              SQL查询
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="启用" :value="1">
              <el-icon class="mr-2" color="#67C23A"><Select /></el-icon>
              启用
            </el-option>
            <el-option label="禁用" :value="0">
              <el-icon class="mr-2" color="#F56C6C"><Close /></el-icon>
              禁用
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            查询
          </el-button>
          <el-button :icon="Refresh" @click="handleReset"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><List /></el-icon>
          <span>接口列表</span>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column
          prop="apiName"
          label="接口名称"
          min-width="140"
          show-overflow-tooltip
        >
          <template #default="scope">
            <div class="api-name-cell">
              <el-icon class="mr-2"><Connection /></el-icon>
              {{ scope.row.apiName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="apiCode"
          label="接口编码"
          min-width="140"
          show-overflow-tooltip
        >
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ scope.row.apiCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="apiType"
          label="接口类型"
          width="120"
          align="center"
        >
          <template #default="scope">
            <el-tag
              :type="scope.row.apiType === 1 ? 'success' : 'primary'"
              effect="light"
            >
              <svg-icon :name="scope.row.apiType === 1?'iconfont icon-fuwu':'iconfont icon-sql'"></svg-icon>

              {{ scope.row.apiType === 1 ? 'HTTP' : 'SQL' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="httpMethod"
          label="HTTP方法"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag
              v-if="scope.row.httpMethod"
              :type="getMethodTagType(scope.row.httpMethod)"
              size="small"
            >
              {{ scope.row.httpMethod }}
            </el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="repositoryId"
          label="数据源"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag v-if="scope.row.repositoryId" type="info" size="small">
              {{ scope.row.repositoryId }}
            </el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : 'danger'"
              effect="light"
            >
              <el-icon class="mr-1">
                <Select v-if="scope.row.status === 1" />
                <Close v-else />
              </el-icon>
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="scope">
            <span class="description-text">{{
              scope.row.description || '暂无描述'
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="180"
          align="center"
        >
          <template #default="scope">
            <div class="time-cell">
              <el-icon class="mr-1"><Clock /></el-icon>
              {{ scope.row.updateTime }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="440" fixed="right" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                size="small"
                :icon="View"
                @click="handleView(scope.row)"
              >
                查看
              </el-button>
              <el-button
                size="small"
                type="primary"
                :icon="Edit"
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                type="success"
                :icon="Position"
                @click="handleTest(scope.row)"
              >
                测试
              </el-button>
              <el-button
                size="small"
                :type="scope.row.status === 1 ? 'warning' : 'success'"
                :icon="scope.row.status === 1 ? Lock : Unlock"
                @click="handleToggleStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button
                size="small"
                type="danger"
                :icon="Delete"
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <api-config-form
        ref="configFormRef"
        :form-data="currentRow"
        :is-edit="isEdit"
        :is-view="isView"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </el-dialog>

    <!-- 测试对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="接口测试"
      width="800px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <api-test-form
        :api-config="testApiConfig"
        @close="testDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, List, Setting, Link, Select, Close, Connection, Clock, View, Edit, Position, Delete, Memo,Lock , Unlock
} from '@element-plus/icons-vue'
import ApiConfigForm from './components/ApiConfigForm.vue'
import ApiTestForm from './components/ApiTestForm.vue'
import apis from '@/service/apis'
import SvgIcon from '@/components/svgIcon/index.vue'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const testDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const isView = ref(false)
const tableData = ref([])
const currentRow = ref({})
const testApiConfig = ref({})
const configFormRef = ref()

// 搜索表单
const searchForm = reactive({
  apiName: '',
  apiType: '',
  status: '',
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
})

// 生命周期
onMounted(() => {
  fetchData()
})

// 方法
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm,
    }
    const response = await apis.getApiConfigPage(params)
    tableData.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    apiName: '',
    apiType: '',
    status: '',
  })
  handleSearch()
}

const handleCreate = () => {
  currentRow.value = {}
  dialogTitle.value = '新增API接口'
  isEdit.value = false
  isView.value = false
  dialogVisible.value = true
}

const handleEdit = row => {
  currentRow.value = { ...row }
  dialogTitle.value = '编辑API接口'
  isEdit.value = true
  isView.value = false
  dialogVisible.value = true
}

const handleView = row => {
  currentRow.value = { ...row }
  dialogTitle.value = '查看API接口'
  isEdit.value = false
  isView.value = true
  dialogVisible.value = true
}

const handleDelete = async row => {
  try {
    await ElMessageBox.confirm('确定要删除这个API配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await apis.deleteApiConfig(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleToggleStatus = async row => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const action = newStatus === 1 ? '启用' : '禁用'

    await ElMessageBox.confirm(`确定要${action}这个API配置吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await apis.updateApiConfigStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleSubmit = async formData => {
  try {
    if (isEdit.value) {
      await apis.updateApiConfig(formData)
      ElMessage.success('更新成功')
    } else {
      await apis.createApiConfig(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handleCancel = () => {
  dialogVisible.value = false
}

const handleSizeChange = val => {
  pagination.pageSize = val
  pagination.current = 1
  fetchData()
}

const handleCurrentChange = val => {
  pagination.current = val
  fetchData()
}

const handleTest = row => {
  testApiConfig.value = { ...row }
  testDialogVisible.value = true
}

// 获取HTTP方法标签类型
const getMethodTagType = method => {
  const typeMap = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger',
    PATCH: 'info',
  }
  return typeMap[method] || 'info'
}
</script>

<style scoped>
.api-config-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
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

.header-right {
  display: flex;
  gap: 12px;
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  margin: 0;
}

/* 表格卡片样式 */
.table-card {
  border-radius: 8px;
}

/* 表格内容样式 */
.api-name-cell {
  display: flex;
  align-items: center;
  font-weight: 500;
}

.description-text {
  color: #606266;
  font-size: 13px;
}

.time-cell {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.text-muted {
  color: #c0c4cc;
  font-style: italic;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: wrap;
}

/* 分页样式 */
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

/* 通用样式 */
.ml-2 {
  margin-left: 8px;
}

.mr-1 {
  margin-right: 4px;
}

.mr-2 {
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .api-config-container {
    padding: 16px;
  }

  .header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-form {
    flex-direction: column;
  }

  .action-buttons {
    flex-direction: column;
  }
}

/* 卡片阴影动画 */
.header-card:hover,
.search-card:hover,
.table-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

/* 按钮动画 */
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
}
</style>
