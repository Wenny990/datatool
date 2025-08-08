<template>
  <div class="ogg-server-container h100 flex-center">
    <div>
      <div class="pt6 pb6 pl6">
        <el-input
          size="small"
          v-model="state.keyword"
          style="width: 150px"
          class="mr6"
          clearable
        ></el-input>

        <el-button type="primary" size="small" @click="fetchData"
          >刷新
        </el-button>
        <el-button type="success" size="small" @click="onEditGroup({})"
          >添加
        </el-button>
      </div>
    </div>
    <div class="tree-content flex-auto" v-loading="loading">
      <el-scrollbar>
        <el-tree
          ref="treeRef"
          :data="dataxJobGroupList"
          :props="{
            children: 'children',
            label: 'name',
            isLeaf: 'isLeaf',
          }"
          lazy
          :load="onLoadChildren"
          highlight-current
          @node-click="handleNodeClick"
          :filter-node-method="filterNode"
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <div class="flex-auto mr6" :title="node.label">
                <div class="flex align-center gap6">
                  <el-icon v-if="data.isLeaf" color="#80ADFF" size="18">
                    <Document />
                  </el-icon>
                  <el-icon v-else-if="data.isLoaded" color="#FFDC8A">
                    <FolderOpened />
                  </el-icon>
                  <el-icon v-else color="#FFDC8A">
                    <Folder />
                  </el-icon>
                  <span>{{ node.label }}</span>
                </div>
              </div>
              <div v-if="data.isLoaded" class="text-p mr6">
                {{node?.childNodes?.length}}
              </div>
              <div class="tree-content-opt">
                <div @click.stop>
                  <el-dropdown trigger="click">
                    <el-icon>
                      <MoreFilled />
                    </el-icon>
                    <template #dropdown>
                      <el-dropdown-menu v-if="!data.isLeaf" @click.stop>
                        <el-dropdown-item
                          :icon="Refresh"
                          @click.stop="onRefreshNode(node)"
                          >刷新
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Edit"
                          @click.stop="onEditGroup(data)"
                          >编辑
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Plus"
                          @click.stop="onAddJob(data)"
                          >添加任务
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="DocumentAdd"
                          @click.stop="onBatchAddJob(node)"
                          >批量添加任务
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Monitor"
                          @click.stop="onBatchExecJob(node)"
                          >批量执行任务
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Delete"
                          @click.stop="onDeleteGroup(data)"
                          >删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                      <el-dropdown-menu v-else>
                        <!--                        <el-dropdown-item :icon="Odometer" @click.stop="onFrontExecute(data)">前端执行</el-dropdown-item>-->
                        <el-dropdown-item
                          :icon="Monitor"
                          @click.stop="showBackupExecute(data)"
                          >后台执行
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="UploadFilled"
                          @click.stop="onUpload(data)"
                          >上传任务
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Edit"
                          @click.stop="onEditJob(data)"
                          >编辑
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Delete"
                          @click.stop="onDeleteJob(node)"
                          >删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </template>
        </el-tree>
      </el-scrollbar>
    </div>
    <el-dialog v-model="execDialog" :title="currentJob.jobName">
      <div class="flex-center" style="height: 55vh">
        <el-form
          :model="currentJob.splitParams"
          label-width="100px"
          label-position="left"
          ref="formRef"
          :rules="rules"
        >
          <el-form-item label="执行方式" prop="type">
            <el-radio-group v-model="currentJob.splitParams.type">
              <el-radio-button value="none">直接执行</el-radio-button>
              <el-radio-button value="date">日期区间</el-radio-button>
              <el-radio-button value="range">主键区间</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="开始日期"
            v-if="currentJob.splitParams.type === 'date'"
            prop="startDate"
          >
            <el-date-picker
              v-model="currentJob.splitParams.startDate"
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item
            label="结束日期"
            v-if="currentJob.splitParams.type === 'date'"
            prop="endDate"
          >
            <el-date-picker
              v-model="currentJob.splitParams.endDate"
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item
            label="间隔月数"
            v-if="currentJob.splitParams.type === 'date'"
            prop="step"
          >
            <el-input-number
              v-model="currentJob.splitParams.step"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="最小值"
            v-if="currentJob.splitParams.type === 'range'"
            prop="startValue"
          >
            <el-input-number
              v-model="currentJob.splitParams.startValue"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="最大值"
            v-if="currentJob.splitParams.type === 'range'"
            prop="endValue"
          >
            <el-input-number
              v-model="currentJob.splitParams.endValue"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="间隔值"
            v-if="currentJob.splitParams.type === 'range'"
            prop="step"
          >
            <el-input-number
              v-model="currentJob.splitParams.step"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="条件语句"
            v-if="currentJob.splitParams.type !== 'none'"
            prop="replaceStr"
          >
            <code-editor
              v-model="currentJob.splitParams.replaceStr"
              :disabled="false"
              type="sql"
              height="200px"
              placeholder="使用#{0} 和 #{1} 替换开始值和结束值"
            ></code-editor>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div>
          <el-button @click="onBackupExecute" type="primary">执行</el-button>
          <el-button @click="execDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  Delete,
  Document,
  DocumentAdd,
  Edit,
  Folder,
  FolderOpened,
  Monitor,
  MoreFilled,
  Plus,
  Refresh,
  UploadFilled,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import apis from '@/service/apis.js'
