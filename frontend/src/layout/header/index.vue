<script setup>
import { Operation } from '@element-plus/icons-vue'
import routes from '@/router/route.js'
import { useRoute } from 'vue-router'
import { useGlobalSettingStore } from '@/stores/config'
import GlobalConfig from '@/layout/config/global-config.vue'
import GlobalChat from '@/layout/config/global-chat.vue'
import { useDark } from '@vueuse/core'

const route = useRoute()

const state = reactive({
  defaultActive: '/',
})

// 设置页面当前路由高亮
const setCurrentRouterHighlight = currentRoute => {
  const { path, meta } = currentRoute
  state.defaultActive = `/${path.split('/')[1]}`
}

onMounted(() => {
  // setCurrentRouterHighlight(route)
})

const collapse = ref(false)

watch(
  () => route.path,
  () => {
    setCurrentRouterHighlight(route)
  },
)

const globalSettingStore = useGlobalSettingStore()

const isDark = useDark()
</script>

<template>
  <div
    class="flex justify-center"
    :class="{
      'h-vertical': globalSettingStore.layout === 'vertical',
      'h-horizontal': globalSettingStore.layout === 'defaults',
    }"
  >
    <el-menu
      :default-active="state.defaultActive"
      class="flex-auto el-menu-demo"
      :collapse="collapse"
      :mode="
        globalSettingStore.layout === 'defaults' ? 'horizontal' : 'vertical'
      "
      router
    >
      <template v-for="val in routes.filter(t => t.path !== '/')">
        <el-sub-menu
          :index="val.path"
          v-if="val.children && val.children.length > 0"
          :key="val.path"
        >
          <template #title>
            <SvgIcon :name="val.meta.icon" />
            <span>{{ val.meta.title }}</span>
          </template>
        </el-sub-menu>
        <template v-else>
          <el-menu-item :index="val.path" :key="val.path">
            <template
              #title
              v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)"
            >
              <SvgIcon :name="val.meta.icon" :size="18" class="mr4" />
              {{ val.meta.title }}
            </template>
            <template #title v-else>
              <a
                :href="val.meta.isLink"
                target="_blank"
                rel="opener"
                class="w100"
              >
                <SvgIcon :name="val.meta.icon" />
                {{ val.meta.title }}
              </a>
            </template>
          </el-menu-item>
        </template>
      </template>
    </el-menu>
    <div class="other-y gap6"  v-if="globalSettingStore.layout === 'vertical'">
      <el-button  text @click="collapse = !collapse">展开收缩</el-button>
      <el-tooltip content="系统配置" :effect="isDark ? 'dark' : 'light'">
        <el-button text @click="globalSettingStore.isShowConfig = true">
          <el-icon size="20">
            <Operation />
          </el-icon>
        </el-button>
      </el-tooltip>
    </div>
    <div class="other flex justify-center align-center px20 gap6" v-else>
      <el-tooltip content="系统配置" :effect="isDark ? 'dark' : 'light'">
        <el-button text @click="globalSettingStore.isShowConfig = true">
          <el-icon size="20">
            <Operation />
          </el-icon>
        </el-button>
      </el-tooltip>
      <!--      <el-button text @click="globalSettingStore.isShowChat = true">-->
      <!--        <img src="/icon/AI助手.png" style="height: 24px"></img>-->
      <!--      </el-button>-->
    </div>
    <global-config />
    <global-chat />
  </div>
</template>

<style scoped lang="scss">
.other {
  border-bottom: 1px solid var(--el-menu-border-color);
  height: var(--el-menu-horizontal-height);
}

.h-vertical {
  height: 100%;
  flex-direction: column;
  justify-content: space-between;
}

.h-horizontal {
  height: 100%;
  align-content: center;
}

.other-y {
  display: flex;
  flex-direction: column;
}
</style>
