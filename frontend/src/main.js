import './assets/index.scss'
import 'splitpanes/dist/splitpanes.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css' //暗黑模式

import SvgIcon from '/@/components/svgIcon/index.vue';


const app = createApp(App)

app.use(createPinia().use(piniaPluginPersistedstate))
app.use(router)
app.use(ElementPlus)
app.component('SvgIcon', SvgIcon);
app.mount('#app')