import { useDataXStore } from '@/stores/datax'
import { storeToRefs } from 'pinia'
import CodeEditor from '@/components/codeEditor/index.vue'
import { useRequest } from 'alova/client'

const dataxStore = useDataXStore()

const { runningJobList } = storeToRefs(dataxStore)

const { addTab } = dataxStore

const treeRef = ref()

const state = reactive({
  loading: false,
  keyword: '',
})

const rules = {
  step: [{ required: true, message: '间隔不能为空' }],
  startDate: [{ required: true, message: '开始日期不能为空' }],
  endDate: [{ required: true, message: '结束日期不能为空' }],
  startValue: [{ required: true, message: '开始值不能为空' }],
  endValue: [{ required: true, message: '结束值不能为空' }],
  replaceStr: [{ required: true, message: '条件语句不能为空' }],
}

const {
  loading,
  data: dataxJobGroupList,
  send,
  update,
} = useRequest(apis.getDataxJobGroupList(), {
  immediate: false,
})

const fetchData = () => {
  send().then(() => {
    dataxJobGroupList.value = dataxJobGroupList.value.map(t => {
      return {
        ...t,
        name: t.groupName,
      }
    })
  })
}

watch(
  () => state.keyword,
  val => {
    treeRef.value.filter(val)
  },
)

const filterNode = (value, data) => {
  if (!value) return true
  return data.name.toUpperCase().includes(value.toUpperCase())
}

const handleNodeClick = data => {
  if (data.isLeaf) {
    onEditJob(data)
  }
}

const { send: loadChildSend } = useRequest(
  data => apis.getDataxJobList(data.id),
  {
    immediate: false,
  },
)

const onLoadChildren = (node, resolve) => {
  if (!node.data.id) {
    return false
  }
  loadChildSend(node.data).then(data => {
    node.data.isLoaded = true
    data = data.map(t => {
      return {
        ...t,
        name: t.jobName,
        isLeaf: true,
        groupId: node.data.id,
      }
    })
    resolve(data)
  })
}

const onRefreshNode = node => {
  node.loaded = false // 设置loaded为false；模拟一次节点展开事件，加载重命名后的新数据；
  node.expand()
}

const onDeleteGroup = data => {
  ElMessageBox.confirm(`此操作将删除任务组 ${data.name} ，是否继续?`, '提示', {
    type: 'warning',
  })
    .then(() => {
      apis.getDataxJobList(data.id).then(respData => {
        if (respData && respData.length > 0) {
          ElMessage.warning('当前任务组下存在任务，不允许删除')
        } else {
          apis
            .delDataxJobGroup(data.id)
            .send()
            .then(() => {
              ElMessage.success('删除成功')
              fetchData()
            })
        }
      })
    })
    .catch(() => {})
}

const onEditJob = data => {
  const data1 = {
    title: '编辑任务-' + data.jobName,
    key: 'JobBuild-' + data.id,
    type: 'JobBuild',
    data: data,
  }
  addTab(data1)
}

