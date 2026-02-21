# Padrões de Transações Distribuídas

O projeto demonstra três abordagens para consistência em transferências entre contas em um cenário com **banco de dados** e **Apache Kafka**.

## 1. Kafka Native Transactions

### Conceito

Uso de `KafkaTemplate.executeInTransaction()` para garantir **atomicidade** nas publicações no Kafka (exactly-once semantics nas mensagens).

### Fluxo

1. API recebe a solicitação de transferência
2. Publica `TransferCommand` no tópico dentro de uma transação Kafka
3. Consumer processa: débito na conta origem + crédito na conta destino (em `@Transactional`)

### Características

- Atomicidade **apenas nas publicações Kafka**, não entre Kafka e banco
- Consumer pode simular rejeição (valor > 10.000 ou "Forçar erro") sem executar débito/crédito
- `transaction-id-prefix` configurado para habilitar transações no producer

### Código Relevante

```java
eventPublisher.publishInTransaction(Topics.KAFKA_NATIVE_TRANSFER, correlationId, command);
```

O consumer verifica condições de falha antes de debitar/creditar, evitando redelivery desnecessário.

---

## 2. Saga Pattern (Coreografia)

### Conceito

Fluxo distribuído em etapas independentes, com **compensação** em caso de falha em alguma etapa.

### Fluxo

1. **Débito** na conta origem + publicação de `DebitCompletedEvent`
2. Consumer recebe e tenta **crédito** na conta destino
3. Se valor > 10.000 ou "Forçar erro": publica `CompensationRequiredEvent` (sem crédito)
4. Consumer de compensação executa **estorno** na conta origem

### Características

- **Compensação** via evento dedicado
- **Idempotência** no processamento de compensação (verificação de step 7 para evitar estornos duplicados)
- Sem `throw` após publicar compensação, para permitir commit do offset e evitar redelivery em loop

### Diagrama de Sequência (Fluxo Normal vs Compensação)

```
Fluxo normal:
API → Débito → Kafka(DebitCompleted) → Consumer → Crédito → Fim

Fluxo com compensação:
API → Débito → Kafka(DebitCompleted) → Consumer → Kafka(CompensationRequired) → Consumer → Estorno → Fim
```

---

## 3. Transactional Outbox Pattern

### Conceito

Garantia de **atomicidade** entre alterações no banco e publicação de eventos, usando uma tabela `outbox` na mesma transação.

### Fluxo

1. Em **uma transação JPA**: débito + crédito + insert na tabela `outbox`
2. Worker (scheduler) lê registros com `published_at IS NULL`
3. Publica no Kafka e atualiza `published_at`

### Características

- At-most-once ou at-least-once, dependendo da estratégia do relay
- Simula o comportamento de **Debezium** (CDC) com polling
- Em caso de "Forçar erro" ou valor > 10.000: exceção antes do commit, **rollback total** (débito, crédito e outbox não são persistidos)

### Estrutura da Tabela Outbox

```sql
outbox (id, aggregate_type, aggregate_id, event_type, payload, created_at, published_at)
```

---

## Comparativo dos Padrões

| Padrão | Atomicidade | Compensação | Complexidade |
|--------|-------------|-------------|--------------|
| Kafka Native | Apenas Kafka | N/A (rejeita antes de processar) | Baixa |
| Saga | Eventual | Explícita via eventos | Média |
| Outbox | DB + outbox na mesma transação | Rollback da transação | Média |
