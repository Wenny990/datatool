<script setup>
import useDbMeta from '@/utils/useDbMeta'
import { reactive } from 'vue'
import { useMigrationStore } from '@/stores/migration'

const { sourceState, targetState, repoList } = storeToRefs(useMigrationStore())
</script>

<template>
  <el-form size="small">
    <el-row :gutter="60">
      <el-col :span="12">
        <el-form-item label="源端数据源">
          <el-select
            v-model="sourceState.currentRepo"
            filterable
            value-key="id"
            style="width: 240px"
            placeholder="选择数据源"
          >
            <el-option
              v-for="item in repoList"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span
                style="
                  float: right;
                  color: var(--el-text-color-secondary);
                  font-size: 13px;
                "
              >
                {{ item.host + ' (' + item.dataSourceName + ')' }}
              </span>
            </el-option>
          </el-select>
          <el-select
            v-model="sourceState.currentSchema"
            style="width: 180px"
            placeholder="选择数据库"
          >
            <el-option
              v-for="item in sourceState.schemaList"
              :label="item.schemaName"
              :value="item.schemaName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="目标端数据源">
          <el-select
            v-model="targetState.currentRepo"
            filterable
            value-key="id"
            style="width: 240px"
            placeholder="选择数据源"
          >
            <el-option
              v-for="item in repoList"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span
                style="
                  float: right;
                  color: var(--el-text-color-secondary);
                  font-size: 13px;
                "
              >
                {{ item.host + ' (' + item.dataSourceName + ')' }}
              </span>
            </el-option>
          </el-select>
          <el-select
            v-model="targetState.currentSchema"
            style="width: 180px"
            placeholder="选择数据库"
          >
            <el-option
              v-for="item in targetState.schemaList"
              :label="item.schemaName"
              :value="item.schemaName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped lang="scss"></style>
