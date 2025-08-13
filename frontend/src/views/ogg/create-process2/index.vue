<template>
  <div class="create-process-container h100 flex-center">
    <div class="step-container">
      <el-steps :active="active" finish-status="success">
        <el-step title="选择数据库"/>
        <el-step title="选择表"/>
        <el-step title="配置项"/>
        <el-step title="预览"/>
        <el-step title="执行"/>
        <el-step title="完成"/>
      </el-steps>
    </div>
    <div class="flex-auto container-main">
      <el-form :model="oggGenerateConfig" ref="formRef" label-width="120px">
        <el-row v-if="active === 0">
          <el-form-item label="进程名称" prop="processName" :rules="[
        { required: true, message: '请输入进程名称' },
      ]">
            <el-input v-model="oggGenerateConfig.processName" :maxlength="5"></el-input>
          </el-form-item>
          <el-form-item label="源端服务器" prop="sourceServerId" :rules="[
        { required: true, message: '请选择源端服务器' },
      ]">
            <el-select v-model="oggGenerateConfig.sourceServerId">
              <el-option v-for="item in serverList" :key="item.id" :label="item.serverName" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="目标端服务器" prop="targetServerId" :rules="[
        { required: true, message: '请选择目标端服务器' },
      ]">
            <el-select v-model="oggGenerateConfig.targetServerId">
              <el-option v-for="item in serverList" :key="item.id" :label="item.serverName" :value="item.id"/>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row v-if="active === 1">
          <el-form-item label="目标端实例" prop="targetDbSchema" :rules="[
        { required: true, message: '请选择目标端数据库实例' },
      ]">
            <el-select v-model="oggGenerateConfig.targetDbSchema" filterable>
              <el-option v-for="item in targetDbSchemaList" :key="item.schemaName" :label="item.schemaName"
                         :value="item.schemaName"/>
            </el-select>
          </el-form-item>
          <el-form-item label="源端数据库" prop="sourceDbSchema" :rules="[
        { required: true, message: '请选择源端数据库实例' },
      ]">
            <el-select v-model="oggGenerateConfig.sourceDbSchema" filterable>
              <el-option v-for="item in sourceDbSchemaList" :key="item.schemaName" :label="item.schemaName"
                         :value="item.schemaName"/>
            </el-select>
          </el-form-item>
          <el-form-item label="数据表" prop="tableList" :rules="[
        { required: true, message: '请选择要抽取的表' },
      ]">
            <el-transfer
                v-model="oggGenerateConfig.tableList"
                v-loading="state.tableLoading"
                style="text-align: left; display: inline-block"
                filterable
                :titles="['源端', '目标端']"
                :data="tableList"
                :props="{key: 'tableName',
      label: 'tableName',
      disabled: 'notkey'}"
            />
          </el-form-item>
        </el-row>
        <el-row :gutter="35" v-if="active===2" v-show="!showConfig">
          <div class="flex w100" style="position: relative">
            <div class="flex-auto flex-center h100" style="border-right: solid 1px var(--el-border-color);position: relative">
              <div style="position: sticky;top: 0">
                <svg-icon name="iconfont icon-dian" color="var(--el-color-primary)"></svg-icon>
                <el-text style="font-weight: bold">
                  源端配置
                </el-text>
              </div>
              <div class="flex-auto pd20">
                <el-form-item label="抓取进程名称" prop="extName" :rules="[
        { required: true, message: '请输入抓取进程名称' },
      ]">
                  <el-input v-model="oggGenerateConfig.extName" placeholder="最多5个字母" :max-length="5"></el-input>
                </el-form-item>
                <el-form-item label="投递进程名称" prop="pumName" :rules="[
        { required: true, message: '请输入投递进程名称' },
      ]">
                  <el-input v-model="oggGenerateConfig.pumName" placeholder="最多5个字母" :max-length="5"></el-input>
                </el-form-item>
                <el-form-item label="源端trail" prop="sourceTrailPath" :rules="[
        { required: true, message: '源端trail文件路径不能为空' },
      ]">
                  <el-input v-model="oggGenerateConfig.sourceTrailPath" placeholder="请输入源端trail文件路径"
                            clearable></el-input>
                </el-form-item>
                <el-row>
                  <el-form-item label="开启附加日志">
                    <el-checkbox v-model="oggGenerateConfig.addTranData" type="textarea"
                                 maxlength="150"></el-checkbox>
                  </el-form-item>
                  <el-form-item label="全部列" v-show="oggGenerateConfig.addTranData">
                    <el-checkbox v-model="oggGenerateConfig.allCol" type="textarea"
                                 maxlength="150"></el-checkbox>
                  </el-form-item>
                </el-row>
                <el-form-item label="初始化" prop="isInit">
                  <el-checkbox v-model="oggGenerateConfig.isInit">
                  </el-checkbox>
                </el-form-item>
                <div v-show="false">
                  <el-form-item label="文件大小">
                    <el-input-number v-model="oggGenerateConfig.megaBytes" placeholder="单位MB"
                                     :min="10"></el-input-number>
                  </el-form-item>
                  <el-form-item label="文件序号">
                    <el-input-number v-model="oggGenerateConfig.extSeqNo" placeholder="从哪个Trail 文件中读取数据"
                                     :min="0"></el-input-number>
                  </el-form-item>
                  <el-form-item label="文件rba">
                    <el-input-number v-model="oggGenerateConfig.extRba" placeholder="从哪个位置开始读取数据"
                                     :min="0"></el-input-number>
                  </el-form-item>
                </div>
                <el-row>
                  <el-form-item label="格式化">
                    <el-checkbox v-model="oggGenerateConfig.format"></el-checkbox>
                  </el-form-item>
                  <el-form-item label="版本" v-show="oggGenerateConfig.format">
                    <el-input v-model="oggGenerateConfig.formatRelease"></el-input>
                  </el-form-item>
                </el-row>
                <el-form-item label="创建子目录">
                  <el-checkbox v-model="oggGenerateConfig.createSubdirs" type="textarea"
                               maxlength="150"></el-checkbox>
                </el-form-item>
                <el-form-item label="NLS_LANG" v-show="oggGenerateConfig.sourceRepo.dataProviderType == '3'">
                  <el-select v-model="oggGenerateConfig.sourceNlsLang">
                    <el-option v-for="item in LangType" :key="item" :label="item" :value="item"/>
                  </el-select>
                </el-form-item>
                <div v-if="oggGenerateConfig.sourceRepo.dataProviderType == '2'">
                  <el-form-item label="mysql_home">
                    <el-input v-model="oggGenerateConfig.mysqlHome" placeholder="mysql的Home目录"
                              clearable></el-input>
                  </el-form-item>
                  <el-form-item label="tranlogOptions">
                    <el-input v-model="oggGenerateConfig.tranlogOptions"
                              clearable></el-input>
                  </el-form-item>
                  <el-form-item label="dbOptions">
                    <el-input v-model="oggGenerateConfig.dbOptions"
                              clearable></el-input>
                  </el-form-item>
                </div>
              </div>
            </div>
            <div class="flex-auto flex-center">
              <div style="position: sticky;top: 0">
                <svg-icon name="iconfont icon-dian" color="var(--el-color-primary)"></svg-icon>
                <el-text style="font-weight: bold">目标端配置</el-text>
              </div>
              <div class="flex-auto pd20">
                <el-form-item label="复制进程名称" prop="repName" :rules="[
        { required: true, message: '请输入复制进程名称' },
      ]">
                  <el-input v-model="oggGenerateConfig.repName" placeholder="最多5个字母" :max-length="5"></el-input>
                </el-form-item>

                <el-form-item label="目标trail" prop="targetTrailPath" :rules="[
        { required: true, message: '目标trail文件路径不能为空' },
      ]">
                  <el-input v-model="oggGenerateConfig.targetTrailPath" placeholder="请输入目标trail文件路径"
                            clearable></el-input>
                </el-form-item>
                <el-row>
                  <el-form-item label="创建检查点表">
                    <el-checkbox v-model="oggGenerateConfig.addCheckPointTable" type="textarea"
                                 maxlength="150"></el-checkbox>
                  </el-form-item>
                  <el-form-item label="检查点表" v-show="oggGenerateConfig.addCheckPointTable">
                    <el-input v-model="oggGenerateConfig.checkPointTable" placeholder="每个数据库实例共用一个"
                              maxlength="150"></el-input>
                  </el-form-item>
                </el-row>

                <el-form-item label="NLS_LANG" v-show="oggGenerateConfig.targetRepo.dataProviderType == '3'">
                  <el-select v-model="oggGenerateConfig.targetNlsLang">
                    <el-option v-for="item in LangType" :key="item" :label="item" :value="item"/>
                  </el-select>
                </el-form-item>

                <el-row>
                  <el-form-item label="多线程初始化" prop="isRange" v-if="oggGenerateConfig.isInit">
                    <el-checkbox v-model="oggGenerateConfig.isRange">
                    </el-checkbox>
                  </el-form-item>
                  <el-form-item label="线程数" prop="rinRange" v-if="oggGenerateConfig.isInit && oggGenerateConfig.isRange">
                    <el-input-number v-model="oggGenerateConfig.rinRange">
                    </el-input-number>
                  </el-form-item>
                </el-row>

              </div>
            </div>
          </div>
        </el-row>
        <el-row class="h100" v-if="active===2 && showConfig">
          <code-editor v-model="oggGenerateConfigJsonStr" type="json" :disabled="false">
            <el-button size="small" @click="saveGenerateConfigJson" type="success">保存</el-button>
          </code-editor>
        </el-row>
      </el-form>
      <div class="h100 " v-if="active===3">
        <ogg-config-panel/>
      </div>
      <div class="h100 " v-if="active>=4" v-show="active===4">
        <ogg-build/>
      </div>
      <div class="h100 " v-if="active===5">
        <el-result
            icon="success"
            title="执行完成"
            sub-title="请刷新左侧进程查看结果"
        >
          <template #extra>
            <el-text>
              <div>1、启动抓取进程</div>
              <div>2、启动源端初始化进程以及目标端初始化进程</div>
              <div>3、等待初始化进程都执行结束后</div>
              <div>4、启动投递进程及复制进程</div>
            </el-text>
          </template>
        </el-result>
      </div>
    </div>
    <div class="footer">
      <el-button @click="next(-1)" v-if="active > 0">上一步</el-button>
      <el-button type="primary" @click="next(1)" v-if="active < 5">下一步</el-button>
      <el-button v-if="active===2" @click="toEditConfig">编辑配置项</el-button>
    </div>
  </div>
