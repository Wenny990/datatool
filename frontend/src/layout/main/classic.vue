<script setup>
import HeaderMenu from '@/layout/header/index.vue'
</script>

<template>
  <el-container class="h100 w100">
    <el-aside>
      <header-menu />
    </el-aside>
    <el-container class="flex-auto">
      <router-view></router-view>
    </el-container>
  </el-container>
</template>

<style scoped lang="scss"></style>
