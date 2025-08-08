<template>
  <div
      class="shellContainer pd20 h100 w100"
      ref="el"
      style="background: #282a36;"
  >
    <div id="terminal" class="h100 w100" ref="terminal" v-on:contextmenu="handleRightClick"></div>
  </div>
</template>

<script setup>
import {Terminal} from "xterm";
import {FitAddon} from "xterm-addon-fit";
import "xterm/css/xterm.css";
import "xterm/lib/xterm.js";
import {AttachAddon} from 'xterm-addon-attach';
import {useElementSize} from "@vueuse/core";
import {debounce} from "lodash-es";


const props = defineProps({
  data:{
    type: Object,
    require: true
  }
})

const terminal = ref('')

let  webSocket, fitAddon;

let term = null;
let split = 8
const el = ref(null)
const { width, height } = useElementSize(el)
const init = () => {
  term = new Terminal({
    rendererType: "canvas", //渲染类型
    // rows: parseInt(rows), //行数
    // cols: parseInt(cols), // 不指定行数，自动回车后光标从下一行开始
    fontSize: 14,
    lineHeight: 1.4,
    fontFamily: 'Consolas, "Courier New", monospace',
    convertEol: true, //启用时，光标将设置为下一行的开头
    tabStopWidth: 4,
    scrollback: 500, //终端中的回滚量
    disableStdin: false, //是否应禁用输入。
    cursorStyle: "block", //光标样式
    cursorBlink: true, //光标闪烁
    theme: {
      foreground: "#f8f8f8", //字体
      background: "#282a36", //背景色
      // cursor: "help", //设置光标

    },
    screenReaderMode: true,
    windowsMode: true,
  });

// 创建terminal实例
  term.open(terminal.value);

// 换行并输入起始符“$”
  term.prompt = () => {
    term.write("\r\n$ ");
  };

  term.prompt();

 // canvas背景全屏
  fitAddon = new FitAddon();
  term.loadAddon(fitAddon);
  fitAddon.fit();

  term.onResize((size) => {
    const { cols, rows } = size;
    webSocket.send(JSON.stringify({
      type: 'resize',
      rows: rows ,
      cols: cols ,
      hp: height.value,
      wp: width.value
    }))
  });

  term.write("正在连接服务器...");
  webSocket = new WebSocket(globalConfig.webSocketUrl);

  webSocket.addEventListener('open', () => {
    term.write("服务器连接成功...\r\n ");
    webSocket.send(JSON.stringify({
      type: 'init',
      serverId: props.data.id
    }))
    setTimeout(resizeScreen,3000);
  });

  webSocket.addEventListener('close', () => {
    term.write("连接已关闭...");
  })
  //此段代码必须要有，通过插件，来接收渲染node服务返回的数据
  const attachAddon = new AttachAddon(webSocket);
  term.loadAddon(attachAddon);

  window.addEventListener("resize", debounce(resizeScreen,1000));
}

const handleRightClick =  (e) => {
  e.preventDefault();
  navigator.clipboard.readText().then((data)=>{
    if(data){
      webSocket.send(data)
    }
  })
}
// 内容全屏显示
function resizeScreen() {
  try {
    fitAddon.fit();
  } catch (e) {
    console.log("e", e.message);
  }
}

function runFakeTerminal() {
  if (term._initialized) {
    return;
  }
  // 初始化
  term._initialized = true;

  term.prompt();
}

onMounted(() => {
  if (webSocket) {
    webSocket.close();
  }
  // wsShell();
  init();
  runFakeTerminal();
  // resizeScreen();
})

onBeforeUnmount(() => {
  if (webSocket) {
    webSocket.close();
  }
  window.removeEventListener("resize", resizeScreen);
})

</script>

<style scoped lang="scss">
.shellContainer{
  ::-webkit-scrollbar-track-piece {
    background-color: transparent !important;
  }
}

</style>