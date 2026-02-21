<script setup>
import { ref, watch } from 'vue'
import { getAccounts, getStatement } from '../api/banking'

const accounts = ref([])
const selectedAccountId = ref(null)
const statement = ref([])
const loading = ref(false)
const accountsError = ref(null)

const fetchAccounts = async () => {
  accountsError.value = null
  try {
    const { data } = await getAccounts()
    accounts.value = Array.isArray(data) ? data : []
    if (accounts.value.length > 0 && !selectedAccountId.value) {
      selectedAccountId.value = accounts.value[0].id
    }
  } catch (e) {
    accountsError.value = e.response?.data?.message || e.message || 'Erro ao carregar contas'
    accounts.value = []
  }
}

const fetchStatement = async () => {
  if (!selectedAccountId.value) return
  loading.value = true
  try {
    const { data } = await getStatement(selectedAccountId.value)
    statement.value = data
  } catch {
    statement.value = []
  } finally {
    loading.value = false
  }
}

watch(selectedAccountId, fetchStatement)
fetchAccounts()
watch(accounts, (a) => {
  if (a.length > 0 && !selectedAccountId.value) {
    selectedAccountId.value = a[0].id
  }
}, { deep: true })

const formatType = (type) => type === 'DEBIT' ? 'Débito' : 'Crédito'
const formatDate = (d) => d ? new Date(d).toLocaleString('pt-BR') : '-'
</script>

<template>
  <div class="space-y-6">
    <div class="prose prose-invert max-w-none">
      <h2 class="text-xl font-bold text-slate-100">Extrato das Contas</h2>
      <p class="text-slate-400 text-sm">
        Selecione uma conta para visualizar o histórico de transações e validar as transferências.
      </p>
    </div>
    <div>
      <label class="block text-sm text-slate-400 mb-2">Conta</label>
      <p v-if="accountsError" class="text-red-400 text-sm mb-2">{{ accountsError }}</p>
      <select
        v-model="selectedAccountId"
        class="rounded-lg border border-slate-600 bg-slate-800 px-4 py-2 text-slate-100 w-full max-w-xs"
        :disabled="accounts.length === 0"
      >
        <option v-if="accounts.length === 0" value="">Nenhuma conta disponível</option>
        <option v-for="acc in accounts" :key="acc.id" :value="acc.id">
          {{ acc.number }} - {{ acc.holder }} (R$ {{ acc.balance?.toFixed(2) }})
        </option>
      </select>
    </div>
    <div class="overflow-x-auto">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-slate-700">
            <th class="text-left py-3 px-2 text-slate-400">Data</th>
            <th class="text-left py-3 px-2 text-slate-400">Tipo</th>
            <th class="text-right py-3 px-2 text-slate-400">Valor</th>
            <th class="text-right py-3 px-2 text-slate-400">Saldo após</th>
            <th class="text-left py-3 px-2 text-slate-400">Padrão</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="text-slate-500">
            <td colspan="5" class="py-8 text-center">Carregando...</td>
          </tr>
          <tr v-else-if="statement.length === 0" class="text-slate-500">
            <td colspan="5" class="py-8 text-center">Nenhuma transação</td>
          </tr>
          <tr
            v-else
            v-for="tx in statement"
            :key="tx.id"
            class="border-b border-slate-800 hover:bg-slate-800/30"
          >
            <td class="py-2 px-2">{{ formatDate(tx.createdAt) }}</td>
            <td class="py-2 px-2">
              <span :class="tx.type === 'DEBIT' ? 'text-red-400' : 'text-emerald-400'">
                {{ formatType(tx.type) }}
              </span>
            </td>
            <td class="py-2 px-2 text-right">{{ tx.amount?.toFixed(2) }}</td>
            <td class="py-2 px-2 text-right">{{ tx.balanceAfter?.toFixed(2) }}</td>
            <td class="py-2 px-2 text-slate-500">{{ tx.transferType || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
