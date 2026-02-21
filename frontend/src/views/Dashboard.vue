<script setup>
import { ref } from 'vue'
import TransferKafkaNative from '../components/TransferKafkaNative.vue'
import TransferSaga from '../components/TransferSaga.vue'
import TransferOutbox from '../components/TransferOutbox.vue'
import StatementView from '../components/StatementView.vue'

const activeTab = ref('kafka-native')
const tabs = [
  { id: 'kafka-native', label: 'Kafka Native', component: TransferKafkaNative },
  { id: 'saga', label: 'Saga', component: TransferSaga },
  { id: 'outbox', label: 'Outbox', component: TransferOutbox },
  { id: 'statement', label: 'Extrato', component: StatementView }
]
</script>

<template>
  <div class="space-y-6">
    <div class="flex border-b border-slate-700">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="activeTab = tab.id"
        :class="[
          'px-6 py-3 font-medium transition-colors',
          activeTab === tab.id
            ? 'text-emerald-400 border-b-2 border-emerald-500'
            : 'text-slate-400 hover:text-slate-200'
        ]"
      >
        {{ tab.label }}
      </button>
    </div>
    <div class="rounded-xl border border-slate-700 bg-slate-900/50 p-6">
      <component :is="tabs.find(t => t.id === activeTab).component" />
    </div>
  </div>
</template>