const onUpload = data => {
  apis.uploadDataxJob(data.id).then(resp => {
    ElMessage.success('任务：' + data.jobName + '上传成功!')
  })
}
const onAddJob = data => {
  const data1 = {
    title: '创建任务',
    key: 'JobBuild',
    type: 'JobBuild',
    data: {
      type: 'create',
      groupId: data.id,
    },
  }
  addTab(data1)
}

const onBatchAddJob = node => {
  const data = node.data
  const data1 = {
    title: '批量创建任务',
    key: 'BatchCreate',
    type: 'BatchCreate',
    data: {
      groupId: data.id,
    },
  }
  addTab(data1)
  dataxStore.currNode = node
}

const onDeleteJob = node => {
  let data = node.data
  ElMessageBox.confirm(`此操作将删除任务 ${data.jobName} ，是否继续?`, '提示', {
    type: 'warning',
  })
    .then(() => {
      apis.deleteDataxJob(data.id).then(() => {
        ElMessage.success('删除成功')
        onRefreshNode(node.parent)
      })
    })
    .catch(() => {})
}

const onFrontExecute = data => {
  const data1 = {
    title: '执行任务-' + data.jobName,
    key: 'ExecuteJob-' + data.id,
    type: 'ExecuteJob',
    data: data,
  }
  addTab(data1)
}

const execDialog = ref(false)
const currentJob = ref({})
const formRef = ref()
const showBackupExecute = async data => {
  currentJob.value = await apis.getDataxJob(data.id)
  execDialog.value = true
}

const { send: executeDataxJobSend } = useRequest(
  data => apis.executeDataxJob(data),
  {
    immediate: false,
  },
)

const onBatchExecJob = async node => {
  if (!node.loaded) {
    ElMessage.warning('请先刷新节点')
    return
  }
  for (const t of node.childNodes) {
    const id = t.data.id
    const jobName = t.data.jobName
    const uuid = await executeDataxJobSend(id)
    runningJobList.value.unshift({
      uuid: uuid,
      jobName: jobName,
      status: -1,
    })
    execDialog.value = false
  }
  ElMessage.success(node.childNodes.length + '任务提交成功!')
}

const onBackupExecute = () => {
  if (currentJob.value.splitParams.type === 'none') {
    apis
      .saveDataxJob(currentJob.value)
      .send()
      .then(() => {
        apis
          .executeDataxJob(currentJob.value.id)
          .send()
          .then(uuid => {
            ElMessage.success('任务提交成功，返回任务标识：' + uuid)
            execDialog.value = false
            runningJobList.value.unshift({
              uuid: uuid,
              jobName: currentJob.value.jobName,
              status: -1,
            })
          })
      })
  } else {
    formRef.value.validate().then(() => {
      apis
        .saveDataxJob(currentJob.value)
        .send()
        .then(() => {
          return apis.executeDataxJobByRange(currentJob.value.id).send()
        })
        .then(uuid => {
          execDialog.value = false
          ElMessage.success('任务提交成功，返回任务标识：' + uuid)
          runningJobList.value.unshift({
            uuid: uuid,
            jobName: currentJob.value.jobName,
            status: -1,
          })
        })
    })
  }
}

const onEditGroup = data => {
  ElMessageBox.prompt('填写任务组名称', '提示', {
    inputValue: data.groupName,
    inputValidator: val => {
      if (
        val === undefined ||
        val === null ||
        val.length < 1 ||
        val.length > 216
      ) {
        return '任务组名称异常'
      } else {
        return true
      }
    },
    inputErrorMessage: '任务组名称不能为空',
  })
    .then(({ value }) => {
      data.groupName = value
      apis
        .saveDataxJobGroup(data)
        .send()
        .then(resp => {
          ElMessage.success('保存成功')
          fetchData()
        })
    })
    .catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
$border: 1px solid var(--el-border-color);

.ogg-server-container {
  border: $border;
}

.tree-content {
  border: $border;
  background-color: var(--el-fill-color-blank);
}

.custom-tree-node {
  width: 80%;
  flex: 1;
  margin-right: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .tree-content-opt {
    visibility: hidden;
  }

  &:hover{
    .tree-content-opt {
      visibility: visible;
    }
  }
}

.el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content{
  .tree-content-opt {
    visibility: visible;
  }
}

.tree-content-opt:hover {
  color: var(--el-color-primary);
}
</style>