</template>

<script setup>
import {useServerApi} from "/@/api/monitor_server";
import {useProcessStore} from "/@/stores/process";
import {watchEffect} from "vue";
import {useRepoApi} from "/@/api/repo";
import {initOggGenerateConfig} from './index';
import {storeToRefs} from "pinia";
import OggConfigPanel from "./ogg-config.vue";
import OggBuild from "./ogg-build.vue";
import CodeEditor from "/@/components/codeEditor/index.vue";
import {ElMessage} from "element-plus";
import SvgIcon from "/@/components/svgIcon/index.vue";

const state = reactive({
  tableLoading: false,
  schemaLoading: false
});

const active = ref(0)
const formRef = ref('')
const serverList = ref([])
const {getServerList} = useServerApi();
const {getSchemas, getTables} = useRepoApi();

const showConfig = ref(false)

const LangType = ['AMERICAN_AMERICA.ZHS16GBK', 'CHINESE_CHINA.ZHS16GBK', 'AMERICAN_AMERICA.US7ASCII', 'AMERICAN_AMERICA.UTF-8']

const processStore = useProcessStore();

const {oggGenerateConfig} = storeToRefs(processStore)

const sourceDbSchemaList = ref([])
const targetDbSchemaList = ref([])
const tableList = ref([])

