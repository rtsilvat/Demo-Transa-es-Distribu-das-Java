# Qualidade e Resiliência

## Tratamento de Erros

### GlobalExceptionHandler (@RestControllerAdvice)

Tratamento centralizado de exceções com DTO padronizado:

| Exceção | HTTP Status | Uso |
|---------|-------------|-----|
| `IllegalArgumentException` | 400 Bad Request | Dados inválidos, conta não encontrada |
| `IllegalStateException` | 422 Unprocessable Entity | Regras de negócio (ex.: saldo insuficiente, transferência rejeitada) |
| `MethodArgumentNotValidException` | 400 | Falhas de validação Bean Validation |
| `Exception` | 500 | Erros não mapeados |

### Estrutura do ErrorResponse

```json
{
  "timestamp": "2026-02-21T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente",
  "path": "/api/transfers/saga",
  "details": []
}
```

---

## Validação

- **Bean Validation** em DTOs (`@NotNull`, `@DecimalMin`)
- Validações de domínio em entidades (`Account.debit`, `Account.credit`)
- Mensagens claras para o frontend

---

## Idempotência

No **processamento de compensação** da Saga:

```java
if (transferLogRepository.existsByCorrelationIdAndStep(correlationId, 7)) {
    return;  // Já compensou, ignora redelivery
}
```

Evita múltiplos estornos quando o mesmo evento é reprocessado pelo Kafka.

---

## Evitando Redelivery Indesejado

No Saga, ao detectar condição de compensação:
- **Antes:** `throw` após publicar evento → offset não commitado → redelivery → múltiplas compensações
- **Depois:** `return` após publicar → offset commitado → processamento único

---

## CORS

Configuração em `WebConfig` para permitir o frontend (ex.: `http://localhost:5173`):

```java
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:5173")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*");
```

---

## Logging e Rastreabilidade

- `TransferLog` com `correlationId` para rastrear cada transferência
- Passos numerados (1, 2, 3...) para debug e auditoria
- Logs em nível adequado (INFO, WARN, ERROR) nos consumers

---

## Infraestrutura como Código

- **Docker Compose** com Postgres, Kafka (KRaft) e backend
- **Dockerfile** multi-stage (build com JDK, runtime com JRE)
- Healthchecks nos serviços
- Porta 8432 para Postgres para evitar conflito com instalação local
