<script setup>
import HeaderMenu from '@/layout/header/index.vue'
</script>

<template>
  <el-container class="h100">
    <el-header>
      <header-menu />
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<style scoped lang="scss"></style>
