<template>
  <div class="h100 w100" ref="chartContainer"></div>
</template>

<script setup>
import useChart from '/@/components/chart'
import { useDark } from '@vueuse/core'

const chartContainer = ref()

const { setOptions, init } = useChart(chartContainer)

const isDark = useDark()
const state = reactive({
  charts: {
    theme: isDark.value ? 'dark' : '',
    bgColor: isDark.value ? 'transparent' : '',
    color: isDark.value ? '#dadada' : '#303133',
  },
})

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
})

const option = {
  backgroundColor: state.charts.bgColor,
  tooltip: {},
  textStyle: {
    color: state.charts.color,
  },
  grid: { top: 40, right: 40, bottom: 60, left: 80 },
  dataset: {},
  legend: {
    textStyle: {
      color: state.charts.color,
    },
  },
  xAxis: { type: 'category' },
  yAxis: [
    {
      splitLine: {
        show: false,
        lineStyle: { type: 'dashed', color: '#f5f5f5' },
      },
      axisLine: { show: false },
      axisTick: { show: false },
    },
  ],
  itemStyle: {
    barWidth: 12,
  },
  series: [
    {
      name: '总记录数',
      type: 'bar',
      colorBy: 'series',
      barWidth: 12,
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          {
            offset: 0,
            color: '#abdcff', // 0% 处的颜色
          },
          {
            offset: 1,
            color: '#0396ff', // 100% 处的颜色
          },
        ],
        global: false, // 缺省为 false
      },
      itemStyle: {
        //柱状图圆角
        borderRadius: [12, 12, 0, 0],
      },
    },
    {
      name: '插入',
      type: 'bar',
      colorBy: 'series',
      barWidth: 12,
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          {
            offset: 0,
            color: '#59FFB7', // 0% 处的颜色
          },
          {
            offset: 1,
            color: '#37BDFF', // 100% 处的颜色
          },
        ],
        global: false, // 缺省为 false
      },
      itemStyle: {
        //柱状图圆角
        borderRadius: [12, 12, 0, 0],
      },
    },
    {
      name: '修改',
      type: 'bar',
      barWidth: 12,
      colorBy: 'series',
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          {
            offset: 0,
            color: '#ee9ae5', // 0% 处的颜色
          },
          {
            offset: 1,
            color: '#5961f9', // 100% 处的颜色
          },
        ],
        global: false, // 缺省为 false
      },
      itemStyle: {
        //柱状图圆角
        borderRadius: [12, 12, 0, 0],
      },
    },
    {
      name: '删除',
      type: 'bar',
      barWidth: 12,
      colorBy: 'series',
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          {
            offset: 0,
            color: '#FFB2B2', // 0% 处的颜色
          },
          {
            offset: 1,
            color: '#F45353', // 100% 处的颜色
          },
        ],
        global: false, // 缺省为 false
      },
      itemStyle: {
        //柱状图圆角
        borderRadius: [12, 12, 0, 0],
      },
    },
  ],
}
const setBarChartData = () => {
  let data = []
  if (props.data) {
    data = props.data.map(t => {
      return {
        name: t.to.replace(':', ''),
        value: t.operationsTotal,
        insertTotal: t.insertTotal,
        updatesTotal: t.updatesTotal,
        deletesTotal: t.deletesTotal,
      }
    })
    let result = []

    data.forEach(item => {
      let existed = result.find(r => r.name === item.name)
      if (existed) {
        existed.value += item.value
        existed.insertTotal += item.insertTotal
        existed.updatesTotal += item.updatesTotal
        existed.deletesTotal += item.deletesTotal
      } else {
        result.push(item)
      }
    })
    data = result.sort((a, b) => b.value - a.value)
  }
  option.dataset.source = data ?? []
  setOptions(option)
}

watch(
  () => props.data,
  () => {
    setBarChartData()
  },
)

onMounted(() => {
  init()
  setBarChartData()
})
</script>

<style scoped></style>
