<script setup>
import { ref, watch, onUnmounted } from 'vue'
import { getTransferLogs } from '../api/banking'

const props = defineProps({
  correlationId: { type: String, default: null }
})

const logs = ref([])
const polling = ref(null)

const fetchLogs = async () => {
  if (!props.correlationId) return
  try {
    const { data } = await getTransferLogs(props.correlationId)
    logs.value = data
  } catch {
    // ignore
  }
}

watch(() => props.correlationId, (id) => {
  logs.value = []
  if (polling.value) clearInterval(polling.value)
  if (id) {
    fetchLogs()
    polling.value = setInterval(fetchLogs, 1500)
  }
}, { immediate: true })

onUnmounted(() => {
  if (polling.value) clearInterval(polling.value)
})
</script>

<template>
  <div class="space-y-2">
    <h3 class="font-medium text-slate-300">Log da transferência</h3>
    <div v-if="logs.length === 0 && !correlationId" class="text-slate-500 text-sm">
      Execute uma transferência para ver o log.
    </div>
    <div v-else-if="logs.length === 0" class="text-slate-500 text-sm">
      Aguardando registros...
    </div>
    <ul v-else class="space-y-1 font-mono text-sm">
      <li
        v-for="(log, i) in logs"
        :key="i"
        class="flex gap-3 rounded bg-slate-800/50 px-3 py-2"
      >
        <span class="text-emerald-500 font-bold">{{ log.step }}.</span>
        <span class="text-slate-300">{{ log.message }}</span>
      </li>
    </ul>
  </div>
</template>
