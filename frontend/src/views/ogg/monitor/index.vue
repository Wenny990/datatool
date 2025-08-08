<template>
  <div class="h100 w100 border" ref="chartContainerWp">
    <el-scrollbar height="100%">
      <div style="height: 680px; position: relative" id="chart-container">
        <div
          class="flex"
          style="
            position: absolute;
            right: 12px;
            top: 10px;
            z-index: 1000;
            cursor: pointer;
            color: #e0dddd;
          "
        >
          <el-tooltip content="刷新数据" effect="dark">
            <SvgIcon
              class="mr10"
              name="iconfont icon-shuaxin"
              @click="fetchData"
              :size="20"
              v-show="!loading"
            ></SvgIcon>
          </el-tooltip>
          <el-tooltip content="正在刷新" effect="dark">
            <SvgIcon
              class="mr10 icon-rotate"
              name="ele-Loading"
              @click="fetchDataNoCache"
              :size="20"
              v-show="loading"
              color="white"
            ></SvgIcon>
          </el-tooltip>
          <el-tooltip content="全屏" effect="dark">
            <SvgIcon
              class="mr10 cursor-pointer"
              name="iconfont icon-fullscreen-expand"
              @click="toggle"
              :size="20"
            ></SvgIcon>
          </el-tooltip>
        </div>
        <div
          id="chart-panel"
          class="h100 w100"
          ref="chartContainer"
          v-loading="loading"
        ></div>
      </div>
    </el-scrollbar>
    <el-dialog v-model="showDetails" :title="process.name">
      <div style="height: 40vh">
        <total-chart :data="process.details"></total-chart>
      </div>
    </el-dialog>

    <el-dialog v-model="showServer" :title="server.name">
      <div style="height: 70vh">
        <server-monitor :data="server"></server-monitor>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import useChart from '/@/components/chart'
import { onMounted } from 'vue'
import symbol from './monitor.js'
import { useFullscreen, useElementSize } from '@vueuse/core'
import TotalChart from './total-chart.vue'
import ServerMonitor from '/@/views/ogg/server-monitor/index.vue'
import { groupBy } from 'lodash-es'
import apis from '@/service/apis'

const chartContainerWp = ref()
const { width: refWidth, height: refHeight } = useElementSize(chartContainerWp)
const chartContainer = ref()
const process = ref({})
const server = ref({})

const { setOptions, init, chartInstance } = useChart(chartContainer)
const { toggle } = useFullscreen(chartContainerWp)

function getStatusColor(status) {
  return status == '1'
    ? '#157eff'
    : status == '0'
      ? '#e6a23c'
      : status == '3'
        ? '#f56c6c'
        : '#67c23a'
}

function getType(type) {
  return type == 'sub'
    ? '发布订阅'
    : type == '1'
      ? '抓取'
      : type == '2'
        ? '投递'
        : type == '3'
          ? '复制'
          : type == '4'
            ? '源端初始化'
            : '目标端初始化'
}

function findChild(data, child, key, value) {
  let result
  data.forEach(t => {
    const item = t[child].find(t1 => t1[key] === value)
    if (item) {
      result = item
    }
  })
  return result
}

const loading = ref(false)
const updateTime = ref()
const fetchData = () => {
  loading.value = true
  apis
    .getMonitorInfo()
    .send()
    .then(respData => {
      if (respData.length > 0) {
        let maxUpdateTime = respData[0].updateTime
        respData.forEach(t => {
          if (t.updateTime > maxUpdateTime) maxUpdateTime = t.updateTime
        })
        updateTime.value = maxUpdateTime
      }

      draw(respData)
    })
    .finally(() => (loading.value = false))
}

const fetchDataNoCache = () => {
  loading.value = true
  apis
    .getMonitorInfoNoCache()
    .send()
    .then(respData => {
      draw(respData)
    })
    .finally(() => (loading.value = false))
}

