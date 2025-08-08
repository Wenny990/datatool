<template>
  <div ref="codeMirror" class="code-contrast" style="width:100%;height:100%"/>
</template>

<script setup>
import {MergeView} from "@codemirror/merge"
import {EditorView, basicSetup,} from "codemirror"
import {placeholder} from "@codemirror/view"
import {EditorState} from "@codemirror/state"
import {json} from "@codemirror/lang-json";
import {sql} from "@codemirror/lang-sql";
import {xml} from "@codemirror/lang-xml";
import {computed, onUnmounted, shallowRef} from "vue";
import {dracula} from "@uiw/codemirror-theme-dracula";


const props = defineProps({
  modelValue: Array,
  disabled: {
    type: Boolean,
    default: false
  },
  type: {
    type: String
  },
  height: {
    type: String,
    default: () => '100%'
  },
  placeholder: {
    type: String
  }

})


const codeMirror = ref()
const lang = {json: json(), sql: sql(), xml: xml()}


const extensions = computed(() => {
  const result = []
  result.push(basicSetup)
  if (lang[props.type]) {
    result.push(lang[props.type])
  }
  if (props.disabled) {
    result.push(EditorView.editable.of(false))
    result.push(EditorState.readOnly.of(true))
  }
  result.push(json())
  result.push(EditorView.lineWrapping)
  result.push(dracula)
  result.push(EditorState.tabSize.of(4))
  result.push(placeholder(props.placeholder ?? '暂无内容'))
  return result
})

const mergeView = shallowRef({})
const initUi = () => {
  mergeView.value = new MergeView({
    a: {
      doc: props.modelValue[0],
      extensions: extensions.value
    },
    b: {
      doc: props.modelValue[1],
      extensions: extensions.value
    },

    parent: codeMirror.value,
    collapseUnchanged: true,
    revertControls: "a-to-b",
    highlightChanges: true,
  })
}

onMounted(() => {
  initUi();
})

onUnmounted(() => {
  if (mergeView.value && mergeView.value.destroy) mergeView.value.destroy()
})
</script>
<style lang="scss">
.code-contrast {
  overflow: auto;
}

.code-contrast .CodeMirror-merge-copy,
.CodeMirror-merge-scrolllock-wrap {

  display: none !important;

}
</style>