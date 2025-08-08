import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useGlobalSettingStore = defineStore(
  'GlobalSetting',
  () => {
    const layout = ref('defaults')
    const defaultSize = ref('small')
    const timeOut = ref(15000)
    const isShowConfig = ref(false)
    const isShowChat = ref(false)
    const baseURL = ref('/')
    return { layout, defaultSize, timeOut, isShowConfig, baseURL }
  },
  {
    persist: true,
  },
)
