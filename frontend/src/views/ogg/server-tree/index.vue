<template>
  <div class="ogg-server-container h100 flex-center">
    <div>
      <div class="pt6 pb6 pl6">
        <el-input
          size="small"
          v-model="state.keyword"
          style="width: 200px"
          class="mr6"
          clearable
        ></el-input>

        <el-button type="primary" size="small" @click="fetchData"
          >刷新
        </el-button>

        <!--        <el-tooltip-->
        <!--            effect="dark"-->
        <!--            content="添加"-->
        <!--        >-->
        <!--          <el-button type="success" :icon="Plus" circle size="small" @click="onEditServer({})"/>-->
        <!--        </el-tooltip>-->
      </div>
    </div>
    <div class="tree-content flex-auto" v-loading="loading">
      <el-scrollbar>
        <el-tree
          ref="treeRef"
          :data="serverList"
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
            <div class="custom-tree-node" :title="getTitle(data)">
              <div>
                <span v-if="data.serverOs" class="mr6">
                  <svg-icon
                    :name="
                      'iconfont ' +
                      (data.serverOs == '01' ? 'icon-windows' : 'icon-linux')
                    "
                    :color="data.isLoaded ? 'var(--el-color-primary)' : ''"
                  ></svg-icon>
                </span>
                <span v-else class="mr6">
                  <svg-icon :name="getIcon(data)"></svg-icon>
                </span>
                <span>{{ node.label }}</span>
              </div>
              <div class="tree-content-opt">
                <span v-if="data.processName" class="ml6">
                  <svg-icon
                    class="mr6"
                    :name="
                      'iconfont ' +
                      (data.status == '1'
                        ? 'icon-yunhang'
                        : data.status == '0'
                          ? 'icon-weibiaoti517'
                          : 'icon-yichang')
                    "
                    :color="
                      data.status == '1'
                        ? 'var(--el-color-success)'
                        : data.status == '0'
                          ? 'var(--el-color-warning)'
                          : 'var(--el-color-error)'
                    "
                  ></svg-icon>
                  <el-dropdown>
                    <svg-icon name="iconfont icon-elipsis" />
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item
                          v-if="data.status !== 1"
                          :icon="VideoPlay"
                          @click.stop="onStartProcess(node, true)"
                          >启动</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-if="data.status === 1"
                          :icon="VideoPause"
                          @click.stop="onStartProcess(node, false)"
                          >停止</el-dropdown-item
                        >
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </span>
                <div v-else>
                  <el-dropdown>
                    <svg-icon name="iconfont icon-elipsis" />
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item
                          :icon="Refresh"
                          @click.stop="onRefreshNode(node)"
                          >刷新
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Lightning"
                          @click.stop="onGoShell(data)"
                          >命令行</el-dropdown-item
                        >
                        <el-dropdown-item
                          :icon="Plus"
                          @click.stop="onAddProcess(data)"
                        >添加进程
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="Monitor"
                          @click.stop="onMonitorServer(data)"
                          >服务器监控
                        </el-dropdown-item>
                        <el-dropdown-item
                          :icon="View"
                          @click.stop="onPreviewCommand(data)"
                          >命令预览
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
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useProcessStore } from '/@/stores/process'
import SvgIcon from '/@/components/svgIcon/index.vue'
import {
  Delete,
  Lightning,
  Monitor,
  Plus,
  Refresh,
  VideoPause,
  VideoPlay,
  View,
} from '@element-plus/icons-vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'

const processStore = useProcessStore()
const serverList = ref([])
const treeRef = ref()

const state = reactive({
  keyword: '',
})

const { loading, send } = useRequest(apis.getServerList)

