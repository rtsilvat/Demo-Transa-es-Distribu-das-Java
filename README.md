# Banking Demo - Transações Distribuídas

Demo de demonstração de transações distribuídas em Java com Kafka e Postgres, apresentando 3 estratégias de consistência em transferências entre contas.

## Tecnologias

- **Backend:** Java 17, Spring Boot 3.5 (compatível com 4.x), Spring Kafka, Spring Data JPA, Virtual Threads
- **Frontend:** Vue.js 3 (Composition API), Tailwind CSS, Pinia, Axios
- **Infraestrutura:** Docker, Kafka (KRaft), PostgreSQL

## Estratégias Implementadas

1. **Kafka Native Transactions** - `executeInTransaction` para atomicidade exactly-once nas publicações
2. **Saga Pattern (Coreografia)** - Débito → evento → crédito, com compensação se valor > R$ 10.000
3. **Transactional Outbox** - Persistência atômica (débito + crédito + outbox) + relay para Kafka

## Pré-requisitos

- Java 17+
- Node.js 18+
- Docker e Docker Compose
- Maven 3.9+ (ou use `./mvnw`)

## Execução

### Opção 1: Docker Compose (recomendado)

```bash
docker-compose up -d
```

- Backend: http://localhost:8080
- Frontend: http://localhost:8081
- Postgres: localhost:8432 (porta 8432 para evitar conflito)
- Kafka: localhost:9092

### Opção 2: Desenvolvimento local (frontend com hot reload)

1. **Subir infraestrutura e backend:**
```bash
docker-compose up -d postgres kafka backend
```

2. **Frontend em modo dev:**
```bash
cd frontend
npm install
npm run dev
```

O frontend roda em http://localhost:5173 com proxy para a API em :8080.

## API

- `GET /api/accounts` - Lista contas
- `GET /api/accounts/{id}/statement` - Extrato da conta
- `POST /api/transfers/kafka-native` - Transferência via Kafka Native
- `POST /api/transfers/saga` - Transferência via Saga
- `POST /api/transfers/outbox` - Transferência via Outbox
- `GET /api/transfers/logs?correlationId=xxx` - Log de passos da transferência

### Exemplo de requisição de transferência

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 100.00,
  "forceError": false
}
```

- `forceError: true` - Força erro/compensação na Saga (e simulação no Kafka Native)
- Valor > 10.000 na Saga - Dispara compensação automática

## Estrutura do Projeto

```
├── src/main/java/com/banking/demo/
│   ├── domain/          # Entidades e regras
│   ├── application/     # Ports, Services, DTOs
│   ├── infrastructure/  # JPA, Kafka, Scheduler
│   └── interfaces/      # REST Controllers
├── frontend/            # Vue.js 3 + Tailwind
├── docker-compose.yml
└── Dockerfile
```

## Documentação para Entrevista

A pasta [`/docs`](docs/) contém documentação técnica detalhada sobre as técnicas e padrões aplicados no projeto, preparada para apresentação em processos seletivos.

## Seeds

Ao iniciar, as contas são criadas automaticamente:
- Conta A (001): R$ 50.000
- Conta B (002): R$ 30.000
- Conta C (003): R$ 10.000
