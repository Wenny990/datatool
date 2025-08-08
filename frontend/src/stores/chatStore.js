import { defineStore } from 'pinia'
import { v4 as uuidv4 } from 'uuid'
import fetchStreamRequest from '@/views/chat/fetchStreamRequest'

const item = localStorage.getItem('GlobalSetting')

const GlobalSetting = item ? JSON.parse(item) : {}

export const useChatStore = defineStore(
  'chatStore',
  () => {
    const model = ref('')

    const chatRecords = ref([])

    const apis = GlobalSetting.baseURL ?? '/api'

    const isReady = ref(true)

    isReady.value = true

    // 解码包含Unicode转义序列的字符串
    function decodeUnicode(str) {
      return str.replace(/\\u[\dA-Fa-f]{4}/g, function (match) {
        return String.fromCharCode(parseInt(match.substr(2), 16))
      })
    }

    let cancelFunc

    const requestAi = (record) => {
      const newUuid = uuidv4()
      const newRecord = {
        uuid: newUuid,
        replyUuid: record.uuid,
        role: 'ai',
        loading: true,
        content: '',
        thinkContent: '',
        isThink: false,
        arrow: false,
        state: 0
      }
      chatRecords.value.push(newRecord)
      let isThink = false
      isReady.value = false
      const request = {
        groupId: '999',
        prompt: record.content,
        model: model.value,
      }
      cancelFunc = fetchStreamRequest(
        apis + '/chat/advanced?cacheCode=999&prompt=' + record.content,
        request,
        data => {
          newRecord.state = 2
          if (newRecord.loading) {
            newRecord.loading = false
          }
          if (data.includes('<think>')) {
            isThink = true
            data = data.replace('<think>', '');
            newRecord.isThink = true
          }
          if (isThink) {
            newRecord.thinkContent += data
          } else {
            newRecord.content += data
          }
          if (isThink && data.includes('</think>')) {
            newRecord.isThink = isThink = false
            newRecord.thinkContent = newRecord.thinkContent.replace('</think>', '')
          }

          // 使用 Vue.set 或替换整个对象来确保响应式更新
          const index = chatRecords.value.findIndex(t => t.uuid === newUuid)
          if (index !== -1) {
            chatRecords.value[index] = {
              ...newRecord,
              content: newRecord.content,
              arrow: chatRecords.value[index].arrow
            }
          }
        },
        () => {
          isReady.value = true
          newRecord.state = 3
          // 使用 Vue.set 或替换整个对象来确保响应式更新
          const index = chatRecords.value.findIndex(t => t.uuid === newUuid)
          if (index !== -1) {
            chatRecords.value[index] = {
              ...newRecord,
              state: 3
            }
          }
        },
        error => {
          isReady.value = true
          console.error('Error:', error)
          newRecord.state = 4
          // 使用 Vue.set 或替换整个对象来确保响应式更新
          const index = chatRecords.value.findIndex(t => t.uuid === newUuid)
          if (index !== -1) {
            chatRecords.value[index] = {
              ...newRecord,
              state: 4
            }
          }
        },
        () => {
          newRecord.state = 1
        }
      )
    }

    const addChatRecord = record => {
      record.uuid = uuidv4()
      chatRecords.value.push(record)
      requestAi(record)
    }

    const retryRecord = record => {
      // const oldRecord = chatRecords.value.find(item => item.uuid === record.replyUuid)
      // if (oldRecord) {
      //   chatRecords.value.splice(chatRecords.value.findIndex(item => item.uuid === record.uuid),1)
      //   requestAi(oldRecord)
      // }
      addChatRecord(record)
    }

    const cancelRecord = uuid => {
      cancelFunc && cancelFunc()
    }

    const removeRecord = (record) => {
      chatRecords.value.splice(chatRecords.value.findIndex(item => item.uuid === record.uuid),1)
    }

    const clearChatRecord = () => {
      chatRecords.value = []
      cancelRecord()
      isReady.value = true
    }

    return {
      model,
      chatRecords,
      addChatRecord,
      retryRecord,
      cancelRecord,
      isReady,
      clearChatRecord,
      removeRecord
    }
  },
  {
    persist: true,
  },
)
