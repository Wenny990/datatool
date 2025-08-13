<template>
  <div class="reader-container h100 flex-center">
    <div class="title">
      <svg-icon
        name="iconfont icon-dian"
        color="var(--el-color-primary)"
      ></svg-icon>
      <el-text style="font-weight: bold">转换</el-text>
    </div>
    <div class="flex-auto flex gap10 pd10">
      <div class="flex-auto h100 border">
        <el-scrollbar height="100%" class="form-container">
          <el-button @click="addTransformer" type="primary" plain class="mg20">
            <el-icon><Plus /></el-icon> 添加转换
          </el-button>
          <el-empty v-if="list.length === 0" description="暂无转换"></el-empty>
          <el-form
            ref="formRef"
            label-position="right"
            label-width="120px"
            class="mg20"
          >
            <div
              class="mb20 flex gap16"
              v-for="(item, index) in list"
              :key="item.index"
            >
              <el-text> {{ '转换' + (index + 1) }}</el-text>
              <el-select v-model="item.name" style="width: 240px">
                <el-option
                  v-for="item in transformerTypeList"
                  :key="item.name"
                  :value="item.name"
                  :label="item.label"
                ></el-option>
              </el-select>
              <div>
                <el-button
                  @click="handleEditParameter(item)"
                  text
                  type="primary"
                  >配置</el-button
                >
                <el-button @click="list.splice(index, 1)" text type="danger"
                  >删除</el-button
                >
              </div>
            </div>
          </el-form>
        </el-scrollbar>
      </div>
      <div class="flex-auto h100 border">
        <div class="title mb24" v-if="editingItem">
          <svg-icon
            name="iconfont icon-dian"
            color="var(--el-color-primary)"
          ></svg-icon>
          <el-text style="font-weight: bold">{{ editingItem.name }}</el-text>
        </div>
        <el-scrollbar
          height="100%"
          class="form-container pd16"
          v-if="editingItem"
        >
          <div class="flex-center pd20">
            <el-empty v-if="Object.keys(parameter).length === 0" description="暂无参数配置"></el-empty>
            <el-row
              v-for="(key, index) in Object.keys(parameter)"
              :key="key"
              :gutter="10"
            >
              <el-col :span="6">
                <el-form-item label="参数名">
                  <el-text>{{ key }}</el-text>
                </el-form-item>
              </el-col>
              <el-col :span="16">
                <el-form-item label="参数值">
                  <el-input
                    type="textarea"
                    v-model="parameter[key]"
                    placeholder="请输入参数值"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <el-button @click="removeParameter(key)" type="danger" plain>
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-col>
            </el-row>
            <el-row v-show="isEdit" :gutter="10">
              <el-col :span="6">
                <el-form-item label="参数名">
                  <el-input
                    v-model="temEditItem.key"
                    @blur="updateParameterKey(temEditItem.key)"
                    placeholder="请输入参数名"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="16">
                <el-form-item label="参数值">
                  <el-input
                    type="textarea"
                    v-model="temEditItem.value"
                    placeholder="请输入参数值"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <el-button @click="isEdit= false" type="danger" plain>
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-col>
            </el-row>
            <div style="margin-top: 20px">
              <el-button @click="addParameter" type="primary" plain>
                <el-icon><Plus /></el-icon> 添加参数
              </el-button>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, toRefs } from 'vue'
import { Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const formRef = ref()

const props = defineProps({
  transformer: {
    type: Array,
    required: true,
    default: () => [],
  },
})

const { transformer: list } = toRefs(props)

// const list = ref([])

const transformerTypeList = [
  {
    name: 'dx_groovy',
    label: '自定义转换',
  },
  {
    name: 'dx_replace',
    label: '字符串替换',
  },
  {
    name: 'dx_substr',
    label: '截取字符串',
  },
  {
    name: 'dx_trim',
    label: '去除首尾空格',
  },
  {
    name: 'dx_upper',
    label: '转大写',
  },
  {
    name: 'dx_lower',
    label: '转小写',
  },
  {
    name: 'dx_filter',
    label: '按条件过滤记录',
  },
]

// 添加新的转换项
const addTransformer = () => {
  list.value.push({
    name: '',
    parameter: {},
  })
}

const dialogVisible = ref(false)
const parameter = ref({})
const editingItem = ref(null)

const handleEditParameter = item => {
  editingItem.value = item
  parameter.value = item.parameter || {}
  dialogVisible.value = true
}

const isEdit = ref(false)
const temEditItem = ref({})
// 更新参数键名
const updateParameterKey = newKey => {
  if (!newKey) {
    return
  }

  // 检查新键名是否已存在
  if (parameter.value[newKey] !== undefined) {
    ElMessage.warning('参数名已存在')
    return
  }

  // 更新键名
  parameter.value[newKey] = temEditItem.value.value
  isEdit.value = false
  temEditItem.value = {}
}

// 添加参数
const addParameter = () => {
  let newKey = 'parameter1'
  let counter = 1

  // 确保生成唯一的参数名
  while (parameter.value[newKey] !== undefined) {
    counter++
    newKey = `parameter${counter}`
  }

  isEdit.value = true
  temEditItem.value.key = newKey
}

// 删除参数
const removeParameter = key => {
  delete parameter.value[key]
}


</script>

<style lang="scss" scoped>
$border: 1px solid var(--el-border-color);

.reader-container {
  border: $border;

  .form-container {
    padding: 12px;
  }
}

.title {
  padding: 12px;
  height: 24px;
  line-height: 24px;
}
</style>
