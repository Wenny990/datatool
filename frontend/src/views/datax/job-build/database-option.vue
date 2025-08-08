<script setup>
import apis from '@/service/apis'
import { useRequest } from 'alova/client'
import { onMounted } from 'vue'

const { data: repoList } = useRequest(apis.getRepo())

const { data = {} } = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const { data: schemaList, send: getSchemasSend } = useRequest(
  data => apis.getSchemas(data),
  {
    immediate: false,
  },
)

const onRepoChange = val => {
  data.schemaName = ''
  data.tableName = ''
  getSchemasSend({ repositoryId: val })
}

const { data: tableList, send: getTablesSend } = useRequest(
  data => apis.getTables(data),
  {
    immediate: false,
  },
)

const onSchemaChange = val => {
  data.tableName = ''
  getTablesSend({
    repositoryId: data.repoId,
    schemaName: val,
  })
}

const { data: columnList, send: getColumnsSend } = useRequest(
  data => apis.getColumns(data),
  {
    immediate: false,
  },
)

function isUpperCaseContainsNumberOrInt(str) {
  const upperCaseStr = str.toUpperCase()
  return upperCaseStr.includes('NUMBER') || upperCaseStr.includes('INT')
}

const onTableChange = val => {
  getColumnsSend({
    repositoryId: data.repoId,
    schemaName: data.schemaName,
    tableName: val,
  }).then(respData => {
    data.columnList = data.columns = respData.map(t => t.columnName)
    const primaryKeyList = respData
      .filter(t => t.isPrimaryKey && isUpperCaseContainsNumberOrInt(t.datatype))
      .map(t => t.columnName)
    if (primaryKeyList.length >= 1) {
      data.splitPk = primaryKeyList[0]
    } else {
      data.splitPk = ''
    }
  })
}

onMounted(() => {
  if (data.repoId) {
    getSchemasSend({ repositoryId: data.repoId })
    if (data.schemaName) {
      getTablesSend({
        repositoryId: data.repoId,
        schemaName: data.schemaName,
      })
      if (data.tableName) {
        getColumnsSend({
          repositoryId: data.repoId,
          schemaName: data.schemaName,
          tableName: data.tableName,
        })
      }
    }
  }
})
</script>

<template>
  <div>
    <el-form-item label="数据连接" prop="repoId">
      <el-select
        v-model="data.repoId"
        filterable
        style="width: 300px"
        @change="onRepoChange"
      >
        <el-option
          v-for="item in repoList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="数据库" prop="schemaName">
      <el-select
        v-model="data.schemaName"
        filterable
        style="width: 300px"
        @change="onSchemaChange"
      >
        <el-option
          v-for="item in schemaList"
          :key="item.schemaName"
          :label="item.schemaName"
          :value="item.schemaName"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="数据库表名" prop="tableName">
      <el-select
        v-model="data.tableName"
        filterable
        style="width: 300px"
        @change="onTableChange"
      >
        <el-option
          v-for="item in tableList"
          :key="item.tableName"
          :label="item.tableName"
          :value="item.tableName"
        />
      </el-select>
    </el-form-item>
  </div>
</template>

<style scoped lang="scss"></style>
