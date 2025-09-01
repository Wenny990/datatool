const routes = [
  {
    path: '/',
    redirect: '/ogg',
    meta: {
      title: '首页',
      icon: 'iconfont icon-chazhaobiaodanliebiao',
    },
  },
  {
    path: '/ogg',
    name: 'ogg',
    component: () => import('@/views/ogg/index.vue'),
    meta: {
      title: 'OGG',
      icon: 'iconfont icon-component',
    },
  },
  {
    path: '/datax',
    name: 'datax',
    component: () => import('@/views/datax/index.vue'),
    meta: {
      title: 'DATAX',
      icon: 'iconfont icon-share-data',
    },
  },
  {
    path: '/apis',
    name: 'api接口',
    component: () => import('@/views/api/config/index.vue'),
    meta: {
      title: 'API接口',
      icon: 'iconfont icon-Checkpoint',
    },
  },
  {
    path: '/migration',
    name: 'migration',
    component: () => import('@/views/migration/index.vue'),
    meta: {
      title: '表结构同步',
      icon: 'iconfont icon-share-data',
    },
  },
  {
    path: '/repo',
    name: 'repo',
    component: () => import('@/views/repo/index.vue'),
    meta: {
      title: '数据源',
      icon: 'iconfont icon-shujuyuanguanligongju',
    },
  },
  {
    path: '/database/monitor',
    name: 'databaseMonitor',
    component: () => import('@/views/database/monitor/index.vue'),
    meta: {
      title: '数据库监控',
      icon: 'iconfont icon-peizhishujuyuan',
    },
  },
  {
    path: '/server',
    name: 'server',
    component: () => import('@/views/server/index.vue'),
    meta: {
      title: '服务器',
      icon: 'iconfont icon-zhujiguanli',
    },
  }
]

export default routes
