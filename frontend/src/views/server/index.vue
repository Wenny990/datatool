<template>
  <div class="server-container h100">
    <div class="flex-center h100">
      <div class="pt10 pb10">
        <el-button @click="fetchData"> 刷新</el-button>
        <el-button type="primary" @click="edit({})"> 新增</el-button>
      </div>
      <div class="flex-auto border">
        <el-table
          :data="serverList"
          stripe
          highlight-current-row
          v-loading="loading"
          height="100%"
          @row-dblclick="edit"
        >
          <el-table-column type="index"></el-table-column>
          <el-table-column prop="serverName" label="名称"></el-table-column>
          <el-table-column prop="serverIp" label="服务器地址">
          </el-table-column>
          <el-table-column prop="serverType" label="类型"></el-table-column>
          <el-table-column prop="serverOs" label="操作系统">
            <template v-slot:default="{ row }">
              {{ row.serverOs == '01' ? 'Windows' : 'Linux' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template v-slot:default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'"
                >{{ row.status ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column width="180" label="操作">
            <template v-slot:default="scope">
              <el-tooltip content="编辑">
                <el-button type="primary" @click="edit(scope.row)" circle plain>
                  <el-icon class="el-icon">
                    <Edit />
                  </el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除">
                <el-button type="danger" @click="del(scope.row)" circle plain>
                  <el-icon class="el-icon">
                    <Delete />
                  </el-icon>
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog title="服务器编辑" v-model="state.dialogVisible" width="55%">
      <div style="height: 50vh">
        <el-scrollbar height="100% pd20">
          <el-form class="mg20"
            :model="oggServer"
            ref="formRef"
            size="default"
            label-width="120px"
          >
            <el-form-item
              label="名称"
              prop="serverName"
              :rules="[{ required: true, message: '请输入服务器名称' }]"
            >
              <el-input v-model="oggServer.serverName"></el-input>
            </el-form-item>
            <el-form-item
              label="服务器地址"
              prop="serverIp"
              :rules="[{ required: true, message: '请输入服务器地址' }]"
            >
              <el-input v-model="oggServer.serverIp"></el-input>
            </el-form-item>
            <el-form-item
              label="服务器端口"
              prop="serverPort"
              :rules="[{ required: true, message: '服务器端口' }]"
            >
              <el-input-number
                v-model.number="oggServer.serverPort"
              ></el-input-number>
            </el-form-item>
            <el-form-item
              label="操作系统"
              prop="serverOs"
              @change="handleOsChange"
              :rules="[{ required: true, message: '操作系统' }]"
            >
              <el-radio-group v-model="oggServer.serverOs">
                <el-radio-button value="01">
                  <svg-icon name="iconfont icon-windows" />
                </el-radio-button>
                <el-radio-button value="02">
                  <svg-icon name="iconfont icon-linux" />
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="用户名"
              prop="serverUser"
              :rules="[{ required: true, message: '请输入用户名' }]"
            >
              <el-input v-model="oggServer.serverUser"></el-input>
            </el-form-item>
            <el-form-item
              label="密码"
              prop="serverPass"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <el-input
                type="password"
                v-model="oggServer.serverPass"
                show-password
                clearable
              ></el-input>
            </el-form-item>
            <el-form-item
              label="服务器类型"
              prop="serverType"
              :rules="[{ required: true, message: '服务器类型' }]"
            >
              <el-radio-group v-model="oggServer.serverType">
                <el-radio-button value="ogg">ogg</el-radio-button>
                <el-radio-button value="datax">datax</el-radio-button>
                <el-radio-button value="other">其它</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              v-if="oggServer.serverType === 'ogg'"
              label="ogg路径"
              prop="oggPath"
              :rules="[{ required: true, message: '请输入ogg路径' }]"
            >
              <el-input v-model="oggServer.oggPath"></el-input>
            </el-form-item>
            <el-form-item
              v-if="oggServer.serverType === 'datax'"
              label="datax路径"
              prop="oggPath"
              :rules="[{ required: true, message: '请输入datax路径' }]"
            >
              <el-input v-model="oggServer.oggPath"></el-input>
            </el-form-item>
            <el-form-item
              label="选择数据源"
              prop="repositorySourceId"
              :rules="[{ required: false, message: '请选择数据源' }]"
            >
              <el-select v-model="oggServer.repositorySourceId">
                <el-option
                  v-for="item in repoList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              label="字符集编码"
              prop="characterSet"
              :rules="[{ required: true, message: '请选择字符集' }]"
            >
              <el-select v-model="oggServer.characterSet">
                <el-option
                  v-for="item in CharacterSetList"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-switch v-model="oggServer.status"></el-switch>
            </el-form-item>
          </el-form>
        </el-scrollbar>

      </div>
      <template #footer>
        <el-button @click="state.dialogVisible = false">取消</el-button>
        <el-button
          type="success"
          @click="onVerifyServer"
          v-loading="verifyLoading"
          >测试
        </el-button>
        <el-button type="primary" @click="onSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ServerIndex">
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Monitor } from '@element-plus/icons-vue'
import apis from '@/service/apis'
import { useRequest } from 'alova/client'

const oggServer = ref({
  serverType: 'ogg',
  characterSet: 'UTF-8',
  status: true,
})

const CharacterSetList = [
  'UTF-8',
  'GBK',
  'GB2312',
  'GB18030',
  'UTF-16BE',
  'UTF-16LE',
  'UTF-16',
  'BIG5',
  'UNICODE',
  'US-ASCII',
]

const { send: verifyServerSend, loading: verifyLoading } = useRequest(
  data => apis.verifyServer(data),
  { immediate: false },
)
const onVerifyServer = () => {
  formRef.value.validate(valid => {
    if (valid) {
      verifyServerSend(oggServer.value).then(respData => {
        ElMessage.success(respData.replace(/,/g, ' '))
      })
    }
  })
}

const onSave = () => {
  formRef.value.validate(valid => {
    if (valid) {
      apis
        .saveServer(oggServer.value)
        .send()
        .then(() => {
          ElMessage.success('保存成功')
          state.dialogVisible = false
        })
    }
  })
}

const handleOsChange = (os) => {
  oggServer.value.characterSet = os === '01' ? 'GBK' : 'UTF-8'
}

const state = reactive({
  dialogVisible: false,
  monitorVisible: false,
})

const { send, data: serverList, loading } = useRequest(apis.getServerList)

const fetchData = () => {
  send()
}

const { data: repoList } = useRequest(apis.getRepo)

onMounted(() => {
  fetchData()
})

const formRef = ref()
const edit = row => {
  oggServer.value = Object.assign({
    serverType: 'ogg',
    characterSet: 'UTF-8',
    status: true,
    serverPort: 22,
    serverOs: '02'
  }, row)
  state.dialogVisible = true
}

const onMonitor = row => {
  oggServer.value = row
  state.monitorVisible = true
}
const del = row => {
  ElMessageBox.confirm('确认删除?', '提示', {
    type: 'warning',
  }).then(() => {
    apis
      .delServer(row.id)
      .send()
      .then(() => {
        fetchData()
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
      })
  })
}
</script>

<style lang="scss" scoped></style>
