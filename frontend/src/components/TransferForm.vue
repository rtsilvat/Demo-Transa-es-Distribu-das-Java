<script setup>
import { ref, computed } from 'vue'
import { getAccounts } from '../api/banking'

const props = defineProps({
  showForceError: { type: Boolean, default: true },
  submitLabel: { type: String, default: 'Transferir' }
})

const emit = defineEmits(['submit'])

const accounts = ref([])
const loading = ref(false)
const error = ref(null)
const fromAccountId = ref(null)
const toAccountId = ref(null)
const amount = ref('')
const forceError = ref(false)

const fetchAccounts = async () => {
  loading.value = true
  error.value = null
  try {
    const { data } = await getAccounts()
    accounts.value = Array.isArray(data) ? data : []
    if (accounts.value.length >= 2) {
      fromAccountId.value = accounts.value[0].id
      toAccountId.value = accounts.value[1].id
    } else if (accounts.value.length === 1) {
      fromAccountId.value = accounts.value[0].id
      toAccountId.value = accounts.value[0].id
    }
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Erro ao carregar contas'
    accounts.value = []
  } finally {
    loading.value = false
  }
}

fetchAccounts()

const valid = computed(() =>
  fromAccountId.value && toAccountId.value && amount.value && parseFloat(amount.value) > 0
)

const handleSubmit = () => {
  if (!valid.value) return
  emit('submit', {
    fromAccountId: fromAccountId.value,
    toAccountId: toAccountId.value,
    amount: parseFloat(amount.value),
    forceError: forceError.value
  })
}

const resetForm = () => {
  amount.value = ''
  forceError.value = false
}

defineExpose({ resetForm })
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-4">
    <div v-if="loading" class="text-slate-400">Carregando contas...</div>
    <div v-else-if="error" class="text-red-400 text-sm">{{ error }}</div>
    <div v-else-if="accounts.length === 0" class="text-amber-400 text-sm">Nenhuma conta cadastrada.</div>
    <template v-else>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Conta origem</label>
        <select
          v-model="fromAccountId"
          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2 text-slate-100"
        >
          <option v-for="acc in accounts" :key="acc.id" :value="acc.id">
            {{ acc.number }} - {{ acc.holder }} (R$ {{ acc.balance?.toFixed(2) }})
          </option>
        </select>
      </div>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Conta destino</label>
        <select
          v-model="toAccountId"
          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2 text-slate-100"
        >
          <option v-for="acc in accounts" :key="acc.id" :value="acc.id">
            {{ acc.number }} - {{ acc.holder }}
          </option>
        </select>
      </div>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Valor (R$)</label>
        <input
          v-model="amount"
          type="number"
          step="0.01"
          min="0.01"
          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2 text-slate-100"
        />
      </div>
      <div v-if="showForceError" class="flex items-center gap-2">
        <input
          v-model="forceError"
          type="checkbox"
          id="forceError"
          class="rounded border-slate-600"
        />
        <label for="forceError" class="text-sm text-slate-400">
          Forçar erro (demonstrar rollback/compensação)
        </label>
      </div>
      <button
        type="submit"
        :disabled="!valid"
        class="rounded-lg bg-emerald-600 px-6 py-2 font-medium text-white hover:bg-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {{ submitLabel }}
      </button>
    </template>
  </form>
</template>
