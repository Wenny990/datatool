<template>
	<div ref="chartRef" style="width: 100%;height: 100%"></div>
</template>

<script setup>
import * as echarts from "echarts";
import './echart-theme.js'
import {useThemeConfig} from "/@/stores/themeConfig";
import elementResizeDetectorMaker from "element-resize-detector";
import {debounce} from "lodash-es";


const props = defineProps({
	options: {
		type: Object,
		default: () => {}
	}
})

const chartRef = ref()

const storesThemeConfig = useThemeConfig();

const chart = { chartInstance : null}

const initChart = () => {
	if(chart.chartInstance && chart.chartInstance.dispose){
		chart.chartInstance.dispose()
	}
	chart.chartInstance = echarts.init(chartRef.value,
		storesThemeConfig.themeConfig.isIsDark? 'chalk': '',
		{
			darkMode: storesThemeConfig.themeConfig.isIsDark
		}
	)
	update();
}

const update = () => {
	chart.chartInstance.setOption(props.options)
}

watch(() => storesThemeConfig.themeConfig.isIsDark, () => {
	initChart();
})

watch(() => props.options, () => {
	update();
},{
	deep: true
})

const onResize = debounce(() => {
	if(chart.chartInstance && chart.chartInstance.resize){
		chart.chartInstance.resize()
	}
})

const erd = elementResizeDetectorMaker(); //创建实例

onMounted(() => {
	initChart()
	//监听id为box的元素
	erd.listenTo(chartRef.value, (element) => {
		onResize()
	});
});

onBeforeUnmount(() => {
	erd.uninstall(chartRef.value); //这里用ref是因为vue离开页面后获取不到dom
	if(chart.chartInstance && chart.chartInstance.dispose){
		try {
			chart.chartInstance.dispose()
		}catch (e) {

		}
	}
});

defineExpose({
	chart
})
</script>

