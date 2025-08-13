<template>
  <div class="mapper-container h100">
    <div class="flex h100 mapper-main">
      <div class="flex-auto flex-center">
        <div>
          <el-text> 源端（未匹配） </el-text>
        </div>
        <div class="column-list flex-auto">
          <div v-for="item in rColumns" :key="item">
            <el-text>{{ item }}</el-text>
          </div>
        </div>
      </div>
      <div class="flex-auto flex-center">
        <div>
          <el-text> 目标端（未匹配） </el-text>
        </div>

        <div class="column-list flex-auto">
          <div v-for="item in wColumns" :key="item">
            <el-text>{{ item }}</el-text>
          </div>
        </div>
      </div>
      <div class="flex-auto flex-center">
        <div>
          <el-text> 已匹配 </el-text>
        </div>
        <div class="column-list flex-auto">
          <div
            v-for="item in job.mapperList"
            :key="item"
            style="
              text-overflow: ellipsis;
              white-space: nowrap;
              overflow: hidden;
            "
            :title="item.left + ' ====》 ' + item.right"
          >
            <el-text>{{ item.left + ' ====》 ' + item.right }}</el-text>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import apis from '@/service/apis'

const props = defineProps({
  job: {
    type: Object,
    required: true,
    default: () => {},
  },
})

const { job } = toRefs(props)

const wColumns = ref([])
const rColumns = ref([])

function matchAndRemove(arr1, arr2) {
  let result = []
  for (let i = 0; i < arr1.length; i++) {
    for (let j = 0; j < arr2.length; j++) {
      if (arr1[i].toLowerCase() == arr2[j].toLowerCase()) {
        result.push({
          left: arr1[i],
          right: arr2[j],
        })
        arr1.splice(i, 1)
        i--
        arr2.splice(j, 1)
        break
      }
    }
  }
  return result
}

const mapper = async () => {
  if (job.value.reader.type == 1) {
    const data = await apis.parseSql({
      repoId: job.value.reader.repoId,
      sql: job.value.reader.querySql,
    })
    const selectItems = data.selectList
    rColumns.value = selectItems.map(t => t.alias)
  } else {
    rColumns.value = [...job.value.reader.columns]
  }
  wColumns.value = [...job.value.writer.columns]

  job.value.mapperList = matchAndRemove(rColumns.value, wColumns.value)
}

onMounted(() => {
  mapper()
})
</script>

<style scoped lang="scss">
.mapper-main {
  gap: 24px;
}

.column-list {
  border: 1px solid var(--el-border-color);
  overflow-y: auto;
  padding: 12px;
  line-height: 1.4;
}

.opt {
  width: 120px;
  align-items: center;
  justify-content: center;
}
</style>
