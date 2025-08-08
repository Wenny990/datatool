<template>
  <div class="about">
    <h1>This is an about page</h1>
    <el-button>click me!</el-button>
    <el-table mb-1 :data="[]" />
    <el-pagination :total="100" />
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>
