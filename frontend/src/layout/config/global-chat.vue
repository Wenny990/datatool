<script setup>
import { useGlobalSettingStore } from '@/stores/config'
import ChatIndex from '@/views/chat/index.vue'

const globalSettingStore = useGlobalSettingStore()

const { isShowChat } = storeToRefs(globalSettingStore)
</script>

<template>
  <el-drawer v-model="globalSettingStore.isShowChat" title="智能问答" direction="rtl" size="50%">
    <chat-index></chat-index>
  </el-drawer>
</template>

<style scoped lang="scss"></style>
