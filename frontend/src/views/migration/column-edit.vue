<script setup>
import { useMigrationStore } from '@/stores/migration'
import { cloneDeep } from 'lodash-es'

const { currEditColumn, columnEditDialog, targetColumnTypeList } = storeToRefs(useMigrationStore())

const formData = ref({})

const handleSubmit = () => {
  currEditColumn.value = {
    ...currEditColumn.value,
    ...formData.value,
  }
  columnEditDialog.value = false
}

watch(
  () => currEditColumn.value,
  newVal => {
    formData.value = cloneDeep(newVal)
  },
)
</script>

<template>
  <div>
    <el-dialog v-model="columnEditDialog" title="字段编辑" width="30%" >
      <div class="pd20">
        <el-form :model="formData" label-position="right" label-width="80px">
          <el-form-item label="字段名称" required prop="targetColumnName">
            <el-input v-model="formData.targetColumnName"></el-input>
          </el-form-item>
          <el-form-item label="类型" required prop="targetDataType">
            <el-select v-model="formData.targetDataType"
                       filterable
                       default-first-option
                       allow-create >
              <el-option v-for="item in targetColumnTypeList" :key="item.columnType" :value="item.columnType" :label="item.columnType"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="默认值" prop="targetDefaultValue">
            <el-input v-model="formData.targetDefaultValue"></el-input>
          </el-form-item>
          <el-form-item label="注释">
            <el-input type="textarea" v-model="formData.targetRemarks" :rows="6"></el-input>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="formData.targetIsPrimaryKey" >主键</el-checkbox>
            <el-checkbox v-model="formData.targetNullable" >允许为空</el-checkbox>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="columnEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss"></style>
