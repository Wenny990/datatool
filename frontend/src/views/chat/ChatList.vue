<script setup>
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useThrottleFn, watchThrottled } from '@vueuse/core'
import { vScroll } from '@vueuse/components'
import { ArrowDown, ArrowUp, Loading } from '@element-plus/icons-vue'
import SvgIcon from '@/components/svgIcon/index.vue'

const props = defineProps({
  items: {
    type: Array,
    required: true,
  },
  roles: {
    type: Object,
    default: () => ({}),
  },
  placeholder: {
    type: String,
    default: 'Shift + Enter 换行，enter 发送',
  },
})

const { items } = toRefs(props)

const emit = defineEmits(['submit', 'change', 'cancel'])

const showToBottom = ref(true)
let autoScroll = true

// 添加 scrollToBottom 方法
const scrollToBottom = () => {
  const chatList = document.querySelector('.chat-list')
  if (chatList) {
    chatList.scrollTop = chatList.scrollHeight
    autoScroll = true
  }
}

// 监听 items 的变化，自动滚动到底部
watchThrottled(
  () => {
    const lastItem = props.items[props.items.length - 1]
    return lastItem
      ? { content: lastItem.content, thinkContent: lastItem.thinkContent }
      : null
  },
  () => {
    if (autoScroll) {
      nextTick(() => {
        scrollToBottom()
      })
    }
  },
  {
    // deep: true ,
    immediate: true,
    throttle: 500,
  },
)

const onScroll = useThrottleFn(e => {
  const { x, y, isScrolling, arrivedState, directions } = e
  if (directions.top) {
    autoScroll = false
    showToBottom.value = true
  }else if (arrivedState.bottom) {
    autoScroll = true
    showToBottom.value = false
  }
}, 10)

defineExpose( {
  scrollToBottom
} )
</script>

<template>
  <div class="chat-container h100 flex-center">
    <div class="chat-list" ref="chatList" v-scroll="onScroll">
      <div
        class="chat-item"
        :class="{ 'chat-item-right': item.role === 'user' }"
        v-for="item in items"
      >
        <div class="chat-content" v-loading="item.loading">
          <div
            class="chat-text"
            v-if="item.role === 'user'"
            v-html="item.content"
          ></div>
          <div
            class="chat-think"
            v-if="item.role === 'ai' && item.thinkContent?.trim()"
          >
            <div class="chat-think-header">
              <div
                @click="item.arrow = !item.arrow"
                class="chat-think-header-p"
              >
                <svg-icon name="iconfont icon-shenduheguangdu"></svg-icon><span>深度思考</span>
                <el-icon class="is-loading" v-if="item.isThink">
                  <Loading />
                </el-icon>
                <el-icon v-show="!item.arrow">
                  <ArrowUp />
                </el-icon>
                <el-icon v-show="item.arrow">
                  <ArrowDown />
                </el-icon>
              </div>
            </div>
            <div class="chat-think-main" v-show="!item.arrow">
              <div class="chat-think-main-border"></div>
              <div class="chat-think-main-content">{{ item.thinkContent }}</div>
            </div>
          </div>
          <MdPreview
            v-if="item.role === 'ai'"
            theme="light"
            :showCodeRowNumber="true"
            :modelValue="item.content"
          />
        </div>
        <div class="chat-footer">
          <slot name="footer" :item="item"></slot>
        </div>
      </div>
    </div>
    <div class="backBottom" v-show="showToBottom">
      <el-button circle @click="scrollToBottom" :icon="ArrowDown"></el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 10px;
  position: relative;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 3px;
  }

  .chat-item:last-child .chat-footer {
    visibility: visible;
  }


}

.backBottom {
  position: absolute;
  bottom: 32px;
  right: 32px;
}

.chat-item {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  max-width: 100%;
  align-self: flex-start;
}

.chat-item-right {
  align-self: flex-end !important;
}

.chat-content {
  font-size: var(--el-font-size-base);
  line-height: var(--el-font-line-height-primary);
  color: #262626;
  padding: 10px;
  box-sizing: border-box;
  border-radius: 14px;
}

.chat-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-item .chat-content {
  //background-color: #f5f5f5;
}

.chat-item-right .chat-content {
  background-color: #eff6ff;
}

.chat-footer {
  display: flex;
  margin-top: 12px;
  height: 20px;
  align-items: center;
  gap: 12px;
  visibility: hidden;
}

.chat-item:hover .chat-footer {
  visibility: visible;
}

.chat-think-header {
  display: flex;
}

.chat-think-header-p {
  display: flex;
  cursor: pointer;
  background-color: #f5f5f5;
  border-radius: 10px;
  justify-content: center;
  align-items: center;
  width: fit-content;
  padding: 7px 14px;
  gap: 10px;

  &:hover {
    background-color: #ededed;
  }
}

.chat-think-main {
  color: #8b8b8b;
  white-space: pre-wrap;
  margin: 0;
  padding: 0 0 0 13px;
  line-height: 26px;
  position: relative;

  .chat-think-main-border {
    border-left: 2px solid #e5e5e5;
    height: calc(100% - 10px);
    margin-top: 5px;
    position: absolute;
    top: 0;
    left: 0;
  }

  .chat-think-main-content {
  }
}
</style>
