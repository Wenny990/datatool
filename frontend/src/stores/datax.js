import { defineStore } from 'pinia'

/**
 * 仪表盘的筛选器信息
 */
export const useDataXStore = defineStore('DataXStore', {
  state: () => ({
    currComponent: 'JobRun',
    componentList: [
      {
        title: '任务运行情况',
        key: 'JobRun',
        type: 'JobRun',
        allowClose: false,
      },
    ],
    runningJobList: [],
    currNode: null,
  }),
  getters: {
    currComponentObj(state) {
      return state.componentList.find(t => t.key === state.currComponent)
    },
  },
  actions: {
    addTab(data) {
      const index = this.componentList.findIndex(t => t.key === data.key)
      if (index < 0) {
        this.componentList.push(data)
      }
      this.currComponent = data.key
    },
    removeTabList(keys) {
      keys.forEach(key => {
        this.removeTab(key)
      })
    },
    removeTab(key) {
      this.componentList = this.componentList.filter(t => t.key !== key)
      if (this.currComponent === key) {
        this.currComponent = this.componentList[0]
          ? this.componentList[0].key
          : ''
      }
    },
    onRefreshNode(data) {
      console.log(data)
      if (this.currNode) {
        this.currNode.loaded = false // 设置loaded为false；模拟一次节点展开事件，加载重命名后的新数据；
        this.currNode.expand()
      }
    },
  },
})
