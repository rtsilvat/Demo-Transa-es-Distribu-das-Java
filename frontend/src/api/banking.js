import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

export const getAccounts = () => api.get('/accounts')
export const getStatement = (accountId, limit = 50) =>
  api.get(`/accounts/${accountId}/statement`, { params: { limit } })
export const transferKafkaNative = (data) => api.post('/transfers/kafka-native', data)
export const transferSaga = (data) => api.post('/transfers/saga', data)
export const transferOutbox = (data) => api.post('/transfers/outbox', data)
export const getTransferLogs = (correlationId) =>
  api.get('/transfers/logs', { params: { correlationId } })
