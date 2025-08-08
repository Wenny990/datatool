<script setup>
import { onClickOutside } from '@vueuse/core'

const { modelValue= '', } = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
})

const modelValueTemp = ref('')

const isEdit = ref(false)

const emit = defineEmits(['update:modelValue'])

const inputRef = useTemplateRef('inputRef');
const focusInput = () => {
  modelValueTemp.value = modelValue
  isEdit.value = true
  nextTick(() => {
    inputRef.value.focus()
  })
}

const handleSave = () => {
  isEdit.value = false
  emit('update:modelValue', modelValueTemp.value)
}

watch(
  () => modelValue,
  val => {
    modelValueTemp.value = val
  },
  {
    immediate: true,
  },
)

onClickOutside(inputRef, () => isEdit.value = false)
</script>

<template>
  <div @dblclick="focusInput" class="pr20 pl2">
    <span v-if="!isEdit">{{ modelValue }}</span>
    <el-input v-else v-model="modelValueTemp"  @keyup.enter="handleSave" ref="inputRef">
      <template #append>
        <el-button @click="handleSave" type="success" link>保存</el-button>
      </template>
    </el-input>
  </div>
</template>

<style scoped lang="scss"></style>
