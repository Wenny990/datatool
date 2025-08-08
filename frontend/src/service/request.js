import { createAlova } from 'alova'
import adapterFetch from 'alova/fetch'
import { ElMessage } from 'element-plus'
import VueHook from 'alova/vue'
import { start, close } from '@/nprogress/nprogress'

const item = localStorage.getItem('GlobalSetting');

const GlobalSetting = item ? JSON.parse(item) : {};

export const alovaIns = createAlova({
  // 假设我们需要与这个域名的服务器交互
  baseURL: GlobalSetting.baseURL??'/',
  requestAdapter: adapterFetch(),
  timeout: GlobalSetting.timeOut??10000,
  cacheFor: 0, //不缓存
  // 在vue项目下引入VueHook，它可以帮我们用vue的ref函数创建请求相关的，可以被alova管理的状态
  statesHook: VueHook,
  // 设置全局的请求拦截器，与axios相似
  beforeRequest({ config }) {
    // const computedToken = getToken()
    // 假设我们需要添加token到请求头
    // config.headers.Authorization = `Bearer ${computedToken}`
    // config.headers.Token = computedToken;
    config.headers['Content-Type'] = 'application/json; charset=utf-8'
    start()
  },
  // 响应拦截器，也与axios类似
  responded: async response => {
    close()
    if (response.status !== 200) {
      ElMessage.warning(response.statusText)
      throw new Error(response)
    }
    const json = await response.json()
    if (response.status !== 200 || !json.success) {
      // 这边抛出错误时，将会进入请求失败拦截器内
      if (json.errMsg) {
        ElMessage.warning(json.errMsg)
        // // 空 token 且 状态码 401 不弹提示
        // if (!computedToken && response.status === 401) {
        //   removeToken();
        //   uni.reLaunch({
        //     url: '/pages/login/index'
        //   })
        // } else {
        //   uni.showToast({
        //     icon: 'exception',
        //     title: json.errMsg
        //   })
        // }
        throw new Error(json.errMsg)
      } else {
        throw new Error(json.error)
      }
    }
    return json.data
  },
})


