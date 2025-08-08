<template>
  <div class="h100 w100">
    <el-tabs
        v-model="currComponent"
        type="card"
        class="h100 flex-center"

        @tab-remove="removeTab"
        v-if="componentList.length > 0"
    >
      <el-tab-pane
          v-for="(item,index) in componentList"
          :key="item.key"
          :label="item.title"
          :name="item.key"
          class="h100"
          :closable="item.allowClose??true"
      >
        <template #label>
          <div @contextmenu.prevent.stop="onRightClick($event,index)">
            {{ item.title }}
          </div>
        </template>
        <slot name="item" v-bind="item">
          {{item.title}}
        </slot>
      </el-tab-pane>
    </el-tabs>
    <el-empty  description="这里什么也没有" v-else/>
  </div>
</template>

<script setup name="ElTabPro">

import {computed, toRefs} from "vue";
import {showRightMenu} from "@/components/right-context-menu";

const props = defineProps({
  componentList: {
    type: Array,
    default: () => []
  },
  modelValue:{
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue']);

const {componentList} = toRefs(props);

const currComponent = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value)
  }
})

const menuList = [{
  name: 'allClose',
  label: '全部关闭'
}, {
  name: 'closeRight',
  label: '关闭右侧'
}]

const onRightClick = (event, index) => {
  showRightMenu({x: event.clientX, y: event.clientY}, menuList, (name) => {
    if (name === 'allClose') {
      removeAllTab();
    }
    if (name === 'closeRight') {
      removeRightTab(index);
    }
  });
}
const removeTab = (targetName) => {
  const index = componentList.value.findIndex(item => item.key === targetName);
  const nextTab = componentList.value[index + 1] || componentList.value[index - 1]
  if (nextTab) {
    currComponent.value = nextTab.key
  }
  componentList.value.splice(index, 1)
}

const removeAllTab = () => {
  currComponent.value = componentList.value[0].key
  componentList.value.splice(1,componentList.value.length);
}

const removeRightTab = (index) => {
  let currIndex = componentList.value.findIndex(t => t.key === currComponent.value)
  if (currIndex > index) {
    componentList.value.findIndex(t => t.key === currComponent.value)
    currComponent.value = componentList.value[index].key;
  }
  componentList.value.splice(index + 1)
}

</script>

<style scoped>

</style>
