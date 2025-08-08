<template>
  <div class="h100">
    <div class="flex-center h100">
      <div class="system-menu-search pt10 pb10">
        <el-button v-on:click="fetchData"> 刷新</el-button>
        <el-button type="primary" v-on:click="onEdit({})"> 新增</el-button>
      </div>
      <div class="flex-auto border">
        <el-table :data="data" @rowDblclick="onEdit" border height="100%">
          <el-table-column prop="name" label="名称"></el-table-column>
          <el-table-column prop="host" label="url"></el-table-column>
          <el-table-column prop="dataSourceName" label="类型"></el-table-column>
          <el-table-column prop="username" label="用户名"></el-table-column>
          <el-table-column label="操作">
            <template #default="{row}">
              <el-tooltip content="编辑">
                <el-button
                  type="primary"
                  @click="onEdit(row)"
                  circle
                  plain
                  :icon="Edit"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="删除">
                <el-button
                  type="danger"
                  @click="del(row)"
                  circle
                  plain
                  :icon="Delete"
                ></el-button>
              </el-tooltip>
            </template>

          </el-table-column>
        </el-table>
      </div>
    </div>
    <repo-edit :on-save="fetchData" ref="repoEditRef"></repo-edit>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import RepoEdit from './repo-edit/index.vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'

const current_row = ref({})
const state = reactive({
  activeName: 'first',
  dialogVisible: false,
  loading: false,
  testLoading: false,
  testFlag: 0,
  tableData: {
    tableList: [],
    tableHeader: [
      {
        type: 'index',
        width: '80',
      },
      {
        prop: 'name',
        label: '名称',
      },
      {
        prop: 'host',
        label: 'url',
      },
      {
        prop: 'dataSourceName',
        label: '类型',
      },
      {
        prop: 'username',
        label: '用户名',
        width: '120',
      },
      {
        prop: 'createTime',
        label: '创建时间',
      },
      {
        label: '操作',
        type: 'slot',
        slotName: 'operation',
        width: '160',
        fixed: 'right',
      },
    ],
  },
})
const { loading, data, send } = useRequest(apis.getRepo)

const repoEditRef = ref()
/**
 * 查询数据
 */
const fetchData = () => {
  send().then(respData => {
    state.tableData.tableList = respData
  })
}
onMounted(() => {
  fetchData()
})

const onEdit = row => {
  repoEditRef.value.openDialog(row)
}
const del = obj => {
  ElMessageBox.confirm('确认删除此连接?', '正在使用此连接将会收到影响', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    apis
      .delRepo(obj.id)
      .send()
      .then(() => {
        ElMessage.success('删除成功!')
        fetchData()
      })
  })
}
</script>

<style></style>
