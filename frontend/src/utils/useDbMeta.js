import apis from '@/service/apis'
import { useRequest } from 'alova/client'

const repoList = ref([])

apis
  .getRepo()
  .send()
  .then(data => {
    repoList.value = data
  })

export default function useDbMeta(state , options) {
  state.repoList = repoList.value

  const { send: getSchemaListSend } = useRequest(
    data => apis.getSchemas(data),
    {
      immediate: false,
    },
  )

  const { send: getTableListSend } = useRequest(data => apis.getTables(data), {
    immediate: false,
  })

  const { send: getColumnListSend } = useRequest(
    data => apis.getColumns(data),
    {
      immediate: false,
    },
  )
  const { send: getIndexSend } = useRequest(
    data => apis.getIndexes(data),
    {
      immediate: false,
    },
  )


  watch( () => state.currentRepo, newVal => {
    state.currentSchema = null
    if (newVal) {
      state.currentRepoObj = repoList.value.find(item => item.id === newVal)
      state.dataSourceName = state.currentRepoObj.dataSourceName
      getSchemaListSend({
        repositoryId: newVal,
      }).then(data => {
        state.schemaList = data
      })
    }
  })

  watch( () => state.currentSchema, newVal => {
    state.currentTable = null
    if (newVal) {
      getTableListSend({
        schemaName: newVal,
        repositoryId: state.currentRepo,
      }).then(data => {
        state.tableList = data
        options?.onTableChange(data)
      })
    }
  })

  watch( () => state.currentTable, newVal => {
    state.columnList = state.indexList = []
    if (newVal) {
      getColumnListSend({
        tableName: newVal,
        schemaName: state.currentSchema,
        repositoryId: state.currentRepo,
      }).then(data => {
        state.columnList = data
      })
      getIndexSend({
        tableName: newVal,
        schemaName: state.currentSchema,
        repositoryId: state.currentRepo,
      }).then(data => {
        state.indexList = data
      })
    }
  })

  return {repoList}
}
