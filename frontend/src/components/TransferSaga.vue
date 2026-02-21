<script setup>
import { ref } from 'vue'
import TransferForm from './TransferForm.vue'
import TransferLog from './TransferLog.vue'
import { transferSaga } from '../api/banking'

const correlationId = ref(null)
const loading = ref(false)
const error = ref(null)
const formRef = ref(null)

const handleSubmit = async (data) => {
  loading.value = true
  error.value = null
  correlationId.value = null
  try {
    const { data: res } = await transferSaga(data)
    correlationId.value = res.correlationId
    formRef.value?.resetForm?.()
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Erro na transferência'
    if (e.response?.data?.correlationId) correlationId.value = e.response.data.correlationId
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="prose prose-invert max-w-none">
      <h2 class="text-xl font-bold text-slate-100">Saga Pattern (Coreografia)</h2>
      <p class="text-slate-400 text-sm leading-relaxed">
        Fluxo distribuído em etapas: o débito na conta origem é persistido e um evento é publicado.
        O consumer tenta creditar na conta destino. Se o valor for maior que R$ 10.000 ou "Forçar erro"
        estiver ativado, simula-se um erro de conformidade e um evento de compensação estorna o débito.
      </p>
    </div>
    <div class="grid md:grid-cols-2 gap-8">
      <div>
        <h3 class="font-medium text-slate-300 mb-3">Nova transferência</h3>
        <TransferForm ref="formRef" :show-force-error="true" @submit="handleSubmit" />
        <p v-if="loading" class="mt-2 text-amber-400 text-sm">Processando...</p>
        <p v-if="error" class="mt-2 text-red-400 text-sm">{{ error }}</p>
      </div>
      <div>
        <TransferLog :correlation-id="correlationId" />
      </div>
    </div>
  </div>
</template>
