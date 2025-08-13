<template>
  <div
    class="code-edit"
    :style="{ height: height, width: '100%' }"
    ref="codeEditRef"
    id="codeEditContainer"
  >
    <div class="code-edit-opt">
      <el-button type="primary" size="small" plain @click="copyText(codes)"
        >复制
      </el-button>
      <el-button type="warning" size="small" plain @click="toggle"
        >全屏
      </el-button>
      <slot></slot>
    </div>
    <codemirror
      ref="codeRef"
      v-model="codes"
      :placeholder="placeholder ?? '在此输入内容...'"
      style="height: 100%; width: 100%"
      :autofocus="true"
      :indent-with-tab="true"
      :tab-size="4"
      :line-warpping="true"
      :extensions="extensions"
      :disabled="disabled"
    />
  </div>
</template>

<script>
import { Codemirror } from 'vue-codemirror'
import { sql } from '@codemirror/lang-sql'
import { json } from '@codemirror/lang-json'
import { dracula } from '@uiw/codemirror-theme-dracula'
import { eclipse } from '@uiw/codemirror-theme-eclipse'
import commonFunction from '@/utils/commonFunction'
import { EditorView, ViewPlugin } from '@codemirror/view'
import { computed, toRefs } from 'vue'
import { useDark, useFullscreen } from '@vueuse/core'

function isElementScrolledToBottom(el, wiggleRoom = 10) {
  return el.scrollHeight - el.scrollTop <= el.clientHeight + wiggleRoom
}

const scrollViewPlugin = ViewPlugin.fromClass(
  class {
    scrolledToBottom = true
    programmaticScroll = false

    update(update) {
      // console.info(1)

      if (update.docChanged) {
        requestAnimationFrame(() => {
          // Scroll to the bottom of the console.
          const el = update.view.scrollDOM
          el.scrollTop = el.scrollHeight
        })
        // for (let tr of update.transactions) {
        //   for (let e of tr.effects) {
        //     console.info(3)
        //     if (e.is(myCustomEffect) && this.scrolledToBottom) {
        //       // Add flag that this was a programmatic scroll.
        //       this.programmaticScroll = true;
        //       // Delay by an animation frame to ensure DOM is ready
        //
        //     }
        //   }
        // }
      }
    }
  },
  {
    eventHandlers: {
      scroll: function (e, view) {
        // Ignore programmatic scroll events.
        if (this.programmaticScroll) {
          this.programmaticScroll = false
          return
        }
        this.scrolledToBottom = isElementScrolledToBottom(view.scrollDOM)
      },
    },
  },
)

export default defineComponent({
  name: 'codeEditor',
  components: {
    Codemirror,
  },
  setup(props, ctx) {
    const { copyText } = commonFunction()
    const lang = { json: json(), sql: sql() }

    const isDark = useDark()

    const extensions = computed(() => {
      const result = []
      if (props.type) {
        result.push(lang[props.type])
      }

      result.push(EditorView.lineWrapping)
      // 始终显示最后一行
      if (props.isBottom) {
        result.push(scrollViewPlugin)
      }

      isDark.value ? result.push(dracula) : result.push(eclipse)
      return result
    })

    const codes = computed({
      get: function () {
        return props.modelValue ?? ''
      },
      set: function (val) {
        //侦听到setter()事件，将值传递回父级组件
        ctx.emit('update:modelValue', val)
      },
    })

    const codeEditRef = ref()

    const fontSize = computed(() => {
      let fontSize1 = 13
      if(props.fontSize){
        fontSize1 = props.fontSize
      }
      return fontSize1
    })

    const { toggle } = useFullscreen(codeEditRef)
    return {
      ...toRefs(props),
      extensions,
      copyText,
      codes,
      codeEditRef,
      toggle,
      fontSize,

    }
  },
  props: {
    modelValue: String,
    disabled: {
      type: Boolean,
      default: false,
    },
    type: {
      type: String,
    },
    height: {
      type: String,
      default: () => '100%',
    },
    isBottom: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
    },
    fontSize: {
      type: Number,
    }
  },
})
</script>

<style lang="scss">
$fontSize: v-bind("fontSize + 'px'");

.code-edit {
  border: 1px solid var(--el-border-color);
  border-radius: var(--el-border-radius-base);
  position: relative;
  width: 100%;
  overflow-y: auto;
  transition: all 0.5s;
  //min-height: 300px;
  font-size: $fontSize;

  ::-webkit-scrollbar-thumb {
    background-color: transparent;
  }

  &:hover {
    ::-webkit-scrollbar-thumb {
      background-color: rgba(144, 147, 153, 0.3);
      transition: var(--el-transition-duration) background-color;
      opacity: var(--el-scrollbar-opacity, 0.3);
    }
  }

  .code-edit-opt {
    overflow: hidden;
    position: absolute;
    right: 12px;
    top: 5px;
    z-index: 2000;
    //visibility: hidden;
    opacity: 0;
    transition: all 0.5s;

    & > * {
      padding-left: 10px;
    }
  }

  &:hover .code-edit-opt {
    opacity: 1;
    right: 12px;
  }

  ::-webkit-scrollbar-track-piece {
    background-color: transparent !important;
  }
}

:deep(.ͼ1 .cm-scroller) {
  height: 100%;
  overflow-y: scroll;
  border-radius: 12px;
  //font-family: Consolas, 'Courier New', monospace;
  font-size: $fontSize;
}

.cm-line {
  font-family: "JetBrains Mono", monospace,Microsoft YaHei !important;
}

.ͼ2 .cm-content, .ͼ4 .cm-line {
  caret-color: black !important;
}


:deep(.codemirror-container.bordered) {
  border: 1px solid var(--next-border-color-light);
  border-radius: var(--el-border-radius-base);
}
</style>

<style lang="scss">
.CodeMirror-hints {
  z-index: 1990 !important;
  min-width: 120px !important;
  background-color: #21232d;
}

.CodeMirror-hint {
  color: #bcc9d4;
}

.ͼ1 .cm-scroller,
.ͼ1 {
  //height: 100% !important;
  //overflow-y: visible !important;
}
</style>
