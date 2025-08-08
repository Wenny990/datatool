<script setup name="DbMigration">
import SvgIcon from '@/components/svgIcon/index.vue'
import MyInput from '@/components/my-input/my-input.vue'
import { useMigrationStore } from '@/stores/migration'
import MapperConfig from '@/views/migration/mapper-config.vue'
import ColumnEdit from '@/views/migration/column-edit.vue'
import { ElMessage } from 'element-plus'
import CodeEditor from '@/components/codeEditor/index.vue'
import { Pane, Splitpanes } from 'splitpanes'
import DbChoose from '@/views/migration/db-choose.vue'

const {
  repoList,
  mappingDialog,
  currEditColumn,
  columnEditDialog,
  targetRepoConfig,
  mappingListData,
  currTable,
  transferModel,
  ddlSqlText,
} = storeToRefs(useMigrationStore())

const { sourceState, targetState } = storeToRefs(useMigrationStore())

const handleExecute = () => {
  const checkedList = sourceTableList.value.filter(t => t.isChecked)
  executeSql(checkedList, 0)
}

const tableContainerRef = useTemplateRef('tableContainerRef')

const executeSql = (checkedList, currIndex) => {
  if (currIndex > checkedList.length - 1) {
    return
  }
  tableContainerRef.value.scrollTo({
    top: currIndex * 20,
    behavior: 'smooth',
  })
  const item = checkedList[currIndex]
  currTable.value = item
  setTimeout(() => {
    currIndex++
    currTable.value.isChecked = false
    executeSql(checkedList, currIndex)
  }, 3000)
}

const handleCheckAll = isCheck => {
  sourceState.value.tableList.forEach(t => (t.isChecked = isCheck))
}

const handleColumnDbClick = item => {
  columnEditDialog.value = true
  currEditColumn.value = item
  console.log(item)
}
</script>

