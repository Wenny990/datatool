import { onUnmounted, ref, shallowRef, toValue } from 'vue'
import * as echarts from 'echarts'
import { useDark, useResizeObserver } from '@vueuse/core'
import { debounce } from 'lodash-es'

/**
 * 创建一个echarts实例，可以自动销毁
 * @param chartContainerRef 图表挂载的实例元素
 * @returns
 */
export default function useChart(chartContainerRef) {
  const chartInstance = shallowRef()
  const isInit = ref(false)

  const isDark = useDark()


  /**
   * 图表初始化，在组件挂载时调用
   */
  const init = () => {
    const chartContainer = toValue(chartContainerRef)
    if (chartContainer) {
      chartInstance.value = echarts.init(
        chartContainer,
        isDark.value ? 'dark' : '',
      )
      isInit.value = true
    }
  }

  /**
   * 设置图表配置项
   * @param options 配置项
   */
  const setOptions = options => {
    if (chartInstance.value && chartInstance.value.setOption) {
      chartInstance.value.setOption(options)
    }
  }

  useResizeObserver(
    chartContainerRef,
    debounce(() => {
      if (isInit.value && chartInstance.value && chartInstance.value.resize) {
        chartInstance.value.resize({
          animation: {
            duration: 500,
          },
        })
      }
    }, 500),
  )

  onUnmounted(() => {
    if (chartInstance.value && chartInstance.value.dispose)
      chartInstance.value.dispose()
  })

  return {
    chartInstance,
    setOptions,
    init,
    isInit,
  }
}
