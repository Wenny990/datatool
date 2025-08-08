<template>
  <div class="h100 build-container flex-center">
    <div v-show="false">
      <el-progress type="dashboard" :percentage="state.step * 20">
        {{ '(' + state.step  + '/6)' + state.loadingText }}
      </el-progress>
    </div>
    <div class="flex tool">
      <div>
        <el-button type="success" @click="build" v-loading="state.loading">点击开始</el-button>
        <div class="ml6" style="display: inline" v-show="state.loading">
          <el-button v-if="isActive" @click="pause">暂停</el-button>
          <el-button v-else @click="resume">恢复</el-button>
        </div>
      </div>
      <div v-show="state.loading">

          <el-text style="line-height: 32px">
            <svg-icon name="ele-Loading" class="spin"></svg-icon>
            {{ '(' + state.step  + '/6)' + state.loadingText }}
          </el-text>
      </div>
      <div class="slider-block">
        <el-text class="mr4">停顿</el-text>
        <el-input-number v-model="timeout"  :min="20" :max="200" :step="10" >
        </el-input-number>
      </div>
    </div>
    <div class="flex-auto">
      <el-tabs v-model="state.activeName" class="h100 flex-center">
        <el-tab-pane label="源端执行" name="first" class="w100 h100">
          <shell-command ref="sourceServerShell" :curr-server-id="oggGenerateConfig.sourceServer.id"/>
        </el-tab-pane>
        <el-tab-pane label="目标端执行" name="second" class="h100" lazy>
          <shell-command ref="targetServerShell" :curr-server-id="oggGenerateConfig.targetServer.id"/>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import {useProcessApi} from "/@/api/process/index.js"
import {useProcessStore} from "/@/stores/process";
import {storeToRefs} from "pinia";
import ShellCommand from "/@/components/shellCommand/index.vue";
import {ElMessage} from "element-plus";
import {useIntervalFn} from '@vueuse/core'
import SvgIcon from "/@/components/svgIcon/index.vue";

const state = reactive({
  step: 0,
  loading: false,
  loadingText: '',
  activeName: 'first'
})

const processStore = useProcessStore();

const {oggConfig, oggGenerateConfig} = storeToRefs(processStore)

const {saveProcessParams} = useProcessApi();

const sourceServerShell = ref('')
const targetServerShell = ref('')

const build = () => {
  state.loading = true;
  state.loadingText = '正在保存抓取参数...'
  saveProcessParams({
    serverId: oggGenerateConfig.value.sourceServer.id,
    processName: oggGenerateConfig.value.extName,
    params: oggConfig.value.extParams
  }).then(resp => {
    state.loadingText = '正在保存投递参数...'
    state.step++;
    return saveProcessParams({
      serverId: oggGenerateConfig.value.sourceServer.id,
      processName: oggGenerateConfig.value.pumName,
      params: oggConfig.value.pumParams
    })
  }).then(resp => {
    state.step++;
    state.loadingText = '正在保存复制参数...'
    return saveProcessParams({
      serverId: oggGenerateConfig.value.targetServer.id,
      processName: oggGenerateConfig.value.repName,
      params: oggConfig.value.repParams
    })
  }).then(resp => {
    state.step++;
    state.loadingText = '正在保存源端初始化参数...'
    return saveProcessParams({
      serverId: oggGenerateConfig.value.sourceServer.id,
      processName: oggGenerateConfig.value.einName,
      params: oggConfig.value.einParams
    })
  }).then(resp => {
    state.step++;
    state.loadingText = '正在保存目标端初始化参数...';
    let funArr = [];
    for (let i = 0; i < oggConfig.value.rinParams.length; i++) {
      funArr.push(saveProcessParams({
        serverId: oggGenerateConfig.value.targetServer.id,
        processName: oggGenerateConfig.value.rinName + i,
        params: oggConfig.value.rinParams[i]
      }))
    }
    return Promise.all(funArr)
  }).then(resp => {
    state.loadingText = '开始执行命令...'
    buildCommand();
  })
}

const buildCommand = () => {
  let split = '\n';
  if (oggGenerateConfig.value.sourceServer.serverOs == '01') {
    split = '\r';
  }
  state.loadingText = '正在执行源端命令...'
  sourceServerShell.value.resizeScreen();
  execCommand(sourceServerShell.value, split, oggConfig.value.sourceCommand).then(() => {
    state.loadingText = '源端命令执行完成！'
    // ElMessage.success("源端命令执行完成");
    state.activeName = 'second'
    split = '\n';
    if (oggGenerateConfig.value.targetServer.serverOs == '01') {
      split = '\r';
    }
    state.activeName = 'second'
    setTimeout(() => {
      state.loadingText = '正在执行目标端命令...'
      execCommand(targetServerShell.value, split, oggConfig.value.targetCommand).then(() => {
        state.loading = false
        state.loadingText = '目标端命令执行完成！'
        ElMessage.success("执行完成！");
      });
    }, 2000)

  })
}

let index;
let resolve;
let serverShellExector;
let currCommand;
const execCommand = (serverShell, split, command) => {
  return new Promise((res) => {
    resolve = res;
    serverShellExector = serverShell;
    currCommand = command.split('\n')   // 将字符串按照换行符分割成数组
        .filter((line) => line.trim() !== '')  // 过滤掉空行
        .join(split);  // 将非空行重新组合成一个字符串
    index = 0;
    resume();
  })
}

const timeout = ref(100)

const {pause, resume, isActive} = useIntervalFn(() => {
  if (index < currCommand.length - 1 && currCommand[index + 1] == '\n') {
    // timeout.value = 3000;
  } else {
    // timeout.value = 200;
  }
  serverShellExector.onSend(currCommand[index]);
  // 输出字符串中指定位置的字符
  index++;
  if (index === currCommand.length) {
    serverShellExector.onSend('\r\n');
    serverShellExector.onSend('\r\n');
    serverShellExector.onSend('执行结束');
    serverShellExector.onSend('\r\n');
    pause()
    resolve && resolve()
  }
}, timeout,{
  immediate: false
})

pause();
</script>

<style scoped lang="scss">
.tool {
  //margin: auto;
  justify-content: space-between;
}

.slider-block {
  display: flex;
  align-items: center;
  //width: 480px;

  .demonstration {
    font-size: 14px;
    color: var(--el-text-color);
    line-height: 44px;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 0;
  }

  .demonstration + .el-slider {
    flex: 0 0 90%;
  }
}

.spin {
  animation: spin 2s infinite linear;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>