const draw = monitorData => {
  let data = []
  let height, width, stepX
  let target, source
  let extHeight = 0
  let pumHeight = 0
  let repHeight = 0
  let subHeight = 0
  const stepY = 80

  const moveLinkData = []
  const linkData = []
  monitorData.forEach(t => {
    t.type = 'server'
    t.status = t.process.find( t1 => t1.processName === 'MGR')?.status
    t.process = t.process.map(t1 => {
        let speed = ''
        if (t1.details) {
          t1.operationsTotal = t1.details.reduce(
            (pre, curr) => pre + curr.operationsTotal,
            0,
          )
          t1.sinceTime = t1.details[0].sinceTime
          speed =
            Math.ceil(
              (t1.operationsTotal /
                ((new Date() - new Date(t1.sinceTime)) / 1000)) *
                10,
            ) /
              10 +
            ' 条/秒'
        } else {
          speed = '0 条/秒'
        }
        return {
          ...t1,
          name: t1.processName,
          speed: speed,
        }
      })
      .filter(
        t1 => t1.name.indexOf('EIN') !== 0 && t1.name.indexOf('RIN') !== 0 && t1.name !== 'MGR',
      )
      .sort((a, b) => (a.name < b.name ? -1 : 1))
    if (t.pubSubInfo) {
      t.pubSubInfo = t.pubSubInfo
        .map(t => {
          return {
            ...t,
            type: 'sub',
            name: t.publication,
            realStatus: t.status,
            status:
              t.status === 3 || t.status === 4 ? 1 : t.status === 2 ? 0 : 3,
          }
        })
        .sort((a, b) => (a.publisher < b.publisher ? -1 : 1))

      t.pubSubGroup = groupBy(t.pubSubInfo, 'publisher')
    }
  })

  target = monitorData.filter(
    t => t.process.findIndex(t1 => t1.name.indexOf('REP') === 0) > -1,
  )
  source = monitorData.filter(
    t => t.process.findIndex(t1 => t1.name.indexOf('REP') === 0) < 0,
  )
  width = document.getElementById('chart-panel').clientWidth
  stepX = width / 5 + 20

  const buildData = (data, target) => {
    data.forEach(t => {
      let extProcess = t.process.filter(t1 => t1.name.indexOf('PUM') !== 0)
      let pumProcess = t.process.filter(t1 => t1.name.indexOf('PUM') === 0)
      let pubSubInfo = t.pubSubInfo || []
      const start = Math.max(extHeight, pumHeight, subHeight)
      const end = Math.max(
        start,
        start +
          (Math.max(extProcess.length, pumProcess.length, pubSubInfo.length) -
            1) *
            stepY,
      )
      t.y = start + (end - start) / 2
      t.x = 0
      if (t.process.length === 0) {
        extHeight += stepY
      }
      addNode(t, 'server', t.status && t.status != 1 )
      extProcess.forEach(t1 => {
        t1.y = extHeight
        t1.x = 1 * stepX
        addNode(t1, 'ext', t1.status != '1')
        addLink(t, t1)
        let pumNode = pumProcess.find(
          p => p.name === t1.name.replace('EXT', 'PUM'),
        )
        if (pumNode) {
          pumNode.y = t1.y
          pumNode.x = 2 * stepX
          addNode(pumNode, 'pum', pumNode.status != '1')
          addLink(t1, pumNode, getStatusColor(t1.status))
          let repNode = findChild(
            target,
            'process',
            'name',
            pumNode.name.replace('PUM', 'REP'),
          )
          if (repNode) {
            repNode.x = 3 * stepX
            repNode.y = pumNode.y
            addNode(repNode, 'rep', repNode.status != '1')
            addLink(pumNode, repNode, getStatusColor(pumNode.status))
          }
        }
        extHeight += stepY
      })
      pumHeight = extHeight
      pumProcess.forEach(t1 => {
        if (!('y' in t1)) {
          t1.y = pumHeight
          t1.x = 2 * stepX
          addNode(t1, 'pum', t1.status != '1')
        }
      })
      extHeight = pumHeight = Math.max(pumHeight, extHeight)
      if (t.pubSubGroup) {
        subHeight = start
        Object.keys(t.pubSubGroup).forEach(p1 => {
          let item = {
            name: p1,
            type: 'pubServer',
          }
          item.x = -2 * stepX
          item.y = Math.max(
            subHeight,
            subHeight + ((t.pubSubGroup[p1].length - 1) * stepY) / 2,
          )
          addNode(item, 'pubServer')
          t.pubSubGroup[p1].forEach(t1 => {
            t1.x = -1 * stepX
            t1.y = subHeight
            t1.name = t1.publication
            subHeight += stepY
            addNode(t1, 'sub')
            addLink(t1, t, getStatusColor(t1.status))
            addLink(item, t1, 1)
            addMoveLink(t1, t)
            addMoveLink(item, t1)
          })
        })
      }
    })
    repHeight = extHeight
    let repSize = 0
    let repStep = 0
    target.forEach(t => {
      repSize += t.process.length
    })
    repStep = (repSize * stepY) / target.length
    target.forEach((t, index) => {
      t.x = 4 * stepX
      t.y = repStep / 2 + index * repStep
      addNode(t, 'server')
      t.process.forEach(t1 => {
        if (!('y' in t1)) {
          t1.y = repHeight
          t1.x = 3 * stepX
          addNode(t1, 'rep', t1.status != '1')
          repHeight += stepY
        }
        addLink(t1, t, getStatusColor(t1.status))
      })
    })
  }

  const formatTooltip = node => {
    if (node.type === 'server') {
      return (
        '{b}：' +
        node.ip +
        '<br />' +
        'ogg路径：' +
        node.oggPath +
        '<br />' +
        '操作系统：' +
        (node.os == '01' ? 'Windows' : 'Linux') +
        '<br />' +
        '进程数量：' +
        node.process.length
      )
    }
    if (node.type === 'sub') {
      return (
        '发布订阅：' +
        node.publication +
        '<br />' +
        '状态：' +
        (node.realStatus == '4'
          ? '空闲'
          : node.realStatus == '3'
            ? '运行中'
            : node.realStatus == '2'
              ? '已停止'
              : '异常') +
        '<br />' +
        '上次代理运行：' +
        node['last_distsync'] +
        '<br />' +
        '订阅服务器：' +
        node['subscriber'] +
        '<br />' +
        '订阅数据库：' +
        node['subscriber_db'] +
        '<br />' +
        '发布服务器：' +
        node['publisher'] +
        '<br />' +
        '发布数据库：' +
        node['publisher_db']
      )
    }
    if (node.type === 'pubServer') {
      return '发布服务器：' + node.name
    } else {
      return (
        getType(node.type) +
        '进程：{b} <br/>' +
        '状态：' +
        (node.status == '1'
          ? '运行中'
          : node.status == '0'
            ? '已停止'
            : '异常') +
        '<br />' +
        '延迟：' +
        node.lagAtChkpt +
        '<br/>检查点：' +
        (node.timeSinceChkpt ?? '未知')
      )
    }
  }

  const addNode = (node, type, error) => {
    data.push({
      name: node.name,
      type: type,
      node: node,
      tooltip: {
        formatter: formatTooltip(node),
      },

      symbol: type == 'server' || type == 'pubServer' ? ( error? symbol[4]:symbol[3]): ( error? symbol[0]:symbol[1]),
      symbolSize: [60, 40],
      value: [node.x, node.y],
      x: node.x,
      y: node.y,
      fixed: true,
      detail: node.details,
      // draggable: false,
      category: 1,
      label: {
        color: '#FFF',
        position: 'bottom',
      },
      itemStyle: {
        normal: {
          color: getStatusColor(node.status),
        },
      },
    })
  }

  const addLink = (source, target, color) => {
    const link = {
      source: source.name,
      target: target.name,
      category: source.speed ?? target.speed,
    }
    if (color) {
      link.lineStyle = {
        color: color,
      }
    }
    if (source.type == 'server' || source.status == '1') {
      addMoveLink(source, target)
    }
    linkData.push(link)
  }

  const addMoveLink = (source, target) => {
    moveLinkData.push([
      [source.x, source.y],
      [target.x, target.y],
    ])
  }

  buildData(source, target)
  const content = document.getElementById('chart-container')
  content.style.height = Math.max( refHeight.value ,repHeight, pumHeight, subHeight) - 80  + 'px'
  height = document.getElementById('chart-panel').clientHeight
  const option = {
    grid: {
      show: false,
      left: '10%',
      right: '0%',
    },
    backgroundColor: '#282a36',
    xAxis: {
      show: false,
      type: 'value',
      max: width,
    },
    yAxis: {
      show: false,
      type: 'value',
      max: height,
    },
    tooltip: {
      backgroundColor: '#21232d',
      textStyle: {
        color: 'white',
      },
      extraCssText: 'box-shadow: 0 0 3px rgba(0, 0, 0, 0.3);',
    },
    series: [
      {
        type: 'graph',
        zlevel: 5,
        roam: true,
        draggable: false,
        coordinateSystem: 'cartesian2d', //使用二维的直角坐标系（也称笛卡尔坐标系）
        edgeLabel: {
          // 连接两个关系对象的线上的标签
          normal: {
            show: false,
            textStyle: {
              fontSize: 12,
            },
            formatter: function (param) {
              // 标签内容
              return param.data.category
            },
          },
          emphasis: {
            show: true,
            textStyle: {
              fontSize: 12,
            },
            formatter: function (param) {
              // 标签内容
              return param.data.category
            },
          },
        },
        symbol: 'rect',
        symbolOffset: ['15%', 0],
        label: {
          normal: {
            show: true,
          },
        },
        data: data,
        links: linkData,
        lineStyle: {
          normal: {
            opacity: 1,
            color: '#53B5EA',
            curveness: 0,
            width: 2,
          },
          emphasis: {
            width: 4,
          },
        },
      },
      {
        type: 'lines',
        coordinateSystem: 'cartesian2d',
        z: 1,
        zlevel: 2,
        animation: false,
        effect: {
          show: true,
          period: 5,
          trailLength: 0.01,
          symbolSize: 12,
          symbol: 'pin',
          loop: true,
          color: 'rgba(55,155,255,0.5)',
        },
        lineStyle: {
          normal: {
            color: '#22AC38',
            width: 0,
            curveness: 0,
          },
        },
        data: moveLinkData,
      },
    ],
  }
  setOptions(option)
}

const showServer = ref(false)
const showDetails = ref(false)

onMounted(() => {
  init()
  chartInstance.value.on('click', function (params) {
    if (params.data?.detail) {
      process.value.name = params.name
      process.value.details = params.data.detail
      showDetails.value = true
    }
    if (params.data?.type == 'server') {
      server.value = params.data.node
      // showServer.value = true
    }
  })
  fetchData()
})
</script>

<style scoped lang="scss"></style>
