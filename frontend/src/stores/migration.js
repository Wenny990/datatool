import { computed, reactive, ref } from 'vue'
import { defineStore } from 'pinia'
import { useRequest } from 'alova/client'
import apis from '@/service/apis'
import useDbMeta from '@/utils/useDbMeta'
import { ElMessage } from 'element-plus'

function transferCase(tableName, transferModel) {
  let newTableName = tableName
  if (transferModel === 'upper') {
    newTableName = newTableName.toUpperCase()
  }
  if (transferModel === 'lower') {
    newTableName = newTableName.toLowerCase()
  }
  return newTableName
}

function addIdentifier(str, targetRepoConfig) {
  return (
    (targetRepoConfig?.identifierBefore ?? '') +
    str +
    (targetRepoConfig?.identifierAfter ?? '')
  )
}

export const useMigrationStore = defineStore(
  'MigrationStore',
  () => {
    const transferModel = ref('unChange')

    const currTable = ref({})

    const ddlSqlText = ref('')

    const sourceState = reactive({
      repoList: [],
      currentRepo: null,
      currentSchema: null,
      schemaList: null,
      tableList: null,
      currentTable: null,
      columnList: [],
    })

    const targetState = reactive({
      repoList: [],
      currentRepo: null,
      currentSchema: null,
      schemaList: null,
      tableList: null,
      currentTable: null,
    })

    const { repoList } = useDbMeta(sourceState, {
      onTableChange: data => {
        sourceState.tableList = data.map(t => ({
          ...t,
          isChecked: false,
          targetTableName: transferTableName(t.tableName),
          repositoryId: sourceState.currentRepo,
        }))
      },
    })

    useDbMeta(targetState)

    const transferTableName = tableName => {
      return transferCase(tableName, transferModel.value)
    }

    const transferDataType = dataType => {
      const mapperData = mappingListData.value.find(
        t =>
          t.sourceId === sourceState.currentRepoObj?.dataProviderType &&
          t.targetId === targetState.currentRepoObj?.dataProviderType &&
          t.sourceType.toLowerCase() === dataType.toLowerCase(),
      )
      if (!mapperData) {
        ElMessage.warning('未找到映射关系：' + dataType)
        return ''
      }
      return mapperData.targetType
    }

    const buildTargetColumnList = data => {
      if (!data) data = []
      return data.map(t => ({
        isChecked: true,
        ...t,
        targetColumnName: transferCase(t.columnName, transferModel.value),
        targetIsPrimaryKey: t.isPrimaryKey,
        targetNullable: t.nullable,
        targetDataType: transferDataType(t.dataType),
        targetRemarks: t.remarks,
        targetDataLength: t.dataLength,
        targetDefaultValue: t.defaultValue,
      }))
    }

    const { send: getColumnListSend } = useRequest(
      data => apis.getColumns(data),
      {
        immediate: false,
      },
    )

    const { send: getIndexListSend } = useRequest(
      data => apis.getIndexes(data),
      {
        immediate: false,
      },
    )

    const loadTableColumn = async table => {
      const columnList = await getColumnListSend({
        tableName: table.tableName,
        schemaName: table.schemaName,
        repositoryId: sourceState.currentRepo,
      })
      const indexList = await  getIndexListSend({
        tableName: table.tableName,
        schemaName: table.schemaName,
        repositoryId: sourceState.currentRepo,
      })
      return {columnList,indexList}
    }

    watch(transferModel, () => {
      if(currTable.value && currTable.value.tableName){
        sourceState.tableList.forEach(t => {
          t.targetTableName = transferTableName(t.tableName)
          t.columns = buildTargetColumnList(t.columns)
        })
        ddlSqlText.value = buildDdlSql(currTable.value)
      }

    })

    watch(currTable, async () => {
      if (currTable.value && currTable.value.tableName) {
        const {columnList, indexList} = await loadTableColumn(currTable.value)
        currTable.value.columns = buildTargetColumnList(columnList)
        currTable.value.indexList = indexList;
        ddlSqlText.value = buildDdlSql(currTable.value)
      }
    })

    const { data: repoConfigList } = useRequest(apis.getRepoConfig)

    const targetRepoConfig = computed(() => {
      return repoConfigList.value.find(
        item => item.id === targetState.currentRepoObj?.dataProviderType,
      )
    })

    const columnEditDialog = ref(false)

    const currEditColumn = ref({})

    const mappingDialog = ref(false)

    const mappingList = ref([])

    const { data: columnTypeList } = useRequest(apis.getRepoColumnTypeList, {
      initialData: () => [],
    })

    const buildDdlSql = tableObj => {

      function findTargetColumn(columnName) {
        return tableObj.columns.find(t => t.columnName === columnName).targetColumnName
      }

      let ddl =
        'CREATE TABLE ' +
        addIdentifier(targetState.currentSchema, targetRepoConfig.value) +
        '.' +
        addIdentifier(tableObj.targetTableName, targetRepoConfig.value) +
        '(' +
        '\n'

      const columnList = tableObj.columns.filter(t => t.isChecked)

      for (let i = 0; i < columnList.length; i++) {
        const column = columnList[i]
        ddl +=
          '\t' +
          addIdentifier(column.targetColumnName, targetRepoConfig.value) +
          ' ' +
          column.targetDataType
        if (column.targetDataLength) {
          ddl += '(' + column.targetDataLength + ')'
        }
        if (!column.targetNullable ) {
          ddl += ' not null'
        }
        if (column.targetIsPrimaryKey) {
          ddl += ' primary key'
        }
        if (i < columnList.length - 1) {
          ddl += ','
        }
        ddl += '\n'
      }
      ddl += ');'
      if (tableObj.targetRemarks) {
        ddl +=
          '\n' +
          'COMMENT ON TABLE ' +
          addIdentifier(targetState.currentSchema, targetRepoConfig.value) +
          '.' +
          addIdentifier(tableObj.targetTableName, targetRepoConfig.value) +
          " IS '" +
          tableObj.targetRemarks +
          "';\n"
      }
      const commentList = columnList.filter(
        t => t.targetRemarks && t.targetRemarks.trim() !== '',
      )
      commentList.forEach(column => {
        ddl +=
          '\n' +
          'COMMENT ON COLUMN ' +
          addIdentifier(targetState.currentSchema, targetRepoConfig.value) +
          '.' +
          addIdentifier(tableObj.targetTableName, targetRepoConfig.value) +
          '.' +
          addIdentifier(column.targetColumnName, targetRepoConfig.value) +
          " IS '" +
          column.targetRemarks +
          "';"
      })
      tableObj.indexList.forEach(t => {
        ddl +=
          '\n' +
          ( t.isUnique?'CREATE UNIQUE INDEX ':'CREATE INDEX ' )
           +
          addIdentifier(transferCase(t.indexName, transferModel.value), targetRepoConfig.value) +
          ' ON ' +
          addIdentifier(targetState.currentSchema, targetRepoConfig.value) +
          '.' +
          addIdentifier(tableObj.targetTableName, targetRepoConfig.value) +
          " USING btree " +
          '(' +
          t.indexColumns.map(column => addIdentifier(findTargetColumn(column.columnName), targetRepoConfig.value)).join(',')
          + ');'

      })
      return ddl
    }

    const {
      data: mappingListData,
      send: getRepoMappingSend,
      loading: mappingListLoading,
    } = useRequest(apis.getRepoMapping)
    return {
      currTable,
      transferModel,
      repoList,
      repoConfigList,
      mappingDialog,
      getRepoMappingSend,
      mappingListData,
      mappingListLoading,
      columnEditDialog,
      currEditColumn,
      targetRepoConfig,
      columnTypeList,
      sourceState,
      targetState,
      ddlSqlText,
    }
  },
  {
    persist: false,
  },
)
