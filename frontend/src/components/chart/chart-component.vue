<template>
  <div class="h100 w100" ref="chartContainer"></div>
</template>

<script setup>
import useChart from "/@/components/chart";
import {watchDebounced} from "@vueuse/core";

const chartContainer = ref()

const {setOptions, init, chartInstance} = useChart(chartContainer);


const props = defineProps({
  options: {
    type: Object,
    required: true
  },
  onClick: {
    type: Function,
  }
})

const emit = defineEmits(['click'])


watchDebounced(() => props.options, () => {
  setOptions(props.options)
}, {
  debounce: 1000,
  deep: true
})

onMounted(() => {
  init();
  setOptions(props.options)
  chartInstance.value.on('click', (e) => {
    emit('click', e)
  })
})

defineExpose({
  chartInstance
})
</script>

<style scoped>

</style>