<template>
  <div class="h100 flex-center ogg-container pd10 border">
    <db-choose></db-choose>
    <div>
      <el-button-group size="small">
        <el-button>刷新</el-button>
        <el-button @click="handleCheckAll(true)">全选</el-button>
        <el-button @click="handleCheckAll(false)">取消全选</el-button>
        <el-button @click="mappingDialog = true">映射 </el-button>
        <el-button type="success" @click="handleExecute">执行</el-button>
      </el-button-group>

      <el-select v-model="transferModel" style="width: 100px" size="small">
        <el-option value="unChange" label="不转换"></el-option>
        <el-option value="upper" label="全大写"></el-option>
        <el-option value="lower" label="全小写"></el-option>
      </el-select>
    </div>
    <div class="flex-auto">
      <Splitpanes class="default-theme h100">
        <Pane :size="35" class="flex-center">
          <div class="flex-auto list-container flex-center border">
            <div class="flex gap6">
              <div
                class="flex-auto list-item-head head-left flex align-center gap4 px4"
                v-show="sourceState.currentRepo && sourceState.currentSchema"
              >
                <svg-icon
                  :name="`/icon/${sourceState.dataSourceName}.svg`"
                  :size="16"
                  color="red"
                ></svg-icon>
                <span>{{ sourceState.currentSchema }}</span>
              </div>
              <div
                class="flex-auto list-item-head head-right flex align-center gap4 px4"
                v-show="targetState.currentRepo && targetState.currentSchema"
              >
                <svg-icon
                  :name="`/icon/${targetState.dataSourceName}.svg`"
                  :size="16"
                  color="red"
                ></svg-icon>
                <span> {{ targetState.currentSchema }}</span>
              </div>
            </div>
            <div
              ref="tableContainerRef"
              class="flex-auto"
              style="overflow: auto"
              v-show="sourceState.currentSchema && targetState.currentSchema"
            >
              <div
                v-for="item in sourceState.tableList"
                :key="item.tableName"
                @click="currTable = item"
                class="flex gap6 flex-auto"
              >
                <div
                  class="flex-auto list-item flex align-center gap4 px4"
                  :class="{ 'is-curr': currTable === item }"
                >
                  <el-checkbox v-model="item.isChecked"></el-checkbox>
                  <svg-icon
                    :name="`/icon/TABLE${!item.isChecked ? '_DISABLE' : ''}.svg`"
                    :size="16"
                  ></svg-icon>
                  <span>{{ item.tableName }}</span>
                </div>
                <div
                  class="flex-auto list-item flex align-center gap4 px4"
                  :class="{ 'is-curr-right': currTable === item }"
                >
                  <svg-icon
                    :name="`/icon/TABLE${!item.isChecked ? '_DISABLE' : ''}.svg`"
                    :size="16"
                  ></svg-icon>
                  <my-input v-model="item.targetTableName" />
                </div>
              </div>
            </div>
          </div>
        </Pane>
        <Pane :size="40" class="flex-center">
          <div class="flex-auto column-container flex-center border">
            <div class="flex gap6">
              <div
                class="flex-auto list-item-head head-left flex align-center gap4 px4"
                v-show="sourceState.currentRepo && sourceState.currentSchema"
              >
                <svg-icon
                  :name="`/icon/${sourceState.dataSourceName}.svg`"
                  :size="16"
                ></svg-icon>
                <span>{{ currTable.tableName }}</span>
              </div>
              <div
                class="flex-auto list-item-head head-right flex align-center gap4 px4"
                v-show="targetState.currentRepo && targetState.currentSchema"
              >
                <svg-icon
                  :name="`/icon/${targetState.dataSourceName}.svg`"
                  :size="16"
                ></svg-icon>
                <span> {{ currTable.targetTableName }}</span>
              </div>
            </div>
            <div
              class="flex-auto"
              style="overflow: auto"
              v-if="currTable.isChecked && currTable.columns"
            >
              <div
                v-for="item in currTable.columns"
                :key="item.columnName"
                class="flex gap6 flex-auto"
                @dblclick="handleColumnDbClick(item)"
                @click="currEditColumn = item"
              >
                <div
                  class="flex-auto list-item flex align-center gap4 px4"
                  :class="{ 'is-curr': currEditColumn === item }"
                >
                  <el-checkbox v-model="item.isChecked"></el-checkbox>
                  <svg-icon
                    :name="`/icon/${item.isPrimaryKey ? 'column_pk' : 'column'}.svg`"
                    :size="16"
                  ></svg-icon>
                  <span>{{ item.columnName }}</span>
                  <span class="desc">{{
                    ' (' +
                    (item.isPrimaryKey ? 'primary key,' : '') +
                    item.dataType +
                    (item.dataLength ? '(' + item.dataLength + ')' : '') +
                    ',' +
                    (!item.nullable ? 'not null' : 'null') +
                    ')'
                  }}</span>
                </div>
                <div
                  class="flex-auto list-item flex align-center gap4 px4"
                  :class="{ 'is-curr-right': currEditColumn === item }"
                >
                  <svg-icon
                    :name="`/icon/${item.isPrimaryKey ? 'column_pk' : 'column'}.svg`"
                    :size="16"
                  ></svg-icon>
                  <span>{{ item.targetColumnName }}</span>
                  <span class="desc">
                    {{
                      ' (' +
                      (item.targetIsPrimaryKey ? 'primary key,' : '') +
                      item.targetDataType +
                      (item.targetDataLength
                        ? '(' + item.targetDataLength + ')'
                        : '') +
                      ',' +
                      (!item.targetNullable ? 'not null' : 'null') +
                      ')'
                    }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </Pane>
        <Pane :size="25" class="flex-center">
          <div class="h100">
            <code-editor
              placeholder="此处生成DDL"
              v-model="ddlSqlText"
              type="sql"
              :disabled="true"
              :font-size="12"
            ></code-editor>
          </div>
        </Pane>
      </Splitpanes>
    </div>
    <div></div>
    <column-edit />
    <mapper-config />
  </div>
</template>

<style scoped lang="scss">
.desc {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: nowrap;
}

.list-item {
  height: 24px;
  font-size: 12px;
}

.list-item.is-curr {
  background-image: linear-gradient(
    to right,
    var(--el-color-primary-light-7),
    #fff
  );
}

.list-item.is-curr-right {
  background-image: linear-gradient(
    to right,
    var(--el-color-success-light-7),
    #fff
  );
}

.list-item-head {
  height: 28px;
  font-size: 13px;
}

.list-item-head.head-left {
  background-image: linear-gradient(
    to right,
    var(--el-color-primary-light-3),
    #fff
  );
}

.list-item-head.head-right {
  background-image: linear-gradient(
    to right,
    var(--el-color-success-light-3),
    #fff
  );
}

.list-container {
  background-color: #fffff0;
}

.column-container {
  background-color: var(--el-bg-color);
}

html.dark .list-container {
  background-color: #282a36;
}
</style>
