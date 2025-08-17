import { alovaIns } from './request.js'
import { cloneDeep } from 'lodash-es'
import { encryptDES } from '@/utils/desUtil'

const getRequest = (url, data, config) =>
  alovaIns.Get(url, {
    params: data,
    ...config,
    localCache: 0,
  })
const postRequest = (url, data) => alovaIns.Post(url, data)
const putRequest = (url, data) => alovaIns.Put(url, data)
const deleteRequest = (url, data) => alovaIns.Delete(url, data)

const isJsonStr = str => {
  if (typeof str === 'string') {
    try {
      const obj = JSON.parse(str)
      return !!(typeof obj === 'object' && obj)
    } catch (e) {
      return false
    }
  }
  return false
}

export default {
  /**
   * 任务组相关接口
   *
   */
  // 获取任务组列表
  getDataxJobGroupList: data => getRequest('/dataxJobGroup', data),
  // 保存任务组
  saveDataxJobGroup: data => postRequest('/dataxJobGroup', data),
  // 删除任务组
  delDataxJobGroup: data => deleteRequest('/dataxJobGroup/' + data),

  //获取datax插件列表
  getDataxPlugins: () => getRequest('/dataxPlugin/'),

  /**
   * 任务相关接口
   */
  // 获取任务组下的全部任务
  getDataxJobList: data => getRequest('/dataxJob/group/' + data),
  // 获取单个任务
  getDataxJob: data =>
    new Promise((resolve, reject) => {
      getRequest('/dataxJob/' + data)
        .send()
        .then(dataxJob => {
          if (isJsonStr(dataxJob.setting)) {
            dataxJob.setting = JSON.parse(dataxJob.setting)
          } else {
            dataxJob.setting = {}
          }
          if (isJsonStr(dataxJob.writer)) {
            dataxJob.writer = JSON.parse(dataxJob.writer)
          } else {
            dataxJob.writer = {}
          }
          if (isJsonStr(dataxJob.reader)) {
            dataxJob.reader = JSON.parse(dataxJob.reader)
          } else {
            dataxJob.reader = {}
          }
          if (isJsonStr(dataxJob.transformer)) {
            dataxJob.transformer = JSON.parse(dataxJob.transformer)
          } else {
            dataxJob.transformer = []
          }
          if (isJsonStr(dataxJob.splitParams)) {
            dataxJob.splitParams = JSON.parse(dataxJob.splitParams)
          } else {
            dataxJob.splitParams = {
              type: 'none',
            }
          }
          resolve(dataxJob)
        })
        .catch(err => {
          reject(err)
        })
    }),
  // 上传任务
  uploadDataxJob: data => getRequest('/dataxJob' + '/upload/' + data),
  // 后台执行任务
  executeDataxJob: id => postRequest('/dataxJob' + '/execute/' + id),
  // 后台执行区间任务
  executeDataxJobByRange: id => postRequest('/dataxJob' + '/executeByRange/' + id),
  // 后台同步执行区间任务
  executeDataxJobBySyncRange: id => postRequest('/dataxJob' + '/executeByRangeSync/' + id),

  // 获取全部任务日志
  getAllExecuteLogKeys: () => getRequest('/dataxJob' + '/execute'),
  // 获取单个任务执行日志
  getExecuteLog: id => getRequest('/dataxJob' + '/execute/' + id),
  // 获取单个任务执行日志详情
  getExecuteLogDetail: id => getRequest('/dataxJob' + '/execute/detail/' + id),
  // 删除任务日志
  removeExecuteLog: data => postRequest('/dataxJob' + '/execute', data),
  stopExecuteLog: id => deleteRequest('/dataxJob' + '/execute/' + id),
  // 加密保存任务
  saveDataxJob: oldData => {
    let data = cloneDeep(oldData)
    data.jobParams = encryptDES(data.jobParams)
    data.setting = encryptDES(JSON.stringify(data.setting))
    data.writer = encryptDES(JSON.stringify(data.writer))
    data.reader = encryptDES(JSON.stringify(data.reader))
    data.splitParams = encryptDES(JSON.stringify(data.splitParams))
    data.transformer = encryptDES(JSON.stringify(data.transformer))
    return postRequest('/dataxJob', data)
  },
  // 删除任务
  deleteDataxJob: data => deleteRequest('/dataxJob' + '/' + data),

  /**
   * 服务器相关接口
   */
  //获取服务器列表
  getServerList: () => getRequest('/server'),
  //保存服务器
  saveServer: data => postRequest('/server', data),
  //删除服务器
  delServer: data => deleteRequest('/server' + '/' + data),
  //测试服务器
  verifyServer: data => postRequest('/server' + '/test', data),
  //监控服务器
  monitorServer: data => getRequest('/server' + '/monitor/' + data),
  // 获取监控信息
  getInfo: () => getRequest('/monitor/server'),

  /**
   * 数据库连接相关接口
   */
  // 获取数据连接列表
  getRepo: () => getRequest('/repo'),
  //保存数据连接
  saveRepo: data => postRequest('/repo', data),
  // 删除数据连接
  delRepo: data => deleteRequest('/repo' + '/' + data),
  // 检查数据连接
  checkRepo: data => postRequest('/repo' + '/checkRepo', data),
//解析sql语句
  parseSql: data => postRequest('/repo' + '/parseSql', data),
  //查询sql语句
  execPageQuery: data => postRequest('/repo' + '/execPageQuery', data),
  // 获取数据库连接配置列表
  getRepoConfig: () => getRequest('/repo' + '/config'),



  /**
   * 数据库元数据相关接口
   */
  // 获取数据库模式
  getSchemas: data => getRequest('/dbMeta' + '/schema', data),
  //获取数据库表
  getTables: data => getRequest('/dbMeta' + '/table', data),
  //获取表字段
  getColumns: data => getRequest('/dbMeta' + '/column', data),
  //获取表索引
  getIndexes: data => getRequest('/dbMeta' + '/index', data),

  /**
   * ogg进程相关接口
   */
  // 获取服务器进程列表
  getProcessList: serverId => getRequest('/process' + '/' + serverId),
  // 获取ogg进程执行明细
  getProcessDetail: (serverId, processName) =>
    getRequest('/process' + '/' + serverId + '/' + processName),
  // 获取ogg进程参数
  getProcessParams: (serverId, processName) =>
    getRequest('/process' + '/params/' + serverId + '/' + processName),
  // 获取ogg进程报告
  getProcessReport: (serverId, processName) =>
    getRequest('/process' + '/report/' + serverId + '/' + processName),
  // 获取ogg进程信息
  getProcessInfo: (serverId, processName) =>
    getRequest('/process' + '/info/' + serverId + '/' + processName),
  // 启动ogg进程
  startProcess: (serverId, processName, isStart) =>
    getRequest('/process' + '/' + serverId + '/' + processName + '/' + isStart),
  // 删除ogg进程
  deleteProcess: (serverId, processName) =>
    getRequest('/process' + '/delete/' + serverId + '/' + processName),
  // 保存ogg进程参数
  saveProcessParams: oldData => {
    let data = cloneDeep(oldData)
    data.params = encryptDES(data.params)
    return postRequest('/process' + '/params', data)
  },
  // 生成ogg配置文件
  generateOggConfig: data => postRequest('/process' + '/generate', data),
  execOggConfig: data => postRequest('/process' + '/exec', data),
  // 获取监控信息
  getMonitorInfo: () => getRequest('/process' + '/monitor'),
  // 刷新监控信息不走缓存
  getMonitorInfoNoCache: () => getRequest('/process' + '/monitor/nocache'),
  // 预览命令结果
  previewCommandResult: data => postRequest('/process' + '/commandPreview', data),
  //ogg的ggsci下执行命令
  commandByOgg: data => postRequest('/process' + '/commandByOgg', data),



  // 获取定义文件参数
  getDefFileParam: data => getRequest('/defFile', data),
  // 保存定义文件参数
  saveDefFileParam: oldData => {
    let data = cloneDeep(oldData)
    data.paramContent = encryptDES(data.paramContent)
    return postRequest('/defFile', data)
  },
  // 生成定义文件
  defgenDefFile: data => getRequest('/defFile' + '/defgen', data),
  // 上传定义文件到目标端
  uploadDefFile: data => getRequest('/defFile' + '/upload', data),
  // 获取定义文件内容
  getDefFileContent: data => getRequest('/defFile' + '/content', data),

  /**
   * 数据库字段类型映射接口
   */
  // 获取数据库映射信息
  getRepoMapping: () => getRequest('/repo_mapping'),
  // 获取数据库字段类型列表
  getRepoColumnTypeList: () => getRequest('/repo_mapping' + '/columnType'),
  // 保存修改数据库映射信息
  saveRepoMapping: data => postRequest('/repo_mapping', data),
  // 删除数据库映射信息
  deleteRepoMapping: data => deleteRequest('/repo_mapping' + '/' + data),

  //获取大模型列表
  getChatModelList: () => getRequest('/chat/model'),

  //清理服务端全部缓存
  clearAllCache: () => getRequest('/cache' + '/clearAll'),
}
