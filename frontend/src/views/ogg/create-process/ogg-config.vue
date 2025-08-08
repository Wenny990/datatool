<template>
  <div class="h100">
    <el-tabs v-model="activeName" class="h100 flex-center" v-loading="state.loading">
      <el-tab-pane label="源端命令" name="first" class="w100 h100">
        <template #label>
          <svg-icon name="iconfont icon-cloudshellyunminglinghang"></svg-icon>
          源端命令
        </template>
        <div class="w100 h100">
          <code-editor v-model="oggConfig.sourceCommand" type="sql" height="100%" :disabled="false"/>
        </div>
      </el-tab-pane>
      <el-tab-pane label="目标端命令" name="second" class="h100">
        <template #label>
          <svg-icon name="iconfont icon-cloudshellyunminglinghang"></svg-icon>
          目标端命令
        </template>
        <code-editor v-model="oggConfig.targetCommand" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
      <el-tab-pane label="抽取参数" name="third" class="h100">
        <template #label>
          <svg-icon name="iconfont icon-shujutiqu"></svg-icon>
          抽取参数
        </template>
        <code-editor v-model="oggConfig.extParams" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
      <el-tab-pane label="投递参数" name="four" class="h100">
        <template #label>
          <svg-icon name="iconfont icon-lijitoudi"></svg-icon>
          投递参数
        </template>
        <code-editor v-model="oggConfig.pumParams" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
      <el-tab-pane label="复制参数" name="five" class="h100">
        <template #label>
          <svg-icon name="iconfont icon-fuzhi"></svg-icon>
          复制参数
        </template>
        <code-editor v-model="oggConfig.repParams" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
      <el-tab-pane label="源端初始化参数" name="six" class="h100" v-if="oggGenerateConfig.isInit">
        <template #label>
          <svg-icon name="iconfont icon-chushihua"></svg-icon>
          源端初始化参数
        </template>
        <code-editor v-model="oggConfig.einParams" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
      <el-tab-pane v-if="oggGenerateConfig.isInit" v-for="(item,index) in oggConfig.rinParams" :label="'目标端初始化参数' + (index +1)" :name="index +1" class="h100">
        <template #label>
          <svg-icon name="iconfont icon-chushihua"></svg-icon>
          {{'目标端初始化参数' + (index +1)}}
        </template>
        <code-editor v-model="oggConfig.rinParams[index]" type="sql" height="100%" :disabled="false"/>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import {useProcessApi} from "/@/api/process/index.js"
import {useProcessStore} from "/@/stores/process";
import {storeToRefs} from "pinia";
import CodeEditor from "/@/components/codeEditor/index.vue";
import SvgIcon from "/@/components/svgIcon/index.vue";

const activeName = ref('first')



const state = reactive({
  loading: false
})

const processStore = useProcessStore();

const { oggGenerateConfig , oggConfig } = storeToRefs(processStore)

const {generateOggConfig} = useProcessApi();

const fetchData = () => {
  state.loading = true;
  generateOggConfig(oggGenerateConfig.value).then(resp => {
    oggConfig.value = resp.data
  }).finally(() => state.loading = false)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>

</style>