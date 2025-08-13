<script setup>
import { Delete, Moon, Sunny } from '@element-plus/icons-vue'
import apis from '@/service/apis'
import { ElMessage } from 'element-plus'
import { useGlobalSettingStore } from '@/stores/config'
import { useDark, useToggle } from '@vueuse/core'

const globalSettingStore = useGlobalSettingStore()

const { layout, timeOut, isShowConfig, defaultSize, baseURL, socketURL } = storeToRefs(globalSettingStore)

const handleClearCache = () => {
  apis
    .clearAllCache()
    .send()
    .then(data => {
      ElMessage.success(`成功清理了 ${data} 项缓存！`)
    })
}

const isDark = useDark()

const toggleDark = useToggle(isDark)
</script>

<template>
  <el-drawer v-model="isShowConfig" title="系统配置" direction="rtl">
    <el-form>
      <el-form-item label="主题">
        <el-switch
          v-model="isDark"
          inline-prompt
          :active-icon="Sunny"
          :inactive-icon="Moon"
          @change="toggleDark"
        />
      </el-form-item>
      <el-form-item label="布局">
        <el-radio-group v-model="layout">
          <el-radio-button label="defaults">纵向</el-radio-button>
          <el-radio-button label="vertical">横向</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="组件大小">
        <el-select v-model="defaultSize" style="width: 80px">
          <el-option label="小" value="small"></el-option>
          <el-option label="中" value="default"></el-option>
          <el-option label="大" value="large"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="清理缓存">
        <el-button
          @click="handleClearCache"
          :icon="Delete"
        >清理</el-button>
      </el-form-item>
      <el-form-item label="请求超时时间(ms)">
        <el-input-number v-model="timeOut" :step="1000"></el-input-number>
      </el-form-item>
      <el-form-item label="请求baseURL">
        <el-input v-model="baseURL"></el-input>
      </el-form-item>
      <el-form-item label="webSocketURL">
        <el-input v-model="socketURL"></el-input>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<style scoped lang="scss"></style>
