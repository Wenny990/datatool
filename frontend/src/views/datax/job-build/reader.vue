<template>
  <div class="reader-container h100 flex-center">
    <div class="title">
      <svg-icon name="iconfont icon-dian" color="var(--el-color-primary)"></svg-icon>
      <el-text style="font-weight: bold">源端</el-text>
    </div>
    <div class="flex-auto">
      <el-scrollbar height="100%" class="form-container">
        <el-form ref="formRef" label-position="right" label-width="120px" :model="ruleForm" :rules="rules">
          <el-form-item label="模式">
            <el-radio-group v-model="ruleForm.type">
              <el-radio-button :value="0">数据表</el-radio-button>
              <el-radio-button :value="1">查询语句</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <database-option :data="ruleForm" v-if="ruleForm.type===0"></database-option>
          <el-form-item label="数据连接" prop="repoId" v-if="ruleForm.type==1">
            <el-select v-model="ruleForm.repoId" filterable style="width: 300px">
              <el-option
                v-for="item in repoList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="查询SQL" prop="querySql" v-if="ruleForm.type==1">
            <code-editor v-model="ruleForm.querySql" type="sql" :disabled="false" height="240px"/>
          </el-form-item>
          <el-form-item label="切分字段" v-if="ruleForm.type===0">
            <el-input v-model="ruleForm.splitPk" style="width: 300px" placeholder="多通道执行时用来切分的字段，一般使用主键"/>
          </el-form-item>
          <el-form-item label="字段" v-if="ruleForm.type===0">
            <el-checkbox
                v-model="ruleForm.checkAll"
                @change="onCheckAllChange"
            >全选
            </el-checkbox>
            <div style="margin: 15px 0;"/>
            <el-checkbox-group v-model="ruleForm.columns" v-if="!ruleForm.checkAll">
              <el-checkbox v-for="columnName in ruleForm.columnList" :key="columnName" :value="columnName">{{
                  columnName
                }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="单次获取条数" prop="fetchSize" v-if="ruleForm.type===0">
            <el-input-number v-model="ruleForm.fetchSize"/>
          </el-form-item>
          <el-form-item label="where条件：" prop="where" v-if="ruleForm.type===0">
            <code-editor v-model="ruleForm.where" placeholder="过滤表数据" type="sql" :disabled="false"
                         height="120px"></code-editor>
          </el-form-item>

        </el-form>
      </el-scrollbar>
      </div>
  </div>
</template>

<script setup>

import {onMounted, ref} from "vue";
import CodeEditor from "@/components/codeEditor/index.vue";
import apis from '@/service/apis'
import DatabaseOption from '@/views/datax/job-build/database-option.vue'

const formRef = ref()
const rules = {
  repoId: [{required: true, message: '请选择数据连接'}],
  schemaName: [{required: true, message: '数据库不能为空'}],
  tableName: [{required: true, message: '表不能为空'}],
  querySql: [{required: true, message: '查询语句不能为空'}]
}

const props = defineProps({
  reader: {
    type: Object,
    required: true,
    default: ()=>{}
  },
  repoList:{
    type: Array,
    required: true,
    default: ()=> []
  }
})

const {reader: ruleForm,repoList} = toRefs(props);



const onCheckAllChange = (val) => {
  if (val) {
    ruleForm.value.columns = ruleForm.value.columnList;
  }
}

defineExpose({
  formRef
})
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
