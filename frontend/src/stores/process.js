import {defineStore} from 'pinia';

/**
 * ogg进程信息
 * @methods setUserInfos 设置用户信息
 */
export const useProcessStore = defineStore('oggProcess', {
	state: () => ({
		currProcessName: null,
		currServerId: null,
		currProcess:{},
		currServer:{},
		currComponent: 'OggMonitor',
		componentList: [{
			type: 'OggMonitor',
			key: 'OggMonitor',
			title: '进程监控',
			allowClose: false
		}],
		oggGenerateConfig:{},
		oggConfig: {}
	}),
	actions: {
		addTab(data){
			const index = this.componentList.findIndex(t => t.key === data.key);
			if(index < 0 ){
				this.componentList.push(data)
			}
			this.currComponent = data.key;
		},
		removeTab(targetName){
			const tabs = this.componentList
			let activeName = this.currComponent
			if (activeName === targetName) {
				tabs.forEach((tab, index) => {
					if (tab.key === targetName) {
						const nextTab = tabs[index + 1] || tabs[index - 1]
						if (nextTab) {
							activeName = nextTab.key
						}
					}
				})
			}
			this.currComponent = activeName
			this.componentList = tabs.filter((tab) => tab.key !== targetName)
		}
	},
});