const fetchData = () => {
  send().then(data => {
    serverList.value = data
      .filter(t => t.serverType === 'ogg')
      .map(t => {
        return {
          ...t,
          name: t.serverName,
          isLeaf: false,
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
  if (data.processName) {
    const data1 = {
      title: '进程详情-' + data.processName,
      key: 'ProcessInfo' + '-' + data.processName,
      type: 'ProcessInfo',
      data: data,
    }
    toComponent(data1)
  } else {
    processStore.currServer = data
    processStore.currServerId = data.id
  }
}

const { send: getProcessListSend } = useRequest(
  data => apis.getProcessList(data),
  {
    immediate: false,
  },
)

const onLoadChildren = (node, resolve) => {
  if (node.data.serverOs) {
    getProcessListSend(node.data.id)
      .then(data => {
        node.data.isLoaded = true
        resolve(
          data.map(t => {
            return {
              ...t,
              name: t.processName,
              isLeaf: true,
              serverId: node.data.id,
            }
          }),
        )
      })
      .catch(error => {
        resolve([])
      })
  }
}

const onRefreshNode = node => {
  node.loaded = false // 设置loaded为false；模拟一次节点展开事件，加载重命名后的新数据；
  node.expand()
}

const onDeleteProcess = node => {
  // const data = node.data
  // apis.deleteProcess(data.serverId, data.name).send().then(data => {
  //   ElMessage.success(data)
  //   onRefreshNode(node.parent)
  // })
}

const onStartProcess = (node, isStart) => {
  const data = node.data
  apis
    .startProcess(data.serverId, data.name, isStart)
    .send()
    .then(() => {
      onRefreshNode(node.parent)
    })
}

const onEditServer = data => {
  processStore.currServer = data
  let data1
  if (data.id) {
    data1 = {
      title: '编辑服务器-' + data.serverName,
      key: 'ServerEdit' + '-' + data.id,
      type: 'ServerEdit',
      data: data,
    }
  } else {
    data1 = {
      title: '新增服务器',
      key: 'ServerEdit',
      type: 'ServerEdit',
      data: data,
    }
  }
  toComponent(data1)
}

const onGoShell = data => {
  processStore.currServerId = data.id
  const data1 = {
    title: '服务器命令行-' + data.serverName,
    key: 'ServerShell' + '-' + data.id,
    type: 'ServerShell',
    data: data,
  }
  toComponent(data1)
}

const onMonitorServer = data => {
  const data1 = {
    title: '服务器监控-' + data.serverName,
    key: 'ServerMonitor' + '-' + data.id,
    type: 'ServerMonitor',
    data: data,
  }
  toComponent(data1)
}

const onPreviewCommand = data => {
  const data1 = {
    title: '服务器命令预览-' + data.serverName,
    key: 'CommandPreview' + '-' + data.id,
    type: 'CommandPreview',
    data: data,
  }
  toComponent(data1)
}

const onAddProcess = data => {
  const data1 = {
    title: '添加进程',
    key: 'CreateProcess',
    type: 'CreateProcess',
    data: data,
  }
  toComponent(data1)
}

const toComponent = data => {
  processStore.addTab(data)
}

const getIcon = data => {
  if (data.type === 0) {
    return 'iconfont icon-component'
  } else if (data.type === 1) {
    return 'iconfont icon-shujutiqu'
  } else if (data.type === 2) {
    return 'iconfont icon-lijitoudi'
  } else if (data.type === 3) {
    return 'iconfont icon-fuzhi'
  } else {
    return 'iconfont icon-chushihua'
  }
}
const getTitle = data => {
  let title
  if (data.serverOs) {
    title =
      '服务器地址：' +
      data.serverIp +
      '，操作系统：' +
      (data.serverOs == '01' ? 'windows' : 'linux')
  } else {
    title =
      (data.type == '1'
        ? '抓取'
        : data.type == '2'
          ? '投递'
          : data.type == '3'
            ? '复制'
            : data.type == '4'
              ? '源端初始化'
              : '目标端初始化') +
      '进程，状态：' +
      (data.status == '1' ? '运行中' : data.status == '0' ? '已停止' : '异常')
    title += ' ,检查点延迟：' + data.lagAtChkpt
    title += ' ,上次更新：' + data.timeSinceChkpt
  }

  return title
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
  flex: 1;
  margin-right: 12px;
  display: flex;
  justify-content: space-between;
}

.tree-content-opt:hover {
  color: var(--el-color-primary);
}
</style>