watch(() => ({
  server: oggGenerateConfig.value.sourceServer,
  sourceDbSchema: oggGenerateConfig.value.sourceDbSchema
}), (oldVal, newVal) => {
  if (oldVal.server?.id !== newVal.server?.id || oldVal.sourceDbSchema != newVal.sourceDbSchema) {
    if (oggGenerateConfig.value.sourceServer?.repositorySourceId && oggGenerateConfig.value.sourceDbSchema) {
      oggGenerateConfig.value.tableList = []
      state.tableLoading = true
      getTables({
        id: oggGenerateConfig.value.sourceServer.repositorySourceId,
        schemaName: oggGenerateConfig.value.sourceDbSchema
      }).then(resp => {
        tableList.value = resp.data.map(t => {
          return {
            ...t,
            notkey: t.notkey === 1
          }
        })
      }).finally(() => state.tableLoading = false)
    }
  }
})
const next = (step) => {
  if (step === -1) {
    active.value--;
    return;
  }
  formRef.value.validate((valid) => {
    if (valid) {
      if (active.value === 0 && step === 1) {
        Promise.all([
          getSchemas(oggGenerateConfig.value.targetServer?.repositorySourceId),
          getSchemas(oggGenerateConfig.value.sourceServer?.repositorySourceId)
        ]).then(resp => {
          targetDbSchemaList.value = resp[0].data
          sourceDbSchemaList.value = resp[1].data
          active.value += step;
        })
      } else if (active.value === 1) {
        initOggGenerateConfig(oggGenerateConfig.value);
        active.value += step;
      } else {
        active.value += step;
        if (active.value > 5) active.value = 0
      }
    }
  })
}

const oggGenerateConfigJsonStr = ref('');

const toEditConfig = () => {
  showConfig.value = !showConfig.value;
  if (showConfig.value) {
    oggGenerateConfigJsonStr.value = JSON.stringify(oggGenerateConfig.value, null, 4)
  }
}
const saveGenerateConfigJson = () => {
  try {
    oggGenerateConfig.value = JSON.parse(oggGenerateConfigJsonStr.value)
  } catch (e) {
    ElMessage.warning('格式不正确')
  }
}

watchEffect(() => {
  oggGenerateConfig.value.sourceServer = serverList.value.find(t => t.id === oggGenerateConfig.value.sourceServerId)
  oggGenerateConfig.value.targetServer = serverList.value.find(t => t.id === oggGenerateConfig.value.targetServerId)
})

const props = defineProps({
  data: {
    type: Object,
    require: true
  }
})

onMounted(() => {
  getServerList().then(resp => {
    serverList.value = resp.data
  })
  oggGenerateConfig.value.sourceServerId = props.data.id;
  oggGenerateConfig.value.sourceServer = props.data;
})
</script>

<style scope lang="scss">
.create-process-container {
  padding: 12px;
  border: 1px solid var(--el-border-color);
  gap: 8px;
}

.step-container, .container-main {
  padding: 12px 20px;
  margin-top: 2px;
  border: 1px solid var(--el-border-color);
  overflow-y: auto;
}

.footer {
  display: flex;
  justify-items: center;
  align-items: center;
}
</style>
