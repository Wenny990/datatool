<template>
  <div v-loading="loading" class="h100 w100 process-container">
    <div
      class="flex-center h100"
      style="border: 1px solid var(--el-border-color); padding: 8px"
    >
      <div class="mb12 mt8">
        <el-descriptions
          class="margin-top"
          :title="currProcess.processName"
          :column="3"
          border
        >
          <template #title>
            <div class="el-descriptions__title ">
              <svg-icon
                name="iconfont icon-dian"
                :color="`var(--el-color-${currProcess.status == '1'
                  ? 'primary'
                  : currProcess.status == '0'
                    ? 'warning'
                    : 'danger'})`"
              ></svg-icon>
              <el-text tag="b" size="large"
                >{{ currProcess.processName }}
              </el-text>

            </div>
          </template>
          <template #extra>
            <el-button-group size="small">
              <el-button
                v-if="!isUnfold"
                @click="isUnfold = !isUnfold"
                :icon="ArrowDownBold"
              >
                展开
              </el-button>
              <el-button
                v-if="isUnfold"
                @click="isUnfold = !isUnfold"
                :icon="ArrowUpBold"
              >
                收起
              </el-button>
              <el-button
                v-if="currProcess.status == '1'"
                type="danger"
                @click="changeProcessStatus(false)"
                :icon="VideoPause"
              >
                停止
              </el-button>
              <el-button
                v-else
                type="success"
                @click="changeProcessStatus(true)"
                :icon="VideoPlay"
              >
                启动
              </el-button>
              <el-button
                type="primary"
                @click="fetchData"

                :icon="Refresh"
                class="mr12"
              >
                刷新
              </el-button>
            </el-button-group>
          </template>
          <template v-if="isUnfold">
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">进程状态</div>
              </template>
              <el-tag
                :type="
                currProcess.status == '1'
                  ? 'primary'
                  : currProcess.status == '0'
                    ? 'warning'
                    : 'danger'
              "
                effect="dark"
              >
                {{
                  currProcess.status == '1'
                    ? '运行中'
                    : currProcess.status == '0'
                      ? '停止'
                      : '异常'
                }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">进程类型</div>
              </template>
              {{
                currProcess.type == '0'
                  ? '管理进程':
                currProcess.type == '1'
                  ? '抓取进程'
                  : currProcess.type == '2'
                    ? '投递进程'
                    : currProcess.type == '3'
                      ? '复制进程'
                      : currProcess.type == '4'
                        ? '源端初始化'
                        : '目标端初始化'
              }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">上次启动</div>
              </template>
              {{ currProcess.lastStartDate }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">延迟</div>
              </template>
              {{ currProcess.checkPointLag }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">进程id</div>
              </template>
              {{ currProcess.processId }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">检查点文件</div>
              </template>
              {{ currProcess.checkPointFile }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">检查点时间</div>
              </template>
              {{ currProcess.checkPointTime }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template #label>
                <div class="cell-item">RBA</div>
              </template>
              {{ currProcess.checkPointRba }}
            </el-descriptions-item>
          </template>

        </el-descriptions>
      </div>
      <div class="flex-center flex-auto tab-container bd">
        <el-tabs v-model="state.activeName" class="h100 flex-center">
          <el-tab-pane label="进程统计" name="first" class="w100 h100" lazy>
            <div class="w100 h100">
              <div style="text-align: right; z-index: 9999" class="mr12">
                <el-radio-group v-model="totalModel">
                  <el-radio-button label="chart">
                    <el-tooltip content="图表">
                      <svg-icon
                        name="iconfont icon-fsux_tubiao_zhuzhuangtu"
                      ></svg-icon>
                    </el-tooltip>
                  </el-radio-button>
                  <el-radio-button label="table">
                    <el-tooltip content="表格">
                      <svg-icon name="iconfont icon-table"></svg-icon>
                    </el-tooltip>
                  </el-radio-button>
                </el-radio-group>
              </div>
              <total-chart
                v-if="totalModel === 'chart'"
                :data="currProcess.details"
              ></total-chart>
              <el-table v-else :data="currProcess.details" height="100%">
                <el-table-column type="index" width="60"></el-table-column>
                <el-table-column prop="from" label="来源"></el-table-column>
                <el-table-column prop="to" label="目标"></el-table-column>
                <el-table-column
                  prop="operationsTotal"
                  label="记录数"
                  width="120"
                ></el-table-column>
                <el-table-column
                  prop="sinceTime"
                  label="开始时间"
                  width="180"
                ></el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
          <el-tab-pane label="进程参数" name="second" class="h100" lazy>
            <process-param :server-id="propsData.serverId"
                           :process-name="propsData.processName"></process-param>
          </el-tab-pane>
          <el-tab-pane label="进程日志" name="third" class="h100" lazy>
            <process-report :server-id="propsData.serverId"
                            :process-name="propsData.processName"></process-report>
          </el-tab-pane>
          <el-tab-pane
            label="Def参数"
            name="four"
            class="h100"
            lazy
            v-if="currProcess.type === 1"
          >
            <defgen-param
              :server-id="propsData.serverId"
              :process-name="propsData.processName"
            ></defgen-param>
          </el-tab-pane>
          <el-tab-pane
            label="Def文件"
            name="five"
            class="h100"
            lazy
            v-if="currProcess.type === 1"
          >
            <defgen
              :server-id="propsData.serverId"
              :process-name="propsData.processName"
            ></defgen>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import CodeEditor from '/@/components/codeEditor/index.vue'
import SvgIcon from '/@/components/svgIcon/index.vue'
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import TotalChart from '/@/views/ogg/monitor/total-chart.vue'
import { ArrowDownBold, ArrowUpBold, Refresh, VideoPause, VideoPlay } from '@element-plus/icons-vue'
import DefgenParam from '/@/views/ogg/process-info/defgen-param.vue'
import Defgen from '/@/views/ogg/process-info/defgen.vue'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'
import ProcessReport from '@/views/ogg/process-info/process-report.vue'
import ProcessParam from '@/views/ogg/process-info/process-param.vue'

const state = reactive({
  loading: false,
  activeName: 'first',
})

const { data: propsData } = defineProps({
  data: {
    type: Object,
    require: true,
  },
})

const currProcess = ref({})

const totalModel = ref('chart')

const isUnfold = ref(false);

const changeProcessStatus = isStart => {
  apis
    .startProcess(
      currProcess.value.serverId,
      currProcess.value.processName,
      isStart,
    )
    .send()
    .then(() => {
      setTimeout(() => fetchData(), 1500)
    })
}



const { send, loading } = useRequest(
  apis.getProcessInfo(propsData.serverId, propsData.processName),
)

const fetchData = () => {
  send().then(respData => {
    currProcess.value = respData
    apis
      .getProcessDetail(
        currProcess.value.serverId,
        currProcess.value.processName,
      )
      .send()
      .then(respData => {
        currProcess.value.details = respData?.details
      })

  })
}



onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.process-container {
  background-color: var(--el-bg-color);
}

.tab-container {
  //padding: 6px 12px;
  //border: 1px solid var(--el-border-color);
}
</style>
