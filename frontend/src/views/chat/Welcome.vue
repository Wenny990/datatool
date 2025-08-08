<script setup>
const props = defineProps({
  title: {
    type: String,
    default: () => ''
  },
  description : {
    type: String,
    default: () => ''
  },
  icon : {
    type: String,
    default: () => ''
  },
})
</script>

<template>
  <div class="flex wel-container gap16">
    <div class="wel-icon">
      <img class="h100" :src="props.icon"></img>
    </div>
    <div class="flex-center gap8">
      <h4 class="wel-title">{{props.title}}</h4>
      <div class="wel-description">{{props.description}}</div>
    </div>

  </div>
</template>

<style scoped lang="scss">
.wel-container {
  padding: 16px 12px;
  background: linear-gradient(97deg, #f2f9fe 0%, #f7f3ff 100%);
  border-radius: 12px;
}

.wel-icon {
  height: 58px;
}

.wel-title {
  font-weight: 600;
  font-size: 20px;
  line-height: 1.4;
}
</style>
