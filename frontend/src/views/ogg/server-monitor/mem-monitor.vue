<template>
  <div class="h100 w100 border">
    <chart-component :options="options" @click="onClick"></chart-component>
  </div>
</template>

<script setup>
import ChartComponent from "/@/components/chart/chart-component.vue";
import commonFunction from "/@/utils/commonFunction";

const props = defineProps({
  mem: {
    type: Object,
    required: true
  }
})

var dataArr = 44;
var colorSet = {
  color: {
    type: 'linear',
    x: 0,
    y: 0,
    x2: 1,
    y2: 0,
    colorStops: [{
      offset: 0, color:   '#abdcff'// 0% 处的颜色
    }, {
      offset: 1, color:   '#0396ff' // 100% 处的颜色
    }],
    global: false // 缺省为 false
  }
};

const options = ref({
  backgroundColor: '#282a36',
  tooltip: {
    formatter: " {b} : {c}%"
  },
  series: [{
    name: "CPU使用率",
    type: "gauge",
    // center: ['20%', '50%'],
    radius: '70%',
    splitNumber: 10,
    axisLine: {
      lineStyle: {
        color: [
          [dataArr / 100, colorSet.color],
          [1, "rgba(128,145,174,0.5)"]
        ],
        width: 8
      }
    },
    axisLabel: {
      show: false,
    },
    axisTick: {
      show: false,
    },
    splitLine: {
      show: false,
    },
    itemStyle: {
      show: false,
    },
    detail: {
      formatter: function(value) {
        if (value !== 0) {
          return value+"%";
        } else {
          return 0;
        }
      },
      offsetCenter: [0, 74],
      textStyle: {
        padding: [0, 0, 0, 0],
        fontSize: 18,
        fontWeight: '700',
        color: '#abdcff'
      }
    },
    title: { //标题
      show: true,
      offsetCenter: [0, 50], // x, y，单位px
      textStyle: {
        color: "#fff",
        fontSize: 16, //表盘上的标题文字大小
        fontWeight: 400,
        fontFamily: 'PingFangSC'
      }
    },
    data: [],
    pointer: {
      itemStyle: {
        color: '#abdcff',
      },
      show: true,
      length: '80%',
      radius: '20%',
      width: 5, //指针粗细
    },
    animationDuration: 4000,
  },
    {
      name: '外部刻度',
      type: 'gauge',
      //  center: ['20%', '50%'],
      radius: '80%',
      min: 0, //最小刻度
      max: 100, //最大刻度
      splitNumber: 5, //刻度数量
      startAngle: 225,
      endAngle: -45,
      axisLine: {
        show: true,
        lineStyle: {
          width: 1,
          color: [
            [1, 'rgba(0,0,0,0)']
          ]
        }
      }, //仪表盘轴线
      axisLabel: {
        show: true,
        color: '#abdcff',
        distance: 35,
        formatter: function(v) {
          switch (v + '') {
            case '0':
              return '0';
            case '10':
              return '10';
            case '20':
              return '20';
            case '30':
              return '30';
            case '40':
              return '40';
            case '50':
              return '50';
            case '60':
              return '60';
            case '70':
              return '70';
            case '80':
              return '80';
            case '90':
              return '90';
            case '100':
              return '100';
          }
        }
      }, //刻度标签。
      axisTick: {
        show: true,
        splitNumber: 7,
        lineStyle: {
          color: colorSet.color, //用颜色渐变函数不起作用
          width: 1,
        },
        length: -8
      }, //刻度样式
      splitLine: {
        show: true,
        length: -16,
        lineStyle: {
          color: colorSet.color, //用颜色渐变函数不起作用
        }
      }, //分隔线样式
      detail: {
        show: false
      },
      pointer: {
        show: false
      }
    },
  ]
})

const setData = () => {
  // {
  //   "total": 255.66,
  //     "used": 0,
  //     "free": 144.29,
  //     "usage": 0
  // }

  if(props.mem){
    props.mem.used = (props.mem.total - props.mem.free).toFixed(2);
    props.mem.usage =
        (props.mem.used / props.mem.total * 100).toFixed(2);
      options.value.series[0].data[0]  = {
        name: '内存',
        value: props.mem.usage,
        tooltip: {
          formatter: '总大小：' + props.mem.total
              + 'GB <br/> 可用空间：'
              + props.mem.free
              + 'GB<br/>使用率：{c}% '
        }
      }
    options.value.series[0].axisLine.lineStyle.color[0][0] = props.mem.usage / 100
  }
}

watch(() => props.mem, () => {
  setData()
},{
  deep: true,
  immediate: true
})

const {copyText} = commonFunction();
const onClick = (param) => {
    copyText('总大小：' + props.mem.total
        + 'GB <br/> 可用空间：'
        + props.mem.free
        + 'GB<br/>使用空间：'+ props.mem.used + 'GB <br/>使用率：props.mem.usage% ')
}
</script>

<style scoped>

</style>