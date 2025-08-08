<template>
  <div class="h100 w100 border">
    <chart-component :options="options" ref="chart"></chart-component>
  </div>
</template>

<script setup>
import ChartComponent from "/@/components/chart/chart-component.vue";
import {onMounted} from "vue";
import commonFunction from "/@/utils/commonFunction";

const props = defineProps({
  sysFiles: {
    type: Array,
    default: () => []
  }
})

const options = ref({
  backgroundColor: '#282a36',
  legend: {
    show: false,
  },
  grid: {
    left: '5%',
    right: '10%',
    top: '10%',
    bottom: '10%',
    containLabel: true
  },
  tooltip: {
    show: "true",
  },
  xAxis: {
    type: 'value',
    axisTick: {show: false},
    axisLabel: {
      show: false,
      formatter: '{value}GB'
    },
    axisLine: {
      show: false,
      lineStyle: {
        color: '#fff',
      }
    },
    splitLine: {
      show: false
    },
  },
  yAxis: [
    {
      type: 'category',
      axisTick: {show: false},
      axisLine: {
        show: false,
        lineStyle: {
          color: '#fff',
        }
      },
      data: []
    },
    {
      type: 'category',
      axisLine: {show: false},
      axisTick: {show: false},
      axisLabel: {show: false},
      splitArea: {show: false},
      splitLine: {show: false},
      data: []
    },

  ],
  series: [
    {
      name: '总量',
      type: 'bar',
      barWidth: 14,
      yAxisIndex: 1,
      label: {
        show: true,
        position: 'right'
      },
      itemStyle: {
        normal: {
          show: true,
          color: 'rgba(128,145,174,0.5)',
          barBorderRadius: 50,
          borderWidth: 0,
          borderColor: '#333',
        }
      },
      barGap: '0%',
      barCategoryGap: '50%',
      data: [120, 132, 101, 134, 90, 230, 210, 125, 231, 132]
    },
    {
      name: '使用量',
      type: 'bar',
      barWidth: 14,
      tooltip: {
        show: false
      },
      itemStyle: {
        normal: {
          show: true,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [{
              offset: 0, color: '#FFB2B2' // 0% 处的颜色
            }, {
              offset: 1, color: '#F45353' // 100% 处的颜色
            }],
            global: false // 缺省为 false
          },
          barBorderRadius: 50,
          borderWidth: 0,
          borderColor: '#333',
        }
      },
      barGap: '0%',
      barCategoryGap: '50%',
      data: [32, 52, 41, 64, 15, 10, 32, 25, 210, 32]
    }

  ]
})

const setData = () => {
  // {
  //   "dirName": "C:",
  //     "sysTypeName": null,
  //     "typeName": null,
  //     "total": "85530243072",
  //     "free": "32064950272",
  //     "used": null,
  //     "usage": 62.51039501313298
  // }
  options.value.yAxis[0].data = props.sysFiles.map(t => {
    return t.dirName
  })
  options.value.yAxis[1].data = props.sysFiles.map(t => {
    return t.dirName
  })

  options.value.series[0].data = props.sysFiles.map(t => {
    return {
      name: t.dirName,
      tooltip: {
        formatter:  "磁盘：" + t.dirName + "<br/>总大小：" + (t.total / 1024 / 1024 / 1024).toFixed(2) + "GB <br/>" + "可用空间：" + (t.free / 1024 / 1024 / 1024).toFixed(2) + "GB<br/>" + "已使用：" + t.usage.toFixed(2) + '%'
      },
      value: t.total / 1024 / 1024 / 1024,
      label: {
        color: "#FFF",
        position: 'right',
        distance: 12,
        formatter: t.usage.toFixed(2) + '%'
      }
    }
  })

  options.value.series[1].data = props.sysFiles.map(t => {
    return {
      name: t.dirName,
      tooltip: {
        show: false
      },
      value: (t.total - t.free) / 1024 / 1024 / 1024,
      itemStyle: {
        normal: {
          show: true,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [{
              offset: 0, color: t.usage > 90 ? '#FFB2B2' : t.usage < 60 ? '#59FFB7' : '#abdcff'// 0% 处的颜色
            }, {
              offset: 1, color: t.usage > 90 ? '#F45353' : t.usage < 60 ? '#37BDFF' : '#0396ff' // 100% 处的颜色
            }],
            global: false // 缺省为 false
          },
          barBorderRadius: 50,
          borderWidth: 0,
          borderColor: '#333',
        }
      },
    }
  })
}

watch(() => props.sysFiles, () => {
  setData()
}, {
  deep: true,
  immediate: true
})

const chart = ref();

const {copyText} = commonFunction();

onMounted(() => {
  chart.value.chartInstance.on('click', (param) => {
    if (param.componentType === "series" && param.data.tooltip.formatter) {
      copyText(param.data.tooltip.formatter)
    }
  })
})
</script>

<style scoped>

</style>