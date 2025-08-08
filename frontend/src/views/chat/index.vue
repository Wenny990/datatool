<script setup>
import { useChatStore } from '@/stores/chatStore'
import { Sender } from 'ant-design-x-vue'
import { ElMessage } from 'element-plus'
import { CopyDocument, Edit, Operation, Refresh } from '@element-plus/icons-vue'
import ChatList from '@/views/chat/ChatList.vue'
import apis from '@/service/apis'
import commonFunction from '@/utils/commonFunction'
import Welcome from '@/views/chat/Welcome.vue'

const { copyText } = commonFunction()

const chatStore = useChatStore()

const { addChatRecord, retryRecord, cancelRecord, removeRecord } = chatStore

const inputVal = ref()

const modelList = ref([])

apis.getChatModelList().then(data => {
  modelList.value = data
  if (!chatStore.model && modelList.value.length > 0) {
    chatStore.model = modelList.value[0].modelCode
  }
})

const chatListRef  = ref(null)

const onSubmit = async () => {
  try {
    // 添加聊天记录
    addChatRecord({
      content: inputVal.value,
      role: 'user',
    })
    nextTick(() => {
      chatListRef.value?.scrollToBottom()
    })

  } catch (error) {
    ElMessage.error('发送失败，点击重试')
  } finally {
    inputVal.value = ''
  }
}

const handleChange = value => {
  inputVal.value = value
}

const handleRetryRecord = record => {
  retryRecord(record) // 重试最后一条记录
}

const handleCopyRecord = record => {}

const handleRemoveRecord = record => {
  removeRecord(record)
}

const handleEditRecord = record => {
  inputVal.value = record.content
}

const handleNotGoodRecord = record => {
  record.good = false
}

const open = ref(false)

const openChange = () => {
  open.value = !open.value
}

onMounted(() => {
  chatStore.isReady = true
})
</script>

<template>
  <div class="flex-center h100">
    <div class="flex-auto">
      <Welcome
        v-show="chatStore.chatRecords.length < 1"
        icon="public/icon/ai.webp"
        title="Hello, 我是你的AI小助手"
        description="我可以帮你写代码、读文件、写作各种创意内容，请把你的任务交给我吧~🙂🙂🙂🚀🚀🚀"
      ></Welcome>
      <div v-if="chatStore.chatRecords.length > 0" class="h100">
        <ChatList :items="chatStore.chatRecords" ref="chatListRef">
          <template #footer="{ item }">
            <div class="flex">
              <el-tooltip content="复制">
                <el-button
                  v-show="item.role === 'ai'"
                  @click="copyText(item.content)"
                  text
                >
                  <el-icon :size="20">
                    <CopyDocument />
                  </el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="重新生成">
                <el-button
                  :disabled="!chatStore.isReady"
                  v-show="item.role === 'user'"
                  @click="handleRetryRecord(item)"
                  text
                >
                  <el-icon :size="20">
                    <Refresh />
                  </el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除">
                <el-button
                  v-show="item.state > 2"
                  @click="handleRemoveRecord(item)"
                  text
                >
                  <svg-icon name="iconfont icon-ashbin" :size="20"> </svg-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑">
                <el-button
                  v-show="item.role === 'user'"
                  @click="handleEditRecord(item)"
                  text
                >
                  <el-icon :size="20">
                    <Edit />
                  </el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="点赞">
                <el-button
                  v-if="!item.good"
                  v-show="item.role === 'ai'"
                  @click="item.good = true"
                  text
                >
                  <svg-icon name="iconfont icon-good" :size="20" />
                </el-button>
                <el-button
                  v-else
                  v-show="item.role === 'ai'"
                  @click="item.good = false"
                  text
                >
                  <svg-icon name="iconfont icon-dianzan" :size="20" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="不喜欢">
                <el-button
                  v-show="item.role === 'ai'"
                  @click="handleNotGoodRecord(item)"
                  text
                >
                  <svg-icon name="iconfont icon-dianzan-xia" :size="20" />
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </ChatList>
      </div>
    </div>
    <div class="px20">
      <Sender
        :value="inputVal"
        submitType="enter"
        placeholder="Shift + Enter 换行，enter 发送"
        @submit="onSubmit"
        @change="handleChange"
        :loading="!chatStore.isReady"
        @cancel="cancelRecord"
      >
        <template #header>
          <Sender.Header
            title="模型"
            :open="open"
            @open-change="openChange"
            closable
          >
            <el-row>
              <el-form-item label="选择模型" class="mb2">
                <el-select v-model="chatStore.model" style="width: 240px">
                  <el-option
                    v-for="item in modelList"
                    :value="item.modelCode"
                    :label="item.modelCode"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-button @click="chatStore.clearChatRecord" class="ml24">
                清空记录
              </el-button>
            </el-row>
          </Sender.Header>
        </template>
        <template #prefix>
          <div class="flex">
            <el-button text :icon="Operation" @click="openChange"></el-button>
          </div>
        </template>
      </Sender>
    </div>
  </div>
</template>

<style lang="scss">
.ant-sender-prefix {
  align-self: center;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
