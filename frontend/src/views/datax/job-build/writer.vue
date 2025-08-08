<template>
  <div class="writer-container h100 flex-center">
    <div class="title">
      <svg-icon name="iconfont icon-dian" color="var(--el-color-primary)"></svg-icon>
      <el-text style="font-weight: bold">目标端</el-text>
    </div>
    <div class="flex-auto">
      <el-scrollbar height="100%" class="form-container">
        <el-form ref="formRef" label-position="right" label-width="120px" :model="ruleForm" :rules="rules">
          <database-option :data="ruleForm"></database-option>
          <el-form-item label="清空表" prop="truncateTable">
            <el-checkbox v-model="ruleForm.truncateTable"/>
          </el-form-item>

          <el-form-item label="字段" prop="columns">
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

          <el-form-item label="批次提交条数" prop="batchSize">
            <el-input-number v-model="ruleForm.batchSize"/>
          </el-form-item>
          <el-form-item label="前置Sql">
            <code-editor v-model="ruleForm.preSql" placeholder="前置sql在insert之前执行" type="sql" :disabled="false"
                         height="120px"></code-editor>
          </el-form-item>
          <el-form-item label="后置Sql">
            <code-editor v-model="ruleForm.postSql" placeholder="后置Sql在插入完成后执行，多个用;分隔" type="sql"
                         :disabled="false" height="120px"></code-editor>
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>

  </div>
</template>

<script setup>
import CodeEditor from "@/components/codeEditor/index.vue";
import DatabaseOption from '@/views/datax/job-build/database-option.vue'


const props = defineProps({
  writer: {
    type: Object,
    required: true,
    default: () => {
    }
  },
  repoList: {
    type: Array,
    required: true,
    default: () => []
  }
})

const {writer: ruleForm, repoList} = toRefs(props);

const rules = {
  repoId: [{required: true, message: '请选择数据连接'}],
  schemaName: [{required: true, message: '数据库不能为空'}],
  tableName: [{required: true, message: '表不能为空'}],
  columns: [{required: true, message: '字段不能为空'}]
}

const onCheckAllChange = (val) => {
  if (val) {
    ruleForm.value.columns = ruleForm.value.columnList;
  }
}

const formRef = ref()

defineExpose({
  formRef
})
</script>

<style lang="scss" scoped>
$border: 1px solid var(--el-border-color);

.writer-container {
  border: $border;


  .form-container {
    padding: 12px;
  }
}


.title {
  height: 24px;
  line-height: 24px;
  padding: 12px;
}
</style>
